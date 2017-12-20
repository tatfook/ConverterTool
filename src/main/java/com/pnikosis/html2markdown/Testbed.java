package com.pnikosis.html2markdown;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.ycy.jodconverter.DConverter;


public class Testbed {
  static Logger logger = Logger.getLogger(DConverter.class);
  public static void HTML2MD(String strType, String strHtml, String outputPath){
	  
		  String mdTextRst = null;
		  URL url = null;
		  final String invalidOutput = "invalid param!";
		  try{
			  if (strType.equals("html")){
				  File in = new File(strHtml);
				  mdTextRst = HTML2Md.convertFile(in, "utf-8");
				  //System.out.println(mdTextRst);
			  }else if (strType.equals("url")){
				  if (!strHtml.contains("http://") && !strHtml.contains("https://")){
					  strHtml = "http://" + strHtml;
				  }
				  url = new URL(strHtml);  
				  mdTextRst = HTML2Md.convert(url, 30000);
				  //System.out.println(mdTextRst);
			  }else{
				  System.out.println(invalidOutput);
				  return;
			  }
			           
			  BufferedWriter writer = null;
		  	  //create a temporary file
		  	  //String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		     // File directory = new File("html2md_output");			    //设定为当前文件夹  
	         // String strRootPath = directory.getCanonicalPath();		//获取标准的路径  
			  String strRootPath = outputPath;
			  
	          //获取文件名称
	          File tempFile =new File(strHtml.trim());  
	          String fileName = tempFile.getName();  
	          String fileNameNoEx = DConverter.getFileNameNoEx(fileName);
	          logger.info("fileName = " + fileNameNoEx);  
	            
	          DConverter.createFile(strRootPath + File.separator + fileNameNoEx + ".md");
	          File mdFile = new File(strRootPath + File.separator + fileNameNoEx + ".md");
			  //This will output the full path where the file will be written to...
			  logger.info("output: " + mdFile.getCanonicalPath());
			
			  writer = new BufferedWriter(new FileWriter(mdFile));
			  writer.write(mdTextRst);
			  writer.close();
		  	  System.out.println("done");
	  	} catch (MalformedURLException e) {
	  			e.printStackTrace();
	  	} catch (IOException e) {
	  			e.printStackTrace();
	  	}
	 }
	
  public static void main(String[] args) {
    URL url;
    final String invalidOutput = "invalid param, Usage: HTML2MD  [optin] input  output";
  //  System.out.println("parm len =" + args.length);
    if (args.length == 2 || args.length == 0 || args.length > 3) {
  		System.out.println(invalidOutput);
  		return;
  	}else if (args.length == 1){
  		final String param1 = args[0]; //参数1
  		if (param1.equals("--help") || param1.equals("-help")){
  			System.out.println("Usage: Html2Md [options] <input> [<output>]");
  			System.out.println("-html  <input.html>  <output.md>");
  			System.out.println("-url  <url>  <output.md>");
  			return;
  	}else{
  		System.out.println(invalidOutput);
  		return;
  		}
  	}
  	else if (args.length == 3){
  		final String optionParam = args[0]; //参数0
  		String inputParam = args[1]; //参数1
  		final String outputParam = args[2]; //参数2
    
  		String mdTextRst = null;
  		
  		try {
  			if (optionParam.equals("-html")){
  				File in = new File(inputParam);
  				mdTextRst = HTML2Md.convertFile(in, "utf-8");
  			//	System.out.println(mdTextRst);
  			}else if(optionParam.equals("-url")){
  				if (!inputParam.contains("http://") && !inputParam.contains("https://")){
  					inputParam = "http://" + inputParam;
  				}
  				url = new URL(inputParam);  
  				mdTextRst = HTML2Md.convert(url, 30000);
  			//	System.out.println(mdTextRst);
  			}else{
  				System.out.println(invalidOutput);
  				return;
  		}
         
  		    BufferedWriter writer = null;
  		    //create a temporary file
  		    //String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
	  	    File mdFile = new File(outputParam);
	  	   //This will output the full path where the file will be written to...
	  	   logger.info("output: " + mdFile.getCanonicalPath());
	
	  	   writer = new BufferedWriter(new FileWriter(mdFile));
	  	   writer.write(mdTextRst);
	  	   writer.close();
  			System.out.println("done");
  		} catch (MalformedURLException e) {
  			e.printStackTrace();
  		} catch (IOException e) {
  			e.printStackTrace();
  		}
  	}
  }
}