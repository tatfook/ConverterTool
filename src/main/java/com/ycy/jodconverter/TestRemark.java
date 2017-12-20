package com.ycy.jodconverter;

import com.overzealous.remark.util.Html2Md;

public class TestRemark {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String strFileType = "html";
		String strFilePath = "D:\\0.tools\\test.html";
		String outputPath = "D:\\0.tools";
		Html2Md html2Md = new Html2Md();
		html2Md.html2Md(strFileType, strFilePath, outputPath);
	}

}
