package com.ycy.jodconverter;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import Util.Office2HtmlUtil;

public class Office2HtmlThread extends Thread {
     private int index;
     private CountDownLatch latch;
 
     public Office2HtmlThread(int index, CountDownLatch latch) {
         this.index = index;
         this.latch = latch;
     }
 
     @Override
     public void run() {
         super.run();
         String str = String.format("5m_%s.doc", index);
         long time = System.currentTimeMillis();
         work(str);
		 latch.countDown();
         System.out.println(String.format("文件%s解析耗时：%sms", str, String.valueOf(System.currentTimeMillis() - time)));
     }
 
     public static void main(String[] args) {
         CountDownLatch latch = new CountDownLatch(5);
         Office2HtmlUtil.startService();
         for (int i = 1; i < 6; i++) {
             new Office2HtmlThread(i, latch).start();
         }
         try {
            long time = System.currentTimeMillis();
            System.out.println(String.format("开始"));
            latch.await();
            Office2HtmlUtil.stopService();
            System.out.println(String.format("所有文件解析耗时：%sms", String.valueOf(System.currentTimeMillis() - time)));
         } catch (InterruptedException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
        }
    }

    public static void work(String fileName) {
         String sysDir = System.getProperty("user.dir") + "\test";
		 System.out.println("sysDir = " + sysDir);
         File inputFile = new File(sysDir + fileName);
         String outputPath = sysDir + "output_rst";
         File outputFile = new File(
        		  outputPath + "\\" + fileName.split("\\.")[0] + "_" + fileName.split("\\.")[1] + ".html");
         Office2HtmlUtil.office2html(inputFile, outputFile);
    }

}