package LogAnalyser;

import javax.swing.*;

public class LogBase{

    public static JTextArea textArea;
//    public static JProgressBar jProgressBar;

    public static JTextArea getTextArea() {
        return textArea;
    }

    public static void print(String log){
        if (textArea == null){
            return;
        }
        textArea.append(log + "\n");
    }

//    public static void setjProgressBar(int n){
//
//        if (jProgressBar == null){
//            return;
//        }
//        jProgressBar.setValue(n);
//
//
//    }

}
