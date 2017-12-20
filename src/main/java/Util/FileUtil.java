package Util;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;

import com.ycy.jodconverter.App;

public class FileUtil {
	static Logger logger = Logger.getLogger(FileUtil.class);
	
	/*
	 **@brief:  获取文件类型
	 **@param:  文件路径
	 **@return: 文件类型
	 **@thor: ycy
	 **@date:  20170630
	 */
	public static String getFileType(String strFilePath){
		
		String strType = null;
		
		//最外层，正则匹配URL
    	boolean bIsValidUrl = FileUtil.isValidURL(strFilePath);
    	if (bIsValidUrl){
    	  	logger.info("bIsValidUrl = " + bIsValidUrl);
    	  	strType = "url";
    	  	return strType;
    	}else{
			File curFile = null;
			try {
				//为增加可移植性，建议使用File.separator
				curFile = new File(strFilePath);   
			} catch (Exception ex) {
				// TODO: handle exception
				logger.info("catch exception: " + ex);
				return "-1";
			}
			strType = FileUtil.getFileExtension(curFile);
			if ("-1" == strType || strType.equals("-1")){
				logger.info(strFilePath + " is Error!");
			}else{
				//System.out.println("strType = "  + strType);
			}
			return strType;
    	} 
    	
	}
	
	/*
	 **@brief:  判定是否是有效的url
	 **@param:  url地址
	 **@return: true,是; false,不是。
	 **@author: ycy
	 **@date:   20170630
	 */
	public static boolean isValidURL(String urlString)
	{
	    try
	    {
	        URL url = new URL(urlString);
	        url.toURI();
	        return true;
	    } catch (Exception exception)
	    {
	        return false;
	    }
	}

	/*
	 **@brief:  获取文件的扩展名
	 **@param:  url地址
	 **@return: true,是; false,不是。
	 **@author: ycy
	 **@date:   20170630
	 */
    public static String getFileExtension(File file) {
        if (file == null) {
        	logger.info("file argument was null!");
         /*   throw new NullPointerException("file argument was null");*/
        	return "-1";
        }
        if (!file.isFile()) {
        	logger.info("The file " + file.getAbsolutePath() + " is not exist !");
            /*throw new IllegalArgumentException("getFileExtension(File file)"
                    + " called on File object that wasn't an actual file"
                    + " (perhaps a directory or device?). file had path: "
                    + file.getAbsolutePath());*/
        	return "-1";
        }
        
        char ch = 0;
        int len = 0;
        String fileName = file.getName();
        if(fileName==null || 
                (len = fileName.length())==0 || 
                (ch = fileName.charAt(len-1))=='/' || ch=='\\' || //in the case of a directory
                 ch=='.' ) //in the case of . or ..
            return "-1";
        int dotInd = fileName.lastIndexOf('.'),
            sepInd = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
        if( dotInd<=sepInd )
            return "-1";
        else
            return fileName.substring(dotInd+1).toLowerCase();
    }

}