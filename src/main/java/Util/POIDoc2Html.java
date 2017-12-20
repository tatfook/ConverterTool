package Util;

import java.io.BufferedWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.w3c.dom.Document;

public class POIDoc2Html {
	//waring:please use UTF-8!
	//将转换后的文档以流的方式写入转换后的HTML文件中
	private void writeFile(String content, String path) {  
        FileOutputStream fos = null;  
        BufferedWriter bw = null;  
        try {  
            File file = new File(path);  
            fos = new FileOutputStream(file);  
            bw = new BufferedWriter(new OutputStreamWriter(fos));  
            bw.write(content);  
        } catch (FileNotFoundException fnfe) {  
            fnfe.printStackTrace();  
        } catch (IOException ioe) {  
            ioe.printStackTrace();  
        } finally {  
            try {  
                if (bw != null)  
                    bw.close();  
                if (fos != null)  
                    fos.close();  
            } catch (IOException ie) {  
            }  
        }  
    }
	
	public void convert2Html(String fileName, String outPutFile , String outputPath)  
            throws TransformerException, IOException,  
            ParserConfigurationException {  
		//创建一个HWPFDocument的对象，来读取要转换的文档
//		InputStream is = new FileInputStream(fileName); 
//        HWPFDocument wordDocument = new HWPFDocument(is); 
		//用try catch块包裹整个处理过程，防止出错时整个程序被阻塞
        try{
        	POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(fileName));
        	HWPFDocument wordDocument = new HWPFDocument(fs);
	        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(  
	                DocumentBuilderFactory.newInstance().newDocumentBuilder()  
	                        .newDocument());  
	        //在转换后的HTML文档中加入图片的链接，并设置图片的大小
	         wordToHtmlConverter.setPicturesManager( new PicturesManager()  
	         {  
	             public String savePicture( byte[] content,  
	                     PictureType pictureType, String suggestedName,  
	                     float widthInches, float heightInches )  
	             { 
	                 return suggestedName; 
	             }  
	         } );  
	        wordToHtmlConverter.processDocument(wordDocument);
	        //保存word文档中的图片 
	        List pics=wordDocument.getPicturesTable().getAllPictures();  
	        if(pics!=null){  
	            for(int i=0;i<pics.size();i++){  
	                Picture pic = (Picture)pics.get(i);  
	                System.out.println();  
	                try {  
	                    pic.writeImageContent(new FileOutputStream(outputPath  
	                            +pic.suggestFullFileName()));  
	                } catch (FileNotFoundException e) {  
	                    e.printStackTrace();  
	                }    
	            }  
	        } 
	        //生成HTML文档 
	        Document htmlDocument = wordToHtmlConverter.getDocument();
	        ByteArrayOutputStream out = new ByteArrayOutputStream();  
	        DOMSource domSource = new DOMSource(htmlDocument);  
	        StreamResult streamResult = new StreamResult(out);  
	  
	        TransformerFactory tf = TransformerFactory.newInstance();  
	        Transformer serializer = tf.newTransformer();  
	        /*serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8"); */ 
	        serializer.setOutputProperty(OutputKeys.INDENT, "yes");  
	        serializer.setOutputProperty(OutputKeys.METHOD, "html");  
	        serializer.transform(domSource, streamResult);  
	        out.close();  
	        writeFile(new String(out.toByteArray()), outPutFile);  
        }catch(Exception e){
        	System.out.print(e);
        }
    }
	//测试方法
	public static void main(String argv[]) {  
        try {
        	POIDoc2Html poiDoc2Html = new POIDoc2Html();
        	poiDoc2Html.convert2Html("F://demo.doc","F://4.html","F://");  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    } 

}
