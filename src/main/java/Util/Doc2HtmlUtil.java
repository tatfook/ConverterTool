package Util;

import java.io.BufferedReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.artofsolving.jodconverter.DocumentConverter;  
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;  
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;  
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.ycy.jodconverter.DConverter;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * 利用jodconverter(基于OpenOffice服务)将文件(*.doc、*.docx、*.xls、*.ppt)转化为html格式或者pdf格式，
 * 使用前请检查OpenOffice服务是否已经开启, OpenOffice进程名称：soffice.exe | soffice.bin
 * @author lcx
 */
public class Doc2HtmlUtil{
    private final Logger logger = Logger.getLogger(Doc2HtmlUtil.class);
	private static String LINUXOFFICEHOME = "/opt/openoffice4/program/";
    private final String OpenOffice_HOME = LINUXOFFICEHOME;
    private final int OPENOFFICE_PORT = 8100;
    //process集合，方便服务器关闭时，关闭openoffice进程
    private List<Process> process = new ArrayList<Process>();
    
    //这里用线程安全的queue管理运行的端口号
    static public BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
    
    
    public void startAllService() throws IOException, NumberFormatException, InterruptedException{

    	//我将使用的端口号写在properties文件中，便于写改
    	String portsStr = "8100,8200,8300,8400,8500,8600,8700,8800,8900,"
    			+ "9000,9100,9200,9300,9400,9500,9600,9700,9800,9900";
    	String[] ports = portsStr.split(",");
    	
		for (String port : ports) {
			//添加到队列 用于线程获取端口 进行连接
			queue.put(Integer.parseInt(port));
			
			//启动OpenOffice的服务  
	        String command = OpenOffice_HOME  
	                + "soffice -headless -accept=\"socket,host=127.0.0.1,port="+port+";urp;\" -nofirststartwizard & ";//这里根据port进行进程开启
	      
	        Process curProcess = Runtime.getRuntime().exec(command);
	        curProcess.waitFor();
	        process.add(curProcess);
	        logger.debug("[startAllService-port-["+port+"]-success]");  
		}
		
		logger.debug("[startAllService-success]");
    }
    
    //服务器关闭时执行 循环关闭所有的打开的openoffice进程
    public void stopAllService(){
    	for (Process p : process) {
			p.destroy();
		}
    	logger.debug("[stopAllService-success]");
    }
    
    
    /**
    * 根据端口获取连接服务  每个转换操作时，JodConverter需要用一个连接连接到端口，（这里类比数据库的连接）
    */
    public OpenOfficeConnection getConnect(int port) throws ConnectException{  
        logger.debug("[connectPort-port:"+port+"]");  
        return new SocketOpenOfficeConnection(port);  
    }  
    
    /** 
     * 转换文件成html 
     *  
     * @param fromFileInputStream: 
     * @throws IOException 
     * @throws InterruptedException  
     */  
    public  void Doc2HtmlExt(String inputPath, String outputPath) throws IOException, InterruptedException {  
        String strRootPath = outputPath;
        
    	//获取文件名称
    	File tempFile =new File(inputPath.trim());  
        String fileName = tempFile.getName();  
        String fileNameNoEx = DConverter.getFileNameNoEx(fileName);
        logger.info("fileName = " + fileNameNoEx);  
                      
    	File htmlRst = new File(strRootPath + File.separator + fileNameNoEx + ".html");
        //这里是重点，每次转换从集合读取一个未使用的端口（直接拿走，这样其他线程就不会读取到这个端口号，不会尝试去使用）</span>  
        //计时并读取一个未使用的端口  
        long old = System.currentTimeMillis();  
        int port = OPENOFFICE_PORT;  //queue.take();  
        //获取并开启连接  
        OpenOfficeConnection connection = getConnect(port);  
        connection.connect();  
        DocumentConverter converter = new OpenOfficeDocumentConverter(connection);   
        try {  
        	logger.info("inputPath = " + inputPath);
        	logger.info("htmlRst = " + htmlRst);
        	converter.convert(new File(inputPath), htmlRst);   
        } catch (Exception e) {  
             
            //当OpenOffice无法打开文件导致错误时，使用POI的提供的转换方式
            String strFileType = FileUtil.getFileType(inputPath.trim());
            //System.out.println(strFileType);
            if(strFileType.equals("doc")){
	            POIDoc2Html pOIDoc2Html = new POIDoc2Html();
	            try {
	            	pOIDoc2Html.convert2Html(inputPath.trim(), strRootPath + File.separator + fileNameNoEx + ".html",strRootPath+File.separator);
				} catch (TransformerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            } else{
            	logger.info("exception:" + e.getMessage());
            }
        }  
        
        //关闭连接  
        connection.disconnect();  
        
        //计算花费时间 将端口放入池中  
        logger.info("convert success! transfer time =" + (System.currentTimeMillis() - old));  
    }  
    
}
    
    
    