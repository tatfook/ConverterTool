package com.overzealous.remark.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.print.attribute.standard.RequestingUserName;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.ycy.jodconverter.DConverter;

/**
 * 将pdf2html转换后的文件进一步处理，以满足html2md对html文件的格式要求，转换完成成的html样式会混乱，只用作转MD的临时文件，
 * 不用于HTML的展示。
 * @author lrq
 * @date 2017年11月29日 上午9:35:36
 */

public class HtmlFormat {
	/**
	 * 该方法为静态方法，需要传入文件的URI地址，和输出文件的URI地址,抛出文件读取异常,读取文件编码为utf-8,最后返回转换文件后的URI.
	 * @return 
	 */
	public static String DoHtmlFormat(String inputFile, String outputPath) throws IOException{
		File tempFile =new File(inputFile.trim());
		Document doc = Jsoup.parse(tempFile, "utf-8");
		ArrayList<Element> temp = doc.getElementsByClass("pc");
		for (int i = 0; i<temp.size();i++){
			ArrayList<Element> childrens = temp.get(i).children().select(".c");
			ArrayList<Element> rows = new ArrayList<>();
			String thirdClassName = "default";
			if(childrens.size()>0){
				try {
					doc.getElementById("pf"+(i+1)).select(".c."+childrens.get(0).className().split(" ")[2]).first().before("<tableflag>");
				} catch (Exception e) {
					System.out.println("本页无表格元素");
					continue;
				}
				
			}
			for(int j=0;j<childrens.size();j++){	
				//判断孩子节点是不是一行，如果是同一行缓存在rows列表里，如果不是同一行，写入table中
				if(j==0){
					thirdClassName = childrens.get(j).className().split(" ")[2];
				}else if(j==(childrens.size()-1)){
					thirdClassName = "end";
					rows.add(childrens.get(j));
				}
				String className = childrens.get(j).className().split(" ")[2];
				if(className.equals(thirdClassName)){
					rows.add(childrens.get(j));
				}else if((!rows.isEmpty()) && (!className.equals(thirdClassName)) && (!"default".equals(thirdClassName))){
//					doc.getElementById("pf"+(i+1)).select(".c."+className).first().before("<tableflag><trflag>");
					doc.getElementsByTag("tableflag").append("<trflag>");
					if("end".equals(thirdClassName)){
						doc.getElementById("pf"+(i+1)).select(".c."+className).remove();
					}else{
						doc.getElementById("pf"+(i+1)).select(".c."+thirdClassName).remove();
					}
					for(int rowsSize = 0; rowsSize<rows.size(); rowsSize++){
						doc.getElementsByTag("trflag").append("<tdflag>"+rows.get(rowsSize).text()+"</tdflag>");
					}
					doc.getElementsByTag("trflag").tagName("tr");
					doc.getElementsByTag("tdflag").tagName("td");

					rows.clear();
					rows.add(childrens.get(j));	
				}else{
					System.out.println("标签添加错误");
				}
				thirdClassName = className;	
			}
			doc.getElementsByTag("tableflag").tagName("table");
		}

		System.out.println("文件已转换完成！！！");
		
		String content = doc.toString();
		String fileName = tempFile.getName();  
        String fileNameNoEx = DConverter.getFileNameNoEx(fileName);
        String path = outputPath + File.separator + fileNameNoEx + ".temp.html";
        String encoding = "utf-8";
        File file = new File(path);  
        file.delete();  
        try {
			file.createNewFile();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}  
        BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(  
			        new FileOutputStream(file), encoding));
			writer.write(content);  
	        writer.close(); 
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		return path;
	}

}
