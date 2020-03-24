package LogAnalyser;

import com.alibaba.fastjson.JSONArray;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.net.HttpURLConnection;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.GZIPInputStream;

public class LogAnalyse {

    private int fileLength;


    /**
     * 下载日志文件
     * @param downloadURL 日志的URL
     * @param localPath 需要存放在的本地路径
     * @throws MalformedURLException
     */
    public boolean downloadNet(String downloadURL,String localPath) throws MalformedURLException {
        // 下载网络文件

        int bytesum = 0;
        int byteread;

        if (downloadURL == null){
            LogBase.print("Please input URL");
            return false;
        }
        if ("".equals(downloadURL) || !downloadURL.contains("http")){
            LogBase.print("URL is Invalid! ");
            return false;
        }
        URL url = new URL(downloadURL);
        try {
            HttpURLConnection urlcon=(HttpURLConnection)url.openConnection();
            fileLength=urlcon.getContentLength();
            LogBase.print(System.currentTimeMillis()+"\n");
            LogBase.print("获取到日志文件总大小： "+fileLength+" byte\n");
        }catch (Exception e){
            return false;
        }
        // 判断本地文件路径是否存在
        File targetFile = new File(localPath.substring(0,localPath.indexOf(".")));
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }

        try {
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream(localPath);
            byte[] buffer = new byte[1204];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                fs.write(buffer, 0, byteread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 解压Zip文件
     * @param zipFilePaht ZIP文件的路径，默认解压在当前路径的同名文件夹下
     */
    public void unzip(String zipFilePaht) {
        LogBase.print(System.currentTimeMillis()+"\n");
        try {
            ZipFile zipFile = new ZipFile(zipFilePaht);
            String afterUnzipPaht = zipFilePaht.substring(0,zipFilePaht.lastIndexOf("."));
            zipFile.extractAll(afterUnzipPaht);
            LogBase.print("Unzip Android Log Success! The path is " + afterUnzipPaht);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解压GZ文件
     * @param inFileName GZ文件的路径，默认解压在当前路径的同名文件夹下
     */
    public void unGz(String inFileName) {

        LogBase.print(System.currentTimeMillis()+"\n");
        String outFileName = inFileName.substring(0,inFileName.lastIndexOf(".")) + "\\nim_iOS.log";

        try {

            LogBase.print("Opening the compressed file.");
            GZIPInputStream in = null;
            try {
                in = new GZIPInputStream(new FileInputStream(inFileName));
            } catch(FileNotFoundException e) {
                LogBase.print("File not found. " + inFileName);
                System.exit(1);
            }

            LogBase.print("Open the output file.");

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(outFileName);
            } catch (FileNotFoundException e) {
                LogBase.print("Could not write to file. " + outFileName);
                System.exit(1);
            }

            LogBase.print("Transfering bytes from compressed file to the output file.");
            byte[] buf = new byte[1024];
            int len;
            while((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            LogBase.print("Closing the file and stream");
            in.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    /**
     * 获取路径下的所有的文件名
     * @param path 本地文件夹路径
     * @param fileNameList 文件名结果
     */
    public void getAllFileName(String path, ArrayList<String> fileNameList) {
        boolean flag = false;
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                fileNameList.add(tempList[i].getName());
            }
            if (tempList[i].isDirectory()) {
                getAllFileName(tempList[i].getAbsolutePath(),fileNameList);
            }
        }
        return;
    }

    /**
     * 安卓日志分析方法入口
     * @param localPath 本地日志路径
     * @param startDateString    检索开始时间String
     * @param endDateString      检索结束时间String
     * @param extraKeyWordString 个性化额外的检索文本，按逗号分隔
     */
    public void analyseAndroidLog(String localPath,String startDateString,String endDateString,String extraKeyWordString){

        FileUtils fileUtils = new FileUtils();

        Date startDate = getDate(startDateString,"yyyy MMdd HHmm");
        Date endDate = getDate(endDateString,"yyyy MMdd HHmm");

        if (startDate != null && endDate != null){
            if (startDate.after(endDate)){
                LogBase.print("时间顺序错误");
                Date date = startDate;
                startDate = endDate;
                endDate = date;
            }
        }
        // 初始化日志字段
        JSONArray keyWordsAfterAdd = addKeyWords(initLogKeyWordsAOS(),extraKeyWordString);
        String logNIMString;
        String logNrtcEngine;
        String logNrtcSdk;
        String logRTSNet;
        logNIMString = fileUtils.getLogString(this,localPath,"nim_sdk");
        logNrtcEngine = fileUtils.getLogString(this,localPath,"nrtc_engine");
        logNrtcSdk = fileUtils.getLogString(this,localPath,"nrtc_sdk");
        logRTSNet = fileUtils.getLogString(this,localPath,"rts_net");

        LogBase.print("\n\n----------------IM Log-----------------\n");
        for (Object keyWord : keyWordsAfterAdd){
            findAndPringLog(logNIMString,keyWord.toString(),startDate,endDate);
        }
        LogBase.print("\n\n----------------NRTC_Engine Log-----------------\n");
        for (Object keyWord : keyWordsAfterAdd){
            findAndPringLog(logNrtcEngine,keyWord.toString(),startDate,endDate);
        }
        LogBase.print("\n\n----------------NRTC_SDK Log-----------------\n");
        for (Object keyWord : keyWordsAfterAdd){
            findAndPringLog(logNrtcSdk,keyWord.toString(),startDate,endDate);
        }
        LogBase.print("\n\n----------------RTS_NET Log-----------------\n");
        for (Object keyWord : keyWordsAfterAdd){
            findAndPringLog(logRTSNet,keyWord.toString(),startDate,endDate);
        }

    }
    /**
     * iOS日志分析方法入口
     * @param localPath 本地日志路径
     * @param startDateString    检索开始时间String
     * @param endDateString      检索结束时间String
     * @param extraKeyWordString 个性化额外的检索文本，按逗号分隔
     */
    public void analyseIOSLog(String localPath,String startDateString,String endDateString,String extraKeyWordString){
        FileUtils fileUtils = new FileUtils();

        // 初始化日志字段
        JSONArray keyWordsAfterAdd = addKeyWords(initLogKeyWordsIOS(),extraKeyWordString);
        //iOS 日志内容
        String logIOSString;

        Date startDate = getDate(startDateString,"yyyy MMdd HHmm");
        Date endDate = getDate(endDateString,"yyyy MMdd HHmm");

        if (startDate != null && endDate != null){
            if (startDate.after(endDate)){
                LogBase.print("时间顺序错误");
                Date date = startDate;
                startDate = endDate;
                endDate = date;
            }
        }
        logIOSString = fileUtils.getLogString(this,localPath,"nim_iOS");

        LogBase.print("\n\n----------------IOS Log-----------------\n");

        for (Object keyword : keyWordsAfterAdd){
            findAndPringLog(logIOSString,keyword.toString(),startDate,endDate);
        }
    }

    /**
     * 安卓端初始化日志关键词
     * @return  返回初始化完成的关键词JSONArray
     */
    private JSONArray initLogKeyWordsAOS(){
        JSONArray keyWords = new JSONArray();
        keyWords.add(0,"**** APPKEY: ");
        keyWords.add(1,"core: on SDK login success");
        keyWords.add(2,"onUserJoin");
        keyWords.add(3,"AVChatManager: join  channel success");
        keyWords.add(4,"Device: {");
        keyWords.add(5,"[VOIP]id");
        keyWords.add(6,"SID");
        return keyWords;
    }
    /**
     * IOS端初始化日志关键词
     * @return 返回初始化完成的关键词JSONArray
     */
    private JSONArray initLogKeyWordsIOS(){
        JSONArray keyWords = new JSONArray();
        keyWords.add(0,"app key");
        keyWords.add(1,"login success");
        keyWords.add(2,"[NVS] Device Info:");
        keyWords.add(3,"joined channel");
        keyWords.add(4,"joined meeting");
        keyWords.add(5,"Starting nvs with channel id");
        keyWords.add(6,"SID");
        return keyWords;
    }
    /**
     * 日志检索方法入口
     * @param logString 需要检索的日志完整的String
     * @param keyWord   传入的检索关键词
     * @param startDate 检索的开始时间
     * @param endDate   检索的结束时间
     */
    private void findAndPringLog(String logString,String keyWord,Date startDate,Date endDate){
        int index = logString.indexOf(keyWord);
        int indexStart;
        int indexEnd;
        int indexCursor = index;
        if (index == -1){
            return;
        }
        // 通过寻找\n的位置截断每行日志
        while (indexCursor < logString.length()){
            indexCursor = logString.indexOf(keyWord,indexCursor);
            while (indexCursor > 0){
                if (logString.indexOf('\n',indexCursor) - indexCursor == 0){
                    break;
                }else{
                    indexCursor--;
                }
            }
            indexStart = indexCursor;
            indexEnd = logString.indexOf('\n',indexCursor + 1);

            // 通过每行日志获取时间戳判断是否需要打印。因为iOS日志的时间戳混乱，这边必须每行进行判断
            if (indexStart < indexEnd && indexStart > 0){
//                String log = "03-18 16:13:12.102: [ui]TransExec: execute Transaction: [id: 25,  method: EventSubscribeService/subscribeEvent] exception\n";
                String log = logString.substring(indexStart,indexEnd);
                Date logDate = getTimeFromLog(log);

                if (isNeedPrint(logDate,startDate,endDate))
                LogBase.print(logString.substring(indexStart,indexEnd));
            }else {
                break;
            }
            indexCursor = indexEnd + 1;
        }
    }

    /**
     * 通过日期的字符串获取日期对象
     * @param dateSource 日期字符串
     * @param formant  转化模板
     * @return 日期对象
     */
    private Date getDate(String dateSource,String formant){
        //创建SimpleDateFormat对象实例并定义好转换格式
        if (dateSource == null){
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(formant);
        System.out.println("把当前时间转换成字符串：" + sdf.format(new Date()));

        Date date = null;
        try {
            // 注意格式需要与上面一致，不然会出现异常
            date = sdf.parse(dateSource);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        System.out.println("字符串转换成时间:" + date);
        return date;
    }

    /**
     * 手动新增检索文本条目
     * @param keywords  当前已有的检索文本库
     * @param extraKeyWords 新增的检索文本
     * @return 添加完成的结果
     */
    private JSONArray addKeyWords(JSONArray keywords,String extraKeyWords){
        int index = 0;
        int indexEnd;
        int i = keywords.size();
        while (index < extraKeyWords.length()){
            indexEnd = extraKeyWords.indexOf(',',index + 1);
            if (indexEnd < 0){
                keywords.add(i,extraKeyWords.substring(index));
                break;
            }
            keywords.add(i++,extraKeyWords.subSequence(index,indexEnd));
            index = indexEnd + 1;
        }
        return keywords;
    }

    /**
     * 通过某行日志获取日志对应的时间
     * @param log 日志文本
     * @return 这行日志对应的时间
     */
    private Date getTimeFromLog(String log){
        Date date = null;
        if (log.indexOf('[') == 1){
            String time = log.indexOf('[') < log.indexOf(']')? log.substring(log.indexOf('[') + 1,log.indexOf(']') - 1):null;
            date = getDate(time,"yyyy-MM-dd HH:mm:ss:SSS");
            return date;
        }
        if ("2".equals(log.substring(0,1))){
            String time = log.substring(0,23);
            date = getDate(time,"yyyy-MM-dd HH:mm:ss:SSS");
        }else {
            String time = log.substring(0,18);
            date = getDate("2020-"+time,"yyyy-MM-dd HH:mm:ss.SSS" );
        }
        return date;
    }

    /**
     * 判断这行日志是否需要打印
     * @param logDate 日志对应的时间
     * @param startDate 开始需要打印的时间
     * @param endDate 结束打印时间
     * @return  YES or NO
     */
    private Boolean isNeedPrint(Date logDate,Date startDate,Date endDate){

        if (logDate == null){
            return true;
        }

        if (startDate == null){
            if (endDate == null){
                return true;
            }else {
                return logDate.before(endDate);
            }
        }else {
            if (endDate == null){
                return logDate.after(startDate);
            }else {
                return logDate.after(startDate)&&logDate.before(endDate);
            }
        }
    }
}
