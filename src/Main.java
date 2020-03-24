import LogAnalyser.FileType;
import LogAnalyser.LogAnalyse;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {

//        String downloadURL = "https://nim-nosdn.netease.im/MTExNzQ4NzM=/bmltYV8xNjgzMDc2MjY0Ml8xNTgxNjQ5OTE3NTkxXzk3MWYwNDQyLWUzYWUtNDQ0Mi1hYTJjLTQzYmY1ZGE3M2M3ZA==";
//        String downloadURL = "https://nim-nosdn.netease.im/MTExNzQ4NzM=/bmltYV8xNjY1MjcwNjMxN18xNTg0Mjc2ODkwODY1XzU1Y2ZkMDNiLTJmNjItNDcyMC1hMGM5LWM5Y2IzMmRiMzU0Zg==";
        String downloadURL = "https://nim-nosdn.netease.im/MTExNzQ4NzM=/bmltYV8xNjY2MDc1NDExMV8xNTgxNzY0Mzg5NjUyX2U1YTM0ZGE1LTJjZDEtNGY0Yi1iYTJjLTU1ZTMwYjcwYzhjNA==";
        String localPath = "E:\\日志下载\\测试日志.rar";
        LogAnalyse logAnalyse = new LogAnalyse();
        // 下载文件
        logAnalyse.downloadNet(downloadURL,localPath);
        // 获取文件类型
        String type = FileType.getFileType(localPath);

        switch (type){
            case "zip" : {
                System.out.println("Get the Android Log Success,Type is ZIP");
                logAnalyse.unzip(localPath);
//                logAnalyse.analyseAndroidLog(localPath);
                break;
            }
            case "gz" : {
                System.out.println("Get the iOS Log Success,Type is GZ");
                logAnalyse.unGz(localPath);
                break;
            }
        }
    }
}
