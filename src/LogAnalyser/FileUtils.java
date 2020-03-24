package LogAnalyser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 文件操作代码
 *
 * @author cn.outofmemory
 * @date 2013-1-7
 */
public class FileUtils {
    /**
     * 将文本文件中的内容读入到buffer中
     * @param buffer buffer
     * @param filePath 文件路径
     * @throws IOException 异常
     * @author cn.outofmemory
     * @date 2013-1-7
     */
    public void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // 读取第一行
        while (line != null) { // 如果 line 为空说明读完了
            buffer.append(line); // 将读到的内容添加到 buffer 中
            buffer.append("\n"); // 添加换行符
            line = reader.readLine(); // 读取下一行
        }
        reader.close();
        is.close();
    }

    /**
     * 读取文本文件内容
     * @param filePath 文件所在路径
     * @return 文本内容
     * @throws IOException 异常
     * @author cn.outofmemory
     * @date 2013-1-7
     */
    public String readFile(String filePath) throws IOException {
        FileUtils fileUtils = new FileUtils();
        StringBuffer sb = new StringBuffer();
        fileUtils.readToBuffer(sb, filePath);
        return sb.toString();
    }

    /**
     * 根据不同的文件类型获取对应的日志内容
     * @param localPath 本地zip文件路径
     * @param typeString 日志的类型，NIM的，还是NRTC的，还是RTS的
     * @return
     */
    public String getLogString(LogAnalyse logAnalyse,String localPath, String typeString){

        String res = null;
        FileUtils fileUtils = new FileUtils();

        String localFoldPath = localPath.substring(0,localPath.lastIndexOf("."));

        ArrayList<String> fileNameList = new ArrayList<String>();
        logAnalyse.getAllFileName(localFoldPath,fileNameList);

        for (String fileName : fileNameList){
            // 根据文件名判断日志的类型
            if (fileName.contains(typeString)){
                String logFilePath = localFoldPath + "\\" + fileName;
                try {
                    res =  fileUtils.readFile(logFilePath);
                }catch (Exception e){
                    e.fillInStackTrace();
                }
            }
        }

        return res;
    }
}