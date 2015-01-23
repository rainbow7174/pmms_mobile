package kr.hs2.co.util;

public class FileUtil {
	public static String getExtension(String fileStr){
		return fileStr.substring(fileStr.lastIndexOf(".")+1,fileStr.length());
	}
}
