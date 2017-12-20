package com.overzealous.remark.util;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;

import com.overzealous.remark.Options;
import com.overzealous.remark.Remark;
import com.sun.star.io.TempFile;
import com.ycy.jodconverter.DConverter;


/**
 * 该类实现html文件转换为md文件的方法
 * @author Administrator
 *
 */
public class Html2Md {
	/**
	 * 该方法实现html文档转换为md文档
	 * @param strType文件类型，可以是HTML文档，也可以是url地址
	 * @param strHtmlHTML文档的位置
	 * @param outputPath转换完结果的存放位置
	 */
	public void html2Md(String strType, String strHtml, String outputPath){
		URL url = null;
		try {
			if(strType.equals("html")){
				//指定处理方式为multiMarkdown,将HTML中的表格也转换为markdown语言
				Options option = Options.markdown();				
				Writer myWriter = null;
				try {
					String strRootPath = outputPath;					  
			        //获取文件名称
			        File tempFile =new File(strHtml.trim());  
			        String fileName = tempFile.getName();  
			        String fileNameNoEx = DConverter.getFileNameNoEx(fileName);
			        fileNameNoEx = fileNameNoEx.replace(".temp", "");
				    myWriter = new FileWriter(new File(strRootPath + File.separator + fileNameNoEx + ".md"));
				    Remark remark = new Remark(option).withWriter(myWriter);
				    remark.convert(new File(strHtml));
				}catch (Exception e) {
					e.printStackTrace();
				} finally {
				    try {
						myWriter.close();
						File deleteFile =new File(strHtml.trim());
						deleteFile.delete();
						System.out.println("缓存文件已删除！");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if(strType.equals("url")) {
				if (!strHtml.contains("http://") && !strHtml.contains("https://")){
					  strHtml = "http://" + strHtml;
				  }
				url = new URL(strHtml); 
				//指定处理方式为multiMarkdown,将HTML中的表格也转换为markdown语言
				Options option = Options.multiMarkdown();				
				Writer myWriter = null;
				try {
					String strRootPath = outputPath;					  
			        //获取文件名称
			        File tempFile =new File(strHtml.trim());  
			        String fileName = tempFile.getName();  
			        String fileNameNoEx = DConverter.getFileNameNoEx(fileName);
				    myWriter = new FileWriter(new File(strRootPath + File.separator + fileNameNoEx + ".md"));
				    Remark remark = new Remark(option).withWriter(myWriter);
				    //传入url地址，设置超时时间
				    remark.convert(url, 15000);
						
				}catch (Exception e) {
					e.printStackTrace();
				} finally {
				    try {
						myWriter.close();
						File deleteFile =new File(strHtml.trim());
						deleteFile.delete();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
}
