package com.ycy.jodconverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.overzealous.remark.util.Html2Md;
import com.overzealous.remark.util.HtmlFormat;

import Util.Doc2HtmlUtil;
import Util.FileUtil;
import Util.PPT2PDFUtil;

/**
 * Hello world! new utf8 changed 20171201
 *
 */
public class App 
{
	static Logger logger = Logger.getLogger(App.class);
	
    public static void main( String[] args ) throws Exception
    {
    	//��ʼ����־����
    	PropertyConfigurator.configure("log4j.properties");
    	
    	//�ж���������Ƿ����������ʽ
    	if (args.length < 1) {
    		logger.error("invalid param, Usage: java -jar DConverter.jar filename ocrflag outputpath");
    		return;
    	}else if ((args.length == 1 && (args[0].equalsIgnoreCase("-help")))
    			|| (args.length == 1 && (args[0].equalsIgnoreCase("-h")))){
    		helpProcess();
    		return;
    	}
    	
    	final String topicIdParam = args[0]; 	//��ȡ����Ĳ���topic_id��
    	logger.info("fileName = " + topicIdParam);

    	String strFilePath = topicIdParam;
    	String strFileType = FileUtil.getFileType(strFilePath);
    	logger.info("strFileType = " + strFileType);
    	
    	//����8100�˿��������ӣ�û������ʱ����OpenOffice����
       /* String[] cmds = {"/bin/sh","-c","netstat -natpl | grep 8100 | awk -F ' ' '{ print $6 }' | grep LISTEN"};  
        Process pro = Runtime.getRuntime().exec(cmds);  
        pro.waitFor(); 
        //��ȡ��ѯ���ص���Ϣ
        InputStream in = pro.getInputStream();  
        BufferedReader read = new BufferedReader(new InputStreamReader(in));  
        String line = null;  
        int flag = 0;//�ж�8100��ǰ�������ӣ�����ѯ�������0ʱ��˵��OpenOffice�����Ѿ�����
        while((line = read.readLine())!=null){ 
        	flag++;
            logger.info("[listen info]:" + line);  
        }  
        logger.info("flag = " + flag);
        //��8100�˿�������ʱ������OpenOffice����
        if(flag<1){
        	logger.info("The openoffice(listen port 8100) is not start, please start it first!");
        	logger.info("[Start Method]: /opt/openoffice4/program/soffice -headless -accept=\"socket,host=127.0.0.1,port=8100;urp;\" -nofirststartwizard&");
        	//String[] cmdStartup = {"/bin/sh","-c","/opt/openoffice4/program/soffice -headless -accept=\"socket,host=127.0.0.1,port=8100;urp;\" -nofirststartwizard&"};
        	//pro = Runtime.getRuntime().exec(cmdStartup);
        	//pro.waitFor();
        	//TimeUnit.SECONDS.sleep(1);//�ó���˯��1�룬�ȴ�Pro����ִ�����
        	//return;
        }*/
        
    	if ( "-1"== strFileType || strFileType.equals("-1")){
    		logger.info("File type error!");
    		return;
    	}else{
    		switch(strFileType){
    		case "pdf":
    			if (args.length > 3 || args.length <= 1){
    				logger.info("Invalid number of parameters");
    				return;
    			}else if (args.length == 3){
    				final int ocrFlag = Integer.parseInt(args[1]);
    	        	logger.info("ocrFlag = " + ocrFlag);
	  	        	String outputPath = args[2];
    				if (0 == ocrFlag){
    					DConverter.PDF2HTML(strFilePath, outputPath);
    					break;
    				} else if (1 == ocrFlag){
    					String strPdfTmp = DConverter.ocrMyPdfProcess(strFilePath);
    					logger.info("strPdfTmp = " + strPdfTmp);
    					DConverter.PDF2HTML(strPdfTmp, outputPath);
    				}else{
    					logger.info("OCR flag error(0:not need ocr recognition, 1:need ocr recognition)!");
    					return;
    				}	
    			}else{
    				logger.info("Invalid number of parameters");
    				return;
    			}
    		case "doc":
    		case "docx":
    			if (args.length >= 3){
    				logger.info("OCR flag is not needed!");
    				return;
    			}else if(args.length == 2){
    				String outputPath = args[1];
    				//���߳�
     				DConverter.DOC2HTML(strFilePath, outputPath);
    				//���̴߳���
    				/*Doc2HtmlUtil doc2HtmlUtilExt = new Doc2HtmlUtil();
    				doc2HtmlUtilExt.Doc2HtmlExt(strFilePath, outputPath);*/
    			}else{
    				logger.info("The output_path param need added!");
    				return;
    			}
    			
    			break;
    			
    		case "ppt":
    		case "pptx":
    			if (args.length >= 3){
    				logger.info("OCR flag is not needed!");
    				return;
    			}else if (args.length == 2){
    				String outputPath = args[1];
    				//���߳�
        			String ppt2PdfPath = DConverter.PPT2PDF(strFilePath);
        			DConverter.PDF2HTML(ppt2PdfPath, outputPath);
    				//���̴߳���
        			/*PPT2PDFUtil ppt2pdfUtil  = new PPT2PDFUtil();
        			String ppt2PdfPath = ppt2pdfUtil.PPT2PDFExt(strFilePath);
        			DConverter.PDF2HTMLEx(ppt2PdfPath, outputPath);*/
        			break;
    			}else{
    				logger.info("The output_path param need added!");
    				return;
    			}
    			

    			
    		case "html":
    		case "url":
    			if (args.length >= 3){
    				logger.info("OCR flag is not needed!");
    				return;
    			}else if (args.length == 2) {
    					String outputPath = args[1];
    					String formatHtmlUri = "error";
    					try {
    						formatHtmlUri = HtmlFormat.DoHtmlFormat(strFilePath, outputPath);
						} catch (Exception e) {
							logger.info("html reader error");
						}
    					Html2Md html2Md = new Html2Md();
    					html2Md.html2Md(strFileType, formatHtmlUri, outputPath);
//    					DConverter.HTML2MD(strFileType, strFilePath, outputPath);
    					break;
    			}else{
    				logger.info("The output_path param need added!");
    				return;
    			}
    			
    		default:
    			logger.info("The type " + strFileType + " is not supported!");
    			break;
    		}
    		//doc2HtmlUtilExt.stopAllService();
    	}
    	
    }
    
    public static void helpProcess(){
		System.out.println("Usage:\tjava -jar DConverter.jar <input> [options] <output_path>\n" + 
				"\tdoc/docx:\t java -jar DConverter.jar filename.doc/.docx \t./output\n" +
				"\tppt/pptx:\t java -jar DConverter.jar filename.ppt/.pptx \t./output\n" + 
				"\tpdf:\t\t java -jar DConverter.jar filename.pdf ocrflag" +
					"(0:not need ocr recognition, 1:need ocr recognition) \t./output\n" +
				"\thtml:\t\t java -jar DConverter.jar filename.html \t./output\n" + 
				"\turl:\t\t java -jar DConverter.jar http:/https:www.xxx.com \t./output\n");
	}
}

