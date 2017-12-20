package com.ycy.jodconverter;

import org.apache.log4j.Logger;

import org.apache.log4j.PropertyConfigurator;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DocumentFormat;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeConnectionProtocol;
import org.artofsolving.jodconverter.office.OfficeManager;

import com.pnikosis.html2markdown.Testbed;

import Util.ConfRW;
import Util.DfConf;
import Util.FileUtil;
import Util.StreamGobbler;

import java.io.File;
import java.io.IOException;

/**
 * Created by ycy on 20170621.
 */
public class DConverter {
	static Logger logger = Logger.getLogger(DConverter.class);
  	
	static String WINOFFICEHOME = "C:\\Program Files (x86)\\OpenOffice 4";
	static String LINUXOFFICEHOME = "/opt/openoffice4";
//	static String PDF2HTMLCOMMAND = "pdf2htmlEX --embed-image 1 --tounicode 1 --embed-css 0 --embed-font 1 "
//			+ "--embed-javascript 0 --embed-outline 0 --no-drm 1 --dest-dir ";
	static String PDF2HTMLCOMMAND = "pdf2htmlEX --no-drm 1 --embed-image 1 --dest-dir ";
	static String PDF2HTMLCOMMANDEX = PDF2HTMLCOMMAND;
	static String OCRMYPDFCOMMAND = "ocrmypdf --pdf-renderer tesseract --output-type pdf "
									+ "--skip-text --tesseract-timeout 6000 -l eng+chi_sim ";
	static long EXECUTE_TIMEOUT = 1000 * 60 * 5L;
	static long QUEUE_TIMEOUT = 1000 * 60 * 60 * 24L;
	
	private static final int MAX_CONVERSIONS = 1024;
	private static final int MAX_RUNNING_THREADS = 128;
	private static final int MAX_TASKS_PER_PROCESS = 1;

	private static final String INPUT_DOC_EXTENSION = "doc";
	private static final String INPUT_PPT_EXTENSION = "doc";
	private static final String OUTPUT_EXTENSION = "html";
	private static int OPEN_OFFICE_PORT[] = {2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009};
	
	/**
	 **@brief：判定输入文件及文件类型
	 **@param: 文件名称
	 **@return：空
	 **@author: ycy
	 **@date:  20170630
	 */
	public static void FileJudgeMent(String strInput){
		
		
		
	}
	
	
	/**
	 **@brief：获取文件扩展名
	 **@param: 文件名称
	 **@return：空
	 **@author: ycy
	 **@date:  20170630
	 */
	 public static String getExtensionName(String filename) { 
	        if ((filename != null) && (filename.length() > 0)) { 
	            int dot = filename.lastIndexOf('.'); 
	            if ((dot >-1) && (dot < (filename.length() - 1))) { 
	                return filename.substring(dot + 1); 
	            } 
	        } 
	        return filename; 
	 } 
	 
	/**
	 **@brief：获取不带扩展名的文件名
	 **@param: 文件名称
	 **@return：空
	 **@author: ycy
	 **@date:  20170701
	 */
	 public static String getFileNameNoEx(String filename) { 
        if ((filename != null) && (filename.length() > 0)) { 
            int dot = filename.lastIndexOf('.'); 
            if ((dot >-1) && (dot < (filename.length()))) { 
                return filename.substring(0, dot); 
            } 
        } 
        return filename; 
	 } 
	 
	 
	/**
	 **@brief：创建路径
	 **@param: 路径名称
	 **@return：ture，创建成功；false，创建失败！
	 **@author: ycy
	 **@date:  20170701
	 */
	 public static boolean createDir(String destDirName) {  
	        File dir = new File(destDirName);  
	        if (dir.exists()) {  
	        	logger.info("create dir" + destDirName + " failed, src dir already exist!");  
	            return false;  
	        }  
	        if (!destDirName.endsWith(File.separator)) {  
	            destDirName = destDirName + File.separator;  
	        }  
	        //创建目录  
	        if (dir.mkdirs()) {  
	        	logger.info("create dir" + destDirName + "success！");  
	            return true;  
	        } else {  
	        	logger.info("create dir" + destDirName + "failed！");  
	            return false;  
	        }  
	    } 
	 
	 /**
	 **@brief：创建文件
	 **@param: 路径名称
	 **@return：ture，创建成功；false，创建失败！
	 **@author: ycy
	 **@date:  20170701
	 */
	 public static boolean createFile(String destFileName) {  
	        File file = new File(destFileName);  
	        if(file.exists()) {  
	            logger.info("create file " + destFileName + "failed，this file already exist！");  
	            return false;  
	        }  
	        if (destFileName.endsWith(File.separator)) {  
	        	logger.info("create file" + destFileName + "failed，the dest file can't be dir！");  
	            return false;  
	        }  
	        //判断目标文件所在的目录是否存在  
	        if(!file.getParentFile().exists()) {  
	            //如果目标文件所在的目录不存在，则创建父目录  
	        	logger.info("the dest dir is not exist, create it！");  
	            if(!file.getParentFile().mkdirs()) {  
	            	logger.info("create dest dir failed！");  
	                return false;  
	            }  
	        }  
	        //创建目标文件  
	        try {  
	            if (file.createNewFile()) {  
	            	logger.info("create file " + destFileName + " success！");  
	                return true;  
	            } else {  
	            	logger.info("create file " + destFileName + " failed！");  
	                return false;  
	            }  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	            logger.info("create file" + destFileName + "failed！" + e.getMessage());  
	            return false;  
	        }  
	    }  
	
	
	
	/**
	 **@brief：ppt/pptx转为pdf
	 **@param: 文件名称
	 **@return：空
	 **@author: ycy
	 **@date:  20170701
	 */
	public static String  PPT2PDF(String ppt){
		   OfficeManager officeManager = null;
	       Integer result=-1;
	       ConfRW.init_config("conf/dafu.properties");
	       String officeHome = null;
	       logger.info("DfConf.linuxFlag = " + DfConf.linuxFlag);
	       if (1 == DfConf.linuxFlag){
	    		officeHome = LINUXOFFICEHOME;
	    	}else{
	    		officeHome = WINOFFICEHOME;
	       }
	       String pdfRstPath = null; 
	        try {
	          //  officeManager = new DefaultOfficeManagerConfiguration()
	          //           .setOfficeHome(officeHome)
	          //           .buildOfficeManager();
	        	DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();
	       		configuration.setPortNumbers(2002);
	       		configuration.setMaxTasksPerProcess(MAX_TASKS_PER_PROCESS);
	       		configuration.setOfficeHome(officeHome);
	       		officeManager = configuration.buildOfficeManager();
	            
	            officeManager.start();

	            // 2) Create JODConverter converter
	            OfficeDocumentConverter converter = new OfficeDocumentConverter(
	                    officeManager);

	            //方式二  
	            File directory = new File("ppt2pdf_output");			//设定为当前文件夹  
	        	String strRootPath = directory.getCanonicalPath();		//获取标准的路径  
	        	
	        	//获取文件名称
	        	File tempFile =new File(ppt.trim());  
	        	logger.info("tempFile = " + tempFile);
	        	
	            String fileName = tempFile.getName();  
	            String fileNameNoEx = getFileNameNoEx(fileName);
	            logger.info("fileName = " + fileNameNoEx);  
	           
	            pdfRstPath = strRootPath + File.separator + fileNameNoEx + ".pdf";
	        	File pdfRst = new File(strRootPath + File.separator + fileNameNoEx + ".pdf");
	            converter.convert(new File(ppt), pdfRst);        
	            
	        }catch(Exception ex){
	            ex.printStackTrace();
	        }finally {
	            // 4) Stop LibreOffice in headless mode.
	            if (officeManager != null) {
	                officeManager.stop();
	            }
	        }

	        return pdfRstPath;
	    }
	
	 /** 
     * 调用pdf2htmlEX将pdf文件转换为html文件 
     * @param command 调用exe的字符串 
     * @param pdfName 需要转换的pdf文件名称 
     * @param htmlName 生成的html文件名称 
     **@author: ycy
	 **@date:  20170701
	 */
    public static boolean PDF2HTML(String pdfName, String outputPath){  
      	PropertyConfigurator.configure("log4j.properties");
    	//String htmlName
        Runtime rt = Runtime.getRuntime();  
        try {  
        	//获取文件名
        	File tempFile =new File(pdfName.trim());  
            String fileName = tempFile.getName();  
            String fileNameNoEx = getFileNameNoEx(fileName);
            logger.info("pdfName = " + fileNameNoEx);  
        	
        	String pdf2htmlexe = PDF2HTMLCOMMAND + outputPath  + " " 
        						+ pdfName + " " + fileNameNoEx + ".html";
            Process p = rt.exec(pdf2htmlexe);  
            
            StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");                
            // kick off stderr    
            errorGobbler.start();  
            
            StreamGobbler outGobbler = new StreamGobbler(p.getInputStream(), "STDOUT");    
            // kick off stdout    
            outGobbler.start();   
            
            int w = p.waitFor();  
            logger.info("w=" + w);  
            
            int v = p.exitValue();  
            logger.info("v=" + v);  
            
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return false;  
    }  
    
    /** 
     * 调用pdf2htmlEX将pdf文件转换为html文件 
     * @param command 调用exe的字符串 
     * @param pdfName 需要转换的pdf文件名称 
     * @param htmlName 生成的html文件名称 
     **@author: ycy
	 **@date:  20170701
	 */
    public static boolean PDF2HTMLEx(String pdfName, String outputPath){  
      	PropertyConfigurator.configure("log4j.properties");
    	//String htmlName
        Runtime rt = Runtime.getRuntime();  
        try {  
        	//获取文件名
        	File tempFile =new File(pdfName.trim());  
            String fileName = tempFile.getName();  
            String fileNameNoEx = getFileNameNoEx(fileName);
            logger.info("pdfName = " + fileNameNoEx);  
        	
        	String pdf2htmlexe = PDF2HTMLCOMMANDEX + outputPath + " " + pdfName + " " + fileNameNoEx + ".html";
            Process p = rt.exec(pdf2htmlexe);  
            
            StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");                
            // kick off stderr    
            errorGobbler.start();  
            
            StreamGobbler outGobbler = new StreamGobbler(p.getInputStream(), "STDOUT");    
            // kick off stdout    
            outGobbler.start();   
            
            int w = p.waitFor();  
            logger.info("w=" + w);  
            
            int v = p.exitValue();  
            logger.info("v=" + v);  
            
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return false;  
    }  
    
    
    /** 
     * 调用ocrmypdf将pdf文件转换为能识别的pdf
     * @param command 调用exe的字符串 
     * @param pdfName 需要转换的pdf文件名称 
     * @param htmlName 生成的html文件名称 
     **@author: ycy
	 **@date:  20170713
	 */
    public static String ocrMyPdfProcess(String pdfName){  
      	PropertyConfigurator.configure("log4j.properties");
    	String pdfRstPath = null;
        Runtime rt = Runtime.getRuntime();  
        try {  
        	//获取文件名
        	File tempFile =new File(pdfName.trim());  
            String fileName = tempFile.getName();  
            String fileNameNoEx = getFileNameNoEx(fileName);
            logger.info("pdfName = " + fileNameNoEx);  
            
            createDir("OCR_Processed");
            
            pdfRstPath = "./OCR_Processed/" + fileNameNoEx + ".pdf";
        	String ocrmypdfexe = OCRMYPDFCOMMAND + " " + pdfName + " " + pdfRstPath;
            Process p = rt.exec(ocrmypdfexe);  
            
            StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");                
            // kick off stderr    
            errorGobbler.start();  
            
            StreamGobbler outGobbler = new StreamGobbler(p.getInputStream(), "STDOUT");    
            // kick off stdout    
            outGobbler.start();   
            
            int w = p.waitFor();  
            logger.info("w=" + w);  
            
            int v = p.exitValue();  
            logger.info("v=" + v);  
            logger.info("ocrmypdf execute success!");
             
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return pdfRstPath;  
    }  
		
	/**
	 **@brief：html转为md(考虑图片的存放）
	 **@param: html
	 **@return：空
	 **@author: ycy
	 **@date:  20170701
	 */
	public static void HTML2MD(String strType, String strHtml, String outputPath){
		Testbed.HTML2MD(strType, strHtml, outputPath);
	}
	
	/**
	 **@brief：ocr转为txt(考虑图片的存放）
	 **@param: image
	 **@author: ycy
	 **@date:  20170701
	 */
	public static void OCR2TXT(String strImage){
		
	}
	
	
	/**
	 **@brief：txt转为html(考虑图片的存放）
	 **@param: txt
	 **@author: ycy
	 **@date:  20170701
	 */
	public static void TXT2HTML(String strTxt){
		
	}
	
	/**
	 **@brief：doc转为html(考虑图片的存放）ok
	 **@param: 文件名称
	 **@author: ycy
	 **@date:  20170701
	 */
    public static Integer DOC2HTML(String doc, String outputPath){
    	OfficeManager officeManager = null;
        Integer result=-1;
    	ConfRW.init_config("conf/dafu.properties");
    	String officeHome;
    	logger.info("DfConf.linuxFlag = " + DfConf.linuxFlag);
    	if (1 == DfConf.linuxFlag){
    		officeHome = LINUXOFFICEHOME;
    	}else{
    		officeHome = WINOFFICEHOME;
    	}
        
        try {
        	DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();
       		configuration.setPortNumbers(8200);
       		configuration.setMaxTasksPerProcess(MAX_TASKS_PER_PROCESS);
       		configuration.setOfficeHome(officeHome);
       		configuration.setTaskExecutionTimeout(EXECUTE_TIMEOUT);	//设置任务执行超时为5分钟
       		configuration.setTaskQueueTimeout(QUEUE_TIMEOUT);//设置任务队列超时为24小时
       		officeManager = configuration.buildOfficeManager();
        	/*
            officeManager = new DefaultOfficeManagerConfiguration()
                    .setOfficeHome(officeHome)
                    //.setConnectionProtocol(OfficeConnectionProtocol.PIPE)
                    .buildOfficeManager();*/
            officeManager.start();

            // 2) Create JODConverter converter
            OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);

            //方式二  
          //  File directory = new File("doc2html_output");			//设定为当前文件夹  
         //	String strRootPath = directory.getCanonicalPath();		//获取标准的路径  
            String strRootPath = outputPath;
            
        	//获取文件名称
        	File tempFile =new File(doc.trim());  
            String fileName = tempFile.getName();  
            String fileNameNoEx = getFileNameNoEx(fileName);
            logger.info("fileName = " + fileNameNoEx);  
                          
        	File htmlRst = new File(strRootPath + File.separator + fileNameNoEx + ".html");
            converter.convert(new File(doc), htmlRst);        
            
        }catch(Exception ex){
            ex.printStackTrace();
            logger.info("[ERROR] DOC2HTML convert failed! reason: " + ex);
        }finally {
            // 4) Stop LibreOffice in headless mode.
            if (officeManager != null) {
                officeManager.stop();
            }
        }

        return result;
    }
    
	/**
	 **@brief：ppt转为html(考虑图片的存放）ok
	 **@param: 文件名称
	 **@author: ycy
	 **@date:  20170701
	 */
    public static Integer PPT2HTML(String ppt, String outputPath){
        OfficeManager officeManager = null;
        Integer result=-1;
    	ConfRW.init_config("conf/dafu.properties");
    	String officeHome;
    	logger.info("DfConf.linuxFlag = " + DfConf.linuxFlag);
    	if (1 == DfConf.linuxFlag){
    		officeHome = LINUXOFFICEHOME;
    	}else{
    		officeHome = WINOFFICEHOME;
    	}
    	
        try {
            officeManager = new DefaultOfficeManagerConfiguration()
                    .setOfficeHome(officeHome)
                    .buildOfficeManager();
            officeManager.start();

            // 2) Create JODConverter converter
            OfficeDocumentConverter converter = new OfficeDocumentConverter(
                    officeManager);
            String strRootPath = outputPath;
            
        	//获取文件名称
        	File tempFile =new File(ppt.trim());  
            String fileName = tempFile.getName();  
            String fileNameNoEx = getFileNameNoEx(fileName);
            logger.info("fileName = " + fileNameNoEx);  
                          
        	File htmlRst = new File(strRootPath + File.separator + fileNameNoEx + ".html");
            converter.convert(new File(ppt), htmlRst);        
            
        }catch(Exception ex){
            ex.printStackTrace();
        }finally {
            // 4) Stop LibreOffice in headless mode.
            if (officeManager != null) {
                officeManager.stop();
            }
        }

        return result;
    }
    
	/**
	 **@brief：ppt转为pdf扩展多线程处理ok
	 **@param: 文件名称
	 **@author: ycy
	 **@date:  20170825
	 */
    public String PPT2PDFEXT(String inputPath, String inputExt) throws Exception{
        String pdfRstPath = null; 
   		DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();
   		configuration.setPortNumbers(2002, 2003);
   		configuration.setMaxTasksPerProcess(MAX_TASKS_PER_PROCESS);
   		configuration.setOfficeHome("/opt/openoffice4");
   		
   		OfficeManager officeManager = configuration.buildOfficeManager();
   		
   		OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
   		DocumentFormatRegistry formatRegistry = converter.getFormatRegistry();
       
   		officeManager.start();
   		try {
   				File inputFile = new File(inputPath);
   				Thread[] threads = new Thread[MAX_RUNNING_THREADS];
           
   				boolean first = true;
   				int t = 0;
   			//	for (int i = 0; i < MAX_CONVERSIONS; ++i) {
   					DocumentFormat inputFormat = formatRegistry.getFormatByExtension(inputExt);
   					DocumentFormat outputFormat = formatRegistry.getFormatByExtension(OUTPUT_EXTENSION);

   					//获取文件名称   20170831
   					File directory = new File("ppt2pdf_output");			//设定为当前文件夹  
   					String strRootPath = directory.getCanonicalPath();		//获取标准的路径  
   					File tempFile =new File(inputPath.trim());  
   					String fileName = tempFile.getName();  
   					String fileNameNoEx = getFileNameNoEx(fileName);
   					logger.info("fileName = " + fileNameNoEx);     

   					File outputFile = new File(strRootPath + File.separator + fileNameNoEx + OUTPUT_EXTENSION);
   					outputFile.deleteOnExit();
   					
   					pdfRstPath = strRootPath + File.separator + fileNameNoEx + OUTPUT_EXTENSION;
           	   	
   					// Converts the first document without threads to ensure everything is OK. 
   					if (first) {
   						converter.convert(inputFile, outputFile);
   						first = false;
   					}
               
           	    logger.info("Creating thread...");
           	    Runner r = new Runner(inputFile, outputFile, inputFormat, outputFormat, converter);
           	    threads[t] = new Thread(r);
           	    threads[t].start();
               
           	    if (t == MAX_RUNNING_THREADS) {
           	    	for (int j = 0; j < t; ++j) {
           	    		threads[j].join();
           	    	}
           	    	t = 0;
           	    }
 		//    }
 		    
 		    // Wait for remaining threads.
 		    for (int j = 0; j < t; j++) {
 		    	threads[j].join();
 		    }
       }
       finally {
           officeManager.stop();
       }
      return pdfRstPath;
   	}
    
	/**
	 **@brief：doc转为html扩展多线程处理ok
	 **@param: 文件名称
	 **@author: ycy
	 **@date:  20170825
	 */
	public void DOC2HTMLEXT(String inputPath, String inputExt, String outputPath) throws Exception{
   		DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();
   		configuration.setPortNumbers(2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009);
   		configuration.setMaxTasksPerProcess(MAX_TASKS_PER_PROCESS);
   		configuration.setOfficeHome("/opt/openoffice4");
   		
   		OfficeManager officeManager = configuration.buildOfficeManager();
   		
   		OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
   		DocumentFormatRegistry formatRegistry = converter.getFormatRegistry();
       
   		officeManager.start();
   		try {
   				File inputFile = new File(inputPath);
   				Thread[] threads = new Thread[MAX_RUNNING_THREADS];
           
   				boolean first = true;
   				int t = 0;
   					// for (int i = 0; i < MAX_CONVERSIONS; ++i) {
   					DocumentFormat inputFormat = formatRegistry.getFormatByExtension(inputExt);
   					DocumentFormat outputFormat = formatRegistry.getFormatByExtension(OUTPUT_EXTENSION);

   					//获取文件名称   20170831
   					String strRootPath = outputPath;
   					File tempFile =new File(inputPath.trim());  
   					String fileName = tempFile.getName();  
   					String fileNameNoEx = getFileNameNoEx(fileName);
   					logger.info("fileName = " + fileNameNoEx);                
   					File outputFile = new File(strRootPath + File.separator + fileNameNoEx + OUTPUT_EXTENSION);
   					outputFile.deleteOnExit();
           	   	
   					// Converts the first document without threads to ensure everything is OK. 
   					if (first) {
   						converter.convert(inputFile, outputFile);
   						first = false;
   					}
               
   					logger.info("Creating thread...");
   					Runner r = new Runner(inputFile, outputFile, inputFormat, outputFormat, converter);
   					threads[t] = new Thread(r);
   					threads[t].start();
               
   					if (t == MAX_RUNNING_THREADS) {
   						for (int j = 0; j < t; ++j) {
   							threads[j].join();
   						}
   						t = 0;
   					}
   			//   }
 		    
   				// Wait for remaining threads.
   				for (int j = 0; j < t; j++) {
   					threads[j].join();
   				}
   			}
       finally {
           officeManager.stop();
       }
   	}
	
	/**
	 **@brief：扩展多线程处理ok
	 **@param: 文件名称
	 **@author: ycy
	 **@date:  20170825
	 */
    public class Runner implements Runnable {
    	public Runner(File inputFile, File outputFile, DocumentFormat inputFormat, DocumentFormat outputFormat,
    			OfficeDocumentConverter converter) {
    		super();
    		this.inputFile = inputFile;
    		this.outputFile = outputFile;
    		this.inputFormat = inputFormat;
    		this.outputFormat = outputFormat;
    		this.converter = converter;
    	}

    	File inputFile, outputFile;
    	DocumentFormat inputFormat, outputFormat;
    	OfficeDocumentConverter converter;

    	public void run() {
    		try {
    			System.out.printf("-- converting %s to %s... ", inputFormat.getExtension(), outputFormat.getExtension());
    			converter.convert(inputFile, outputFile, outputFormat);
    			System.out.printf("done.\n");
    		}
    		catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    }

    
 }
