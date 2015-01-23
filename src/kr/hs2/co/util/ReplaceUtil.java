package kr.hs2.co.util;

public class ReplaceUtil {
	/**
     * DB ��.��½� ��ȯó��
     * @param str ��ȯŸ��
     * @param n ��ȯcase
     * @return �����
     */
    public static String encodeHTMLSpecialChar(String str,int n) {
        switch (n){
            case 1 : // text mode db �Է�
                     str = rplc(str,"<","&lt;");
                     str = rplc(str,"\"","&quot;");
            break;
            case 2 : // html mode db �Է�
                     str = rplc(str,"<sc","<x-sc");
                     str = rplc(str,"<title","<x-title");
                     str = rplc(str,"<xmp","<x-xmp");
            break;
            case 11: // text �϶� CONENT ó��
                     str = rplc(str," ","&nbsp;");
                     str = rplc(str,"\n","<br>");
            break;
            case 13: // comment ���� �϶�
                     str = rplc(str,"<sc","<x-sc");
                     str = rplc(str,"<title","<x-title");
                     str = rplc(str,"<xmp","<x-xmp");
                     str = rplc(str,"\n","<br>");
            break;
            case 14 : // text mode db �Է�
                     str = rplc(str,"&quot;","\"");
            break;
        } 
        return str;
    }
    
    /**
     * replace �޼ҵ�
     * @param mainString Ÿ��
     * @param oldString �ٲٷ��� ��
     * @param newString ��ü ��
     * @return �����
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
     * �˻��� red color�� ��ȯ�ϱ�
     * @param strSubject Ÿ��
     * @param flag �˻����� Ȯ��
     * @param seaName �˻�Ű
     * @param colName Į����
     * @param strKeyword �˻���
     * @return �����
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
     * �±� �����ϱ�
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
