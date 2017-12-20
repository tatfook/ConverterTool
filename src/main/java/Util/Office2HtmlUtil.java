package Util;

import java.io.File;
import java.util.regex.Pattern;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

/*
 ** refer: http://www.echojb.com/web-develop/2017/08/08/456555.html
 */
public class Office2HtmlUtil {

     private static Office2HtmlUtil office2HtmlUtil = new Office2HtmlUtil();
     private static OfficeManager officeManager;
     // 服务端口
     private static int OPEN_OFFICE_PORT[] = { 8100, 8101, 8102, 8103 };
 	 static String WINOFFICEHOME = "C:\\Program Files (x86)\\OpenOffice 4";
 	 static String LINUXOFFICEHOME = "/opt/openoffice4";

    public static Office2HtmlUtil getOffice2HtmlUtil() {
         return office2HtmlUtil;
     }
    
     /**
		* 
		* office2Html 方法
		* @descript：TODO
		* @param inputFile
		*        文件全路径
		* @param outFilePath
		*       html文件全路径
		* @return void
     */
     public static void office2html(File inputFile, File outFile) {
         try {
             	// 打开服务
        	 	startService();
        	 	OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
        	 	
        	 	// 开始转换
        	 	converter.convert(inputFile, outFile);
        	 	
        	 	// 关闭
        	 	stopService();
        	 	
        	 	System.out.println("[office2html] running over!");
         } catch (Exception e) {
             // TODO: handle exception
            e.printStackTrace();
         }
     }
 
     public static void stopService() {
         if (officeManager != null) {
             officeManager.stop();
         }
     }
 
     public static void startService() {
         DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();
         try {
             configuration.setOfficeHome(WINOFFICEHOME);// 设置安装目录
             configuration.setPortNumbers(OPEN_OFFICE_PORT); // 设置端口
             configuration.setTaskExecutionTimeout(1000 * 60 * 5L);
             configuration.setTaskQueueTimeout(1000 * 60 * 60 * 24L);
             officeManager = configuration.buildOfficeManager();
             officeManager.start(); // 启动服务
        } catch (Exception ce) {
             System.out.println("office转换服务启动失败!详细信息:" + ce);
         }
     }
 
     /**
      * openoffice的安装路径
     * 
     * @return
     */
     public static String getOfficeHome() {
         String osName = System.getProperty("os.name");
        if (Pattern.matches("Linux.*", osName)) {
            return "/opt/openoffice4";
         } else if (Pattern.matches("Windows.*", osName)) {
		 return "C:/Program Files (x86)/OpenOffice 4/";
         } else if (Pattern.matches("Mac.*", osName)) {
             return "/Application/OpenOffice.org.app/Contents";
        }
         return null;
     }
 }