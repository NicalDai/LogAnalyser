import LogAnalyser.FileType;
import LogAnalyser.LogAnalyse;
import LogAnalyser.LogBase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GuiMain extends JFrame {
    private static final long serialVersionUID=1L;
    private JPanel jContentPane=null;   // 主Panel
    private JButton AnalyseButton;      // 开始分析
    private JTextField urlTextField;// 输入URL
    private JTextArea textArea1;    // 输出日志分析结果
    private JScrollPane scrollPane;
//    private JProgressBar progressBar1;
    private JTextField startTimeTextField;
    private JTextField extraKeyWords;
    private JTextField endTimeTextField;


    private JScrollPane getScrollPane(){
        if (scrollPane == null){
            scrollPane = new JScrollPane();
        }
        getoutputLog();
        scrollPane.setViewportView(textArea1);
        return scrollPane;
    }

    private JTextField getStartTimeTextField(){
        if (startTimeTextField == null){
            startTimeTextField = new JTextField();
        }
        Date d = new Date();
        DateFormat d1 = new SimpleDateFormat("yyyy MMdd HHmm");//24小时制时间
        System.out.println("默认时间： " + d1.format(d));
        startTimeTextField.setText(d1.format(new Date((d.getTime() -  2 * 24 * 60 * 60 * 1000))));
        return startTimeTextField;
    }

    private JTextField getEndTimeTextField(){
        if (endTimeTextField == null){
            endTimeTextField = new JTextField();
        }
        Date d = new Date();
        DateFormat d1 = new SimpleDateFormat("yyyy MMdd HHmm");//24小时制时间
        System.out.println("默认时间： " + d1.format(d));
        endTimeTextField.setText(d1.format(d));
        return endTimeTextField;
    }

//    private JProgressBar getJProgressBar(){
//        if (progressBar1 == null){
//            progressBar1 = new JProgressBar();
//        }
//        progressBar1.setValue(0);
//        return progressBar1;
//    }

    private JTextField getkeywordTextField(){
        if(urlTextField ==null){
            urlTextField =new JTextField();
        }
        return urlTextField;
    }

    private JTextArea getoutputLog(){
        if(textArea1==null){
            textArea1=new JTextArea();
        }
        return textArea1;
    }


    private JButton getAnalyseButton(){
        AnalyseButton.setText("开始过滤");
        AnalyseButton.addActionListener(new java.awt.event.ActionListener(){
            public void actionPerformed(ActionEvent e){
                String logUrl = urlTextField.getText();
                textArea1.setText("");

//                progressBar1.setValue(0);
                try{
                    // 开始日志分析
                    startAnalyse(logUrl,textArea1);
                }catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        return AnalyseButton;
    }


    public GuiMain(){
        super();
        initialize();
    }

    private void initialize(){
        this.setSize(1280,720);
        this.setContentPane(getJContentPane());
        this.setTitle("GuiMain");
        LogBase.textArea = this.textArea1;
//        LogBase.jProgressBar = this.progressBar1;
        getAnalyseButton();
        getScrollPane();
        getStartTimeTextField();
        getEndTimeTextField();
    }
    private JPanel getJContentPane(){
        if(jContentPane==null){
            jContentPane=new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getkeywordTextField(),null);
            jContentPane.add(getAnalyseButton(),null);
            jContentPane.add(getoutputLog(),null);
        }
        return jContentPane;
    }
    public static void main(String[] args) {

        // TODO Auto-generated method stub
        GuiMain thisClass = new GuiMain();
        thisClass.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        thisClass.setVisible(true);
    }

    private void startAnalyse(String logUrl,JTextArea textArea) throws MalformedURLException {

        String startDateString = startTimeTextField.getText();
        String endDateString = endTimeTextField.getText();
        String extraKeyWordString = extraKeyWords.getText();

        String localPath = "C:\\日志下载\\测试日志.rar";
        LogAnalyse logAnalyse = new LogAnalyse();
        // 下载文件
        if (!logAnalyse.downloadNet(logUrl,localPath)){
            return;
        }
        // 获取文件类型
        String type = FileType.getFileType(localPath);

        switch (type){
            case "zip" : {
                LogBase.print("Get the Android Log Success,Type is ZIP");
                logAnalyse.unzip(localPath);
                logAnalyse.analyseAndroidLog(localPath,startDateString,endDateString,extraKeyWordString);
                break;
            }
            case "gz" : {
                LogBase.print("Get the iOS Log Success,Type is GZ");
                logAnalyse.unGz(localPath);
                logAnalyse.analyseIOSLog(localPath,startDateString,endDateString,extraKeyWordString);
                break;
            }
        }
    }
}