package kr.hs2.co.util;

public class ReplaceUtil {
	/**
     * DB 입.출력시 변환처리
     * @param str 변환타겟
     * @param n 변환case
     * @return 결과값
     */
    public static String encodeHTMLSpecialChar(String str,int n) {
        switch (n){
            case 1 : // text mode db 입력
                     str = rplc(str,"<","&lt;");
                     str = rplc(str,"\"","&quot;");
            break;
            case 2 : // html mode db 입력
                     str = rplc(str,"<sc","<x-sc");
                     str = rplc(str,"<title","<x-title");
                     str = rplc(str,"<xmp","<x-xmp");
            break;
            case 11: // text 일때 CONENT 처리
                     str = rplc(str," ","&nbsp;");
                     str = rplc(str,"\n","<br>");
            break;
            case 13: // comment 저장 일때
                     str = rplc(str,"<sc","<x-sc");
                     str = rplc(str,"<title","<x-title");
                     str = rplc(str,"<xmp","<x-xmp");
                     str = rplc(str,"\n","<br>");
            break;
            case 14 : // text mode db 입력
                     str = rplc(str,"&quot;","\"");
            break;
        } 
        return str;
    }
    
    /**
     * replace 메소드
     * @param mainString 타겟
     * @param oldString 바꾸려는 값
     * @param newString 대체 값
     * @return 결과값
     */
    public static String rplc(String mainString, String oldString, String newString) { 
        if (mainString == null) {
            return null;
        }
        if (oldString == null || oldString.length() == 0) {
            return mainString;
        }
        if (newString == null) {
            newString = "";
        }
        
        int i = mainString.lastIndexOf(oldString);
        if (i < 0)return mainString;
        StringBuffer mainSb = new StringBuffer(mainString);
        while (i >= 0) {
            mainSb.replace(i, (i + oldString.length()), newString);
            i = mainString.lastIndexOf(oldString, i - 1);
        }
        return mainSb.toString();
    }
    
    /**
     * 검색시 red color로 변환하기
     * @param strSubject 타겟
     * @param flag 검색인지 확인
     * @param seaName 검색키
     * @param colName 칼럼명
     * @param strKeyword 검색어
     * @return 결과값
     */
    public static String searchWord(String strSubject,int flag,String seaName,String colName,String strKeyword){
        String alterKey = "<font color=red>" + strKeyword + "</font>" ;
        if ((flag == 1) && (seaName.equals(colName))){
            alterKey = rplc(strSubject,strKeyword,alterKey);
        }else{
            alterKey = strSubject;
        }
        return alterKey;
    }

    /**
     * 태그 제거하기
     */
    public static String removeTag(String str){ 
        int lt = str.indexOf("<"); 
        if ( lt != -1 ) { 
            int gt = str.indexOf(">", lt); 
            if ( (gt != -1) ) { 
                str = str.substring( 0, lt ) + str.substring( gt + 1 ); 
                str = removeTag(str); 
            } 
        } 
        return str; 
    } 
}
