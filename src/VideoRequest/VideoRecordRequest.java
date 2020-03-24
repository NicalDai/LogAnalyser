package VideoRequest;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VideoRecordRequest {

    public static void start(String accid) throws IOException {

        

        DefaultHttpClient httpClient = new DefaultHttpClient();
        String url = "https://api.netease.im/nimserver/msg/sendBatchMsg.action";

        HttpPost httpPost = new HttpPost(url);

        String appKey = "45c6af3c98409b18a84451215d0bdd6e";
        String appSecret = "37db56012b60";

        String nonce =  "12345";
        String curTime = String.valueOf((new Date()).getTime()/1000L);
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce ,curTime);//参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", appKey);
        httpPost.addHeader("Nonce", nonce);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fromAccount","210");


        JSONObject body = new JSONObject();
        body.put("msg","测试测试测试");

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

//        String wsSecret= encoderByMd5With32Bit(secretKey + "/" + bucketName + "/" + objectKey + wsTime);

        nvps.add(new BasicNameValuePair("deleteMsgid", "182403459746627626"));
        nvps.add(new BasicNameValuePair("timetag","1574315398498" ));
        nvps.add(new BasicNameValuePair("type", "8"));
        nvps.add(new BasicNameValuePair("from", "nical"));
        nvps.add(new BasicNameValuePair("to", "2718023356"));
        nvps.add(new BasicNameValuePair("msg", "这条消息是服务器撤回的"));
        nvps.add(new BasicNameValuePair("ignoreTime", "1"));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
        System.out.println(nvps.toString());
        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);
        // 打印执行结果
        System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
        System.out.print((new Date()).getTime() + "\n");

    }
}
