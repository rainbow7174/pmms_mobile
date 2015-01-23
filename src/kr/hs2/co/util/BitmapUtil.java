package kr.hs2.co.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Hashtable;

import android.graphics.BitmapFactory;

public class BitmapUtil {
	
	/** Get Bitmap's Width (�Ķ���ͷ� URL�� ����) **/
	public static int getBitmapOfWidth( String _url ){
	    try {
	    	int retWidth = 0;
	    	InputStream is = new URL(_url).openStream();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
//			BitmapFactory.decodeStream(is, null, options);
			BitmapFactory.decodeStream(new FlushedInputStream(is), null, options);
			retWidth = options.outWidth;
			is.close();
			return retWidth;
	    } catch(Exception e) {
	    return 0;
	    }
	}
	
	/** Get Bitmap's Width (�Ķ���ͷ� URL�� ����) **/
	public static int getBitmapOfHeight( String _url ){
	    try {
	    	int retHeight = 0;
	    	InputStream is = new URL(_url).openStream();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
//			BitmapFactory.decodeStream(is, null, options);
			BitmapFactory.decodeStream(new FlushedInputStream(is), null, options);
			retHeight = options.outHeight;
			is.close();
			return retHeight;
	    } catch(Exception e) {
	    return 0;
	    }
	}
	
	/** Get Bitmap's Width (�Ķ���ͷ� ���ϸ��� ����) **/
	public static int getBitmapOfWidth2( String fileName ){
	    try {
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        BitmapFactory.decodeFile(fileName, options);
	        return options.outWidth;
	    } catch(Exception e) {
	    return 0;
	    }
	}
	 
	/** Get Bitmap's height (�Ķ���ͷ� ���ϸ��� ���� **/
	public static int getBitmapOfHeight2( String fileName ){
	    try {
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        BitmapFactory.decodeFile(fileName, options);
	        return options.outHeight;
	    } catch(Exception e) {
	        return 0;
	   }
	}
	
	public static Hashtable<String,String> getBitmapResizeToSmall(int origin_x, int origin_y) {
		
        int album_x = 130; // ������̹��� Width
        int album_y = 98; // ������̹��� Height

//      int album_x = 100; // ������̹��� Width
//		int album_y = 75; // ������̹��� Height
		
        int resize_x = 0; // ��ȯ�� �̹��� Width
        int resize_y = 0; // ��ȯ�� �̹��� Height

        if (origin_x > origin_y)
        {   //���λ���� ���λ������ ũ��
        	resize_x = (int)((album_y * origin_x) / origin_y);
        	resize_y = (int)((album_x * origin_y) / origin_x);
            if (resize_x > album_x) resize_x = album_x;
            if (resize_y > album_y) resize_y = album_y;
        }
        else if (origin_y > origin_x)
        {   //���λ���� ���λ������ ũ��
        	resize_x = (int)((album_y * origin_x) / origin_y);
        	resize_y = album_y;
        }
        else
        {   //�׿�
        	resize_x = album_y;
        	resize_y = album_y;
        }
        
        Hashtable<String,String> table = new Hashtable<String,String>(); 
        table.put("width", Integer.toString(resize_x));
        table.put("height", Integer.toString(resize_y));
        
		return table;
	}
	
	public static Hashtable<String,String> getBitmapResizeToView(int origin_x, int origin_y) {
        int album_x = 800; // ���̹��� Width
        int album_y = 600; // ���̹��� Height
        
        int resize_x = 0; // ��ȯ�� �̹��� Width
        int resize_y = 0; // ��ȯ�� �̹��� Height

        if (origin_x > origin_y)
        {   //���λ���� ���λ������ ũ��
        	resize_x = (int)((album_y * origin_x) / origin_y);
        	resize_y = (int)((album_x * origin_y) / origin_x);
            if (resize_x > album_x) resize_x = album_x;
            if (resize_y > album_y) resize_y = album_y;
        }
        else if (origin_y > origin_x)
        {   //���λ���� ���λ������ ũ��
        	resize_x = (int)((album_y * origin_x) / origin_y);
        	resize_y = album_y;
        }
        else
        {   //�׿�
        	resize_x = album_y;
        	resize_y = album_y;
        }
        
        Hashtable<String,String> table = new Hashtable<String,String>(); 
        table.put("width", Integer.toString(resize_x));
        table.put("height", Integer.toString(resize_y));
        
        return table;
	}
	
	public static Hashtable<String,String> getBitmapResizeToMiddle(int origin_x, int origin_y) {
        int album_x = 290; // �߰��̹��� Width
        int album_y = 218; // �߰��̹��� Height
        
        int resize_x = 0; // ��ȯ�� �̹��� Width
        int resize_y = 0; // ��ȯ�� �̹��� Height

        if (origin_x > origin_y)
        {   //���λ���� ���λ������ ũ��
        	resize_x = (int)((album_y * origin_x) / origin_y);
        	resize_y = (int)((album_x * origin_y) / origin_x);
            if (resize_x > album_x) resize_x = album_x;
            if (resize_y > album_y) resize_y = album_y;
        }
        else if (origin_y > origin_x)
        {   //���λ���� ���λ������ ũ��
        	resize_x = (int)((album_y * origin_x) / origin_y);
        	resize_y = album_y;
        }
        else
        {   //�׿�
        	resize_x = album_y;
        	resize_y = album_y;
        }
        
        Hashtable<String,String> table = new Hashtable<String,String>(); 
        table.put("width", Integer.toString(resize_x));
        table.put("height", Integer.toString(resize_y));
        
        return table;
	}

	public static void CopyStream(InputStream is, OutputStream os){
		final int buffer_size = 1024;
		try{
			byte[] bytes = new byte[buffer_size];
			for(;;)
			{
				int count= is.read(bytes, 0, buffer_size);
				if (count==-1)
					break;
				os.write(bytes,0,count);
			}
		}catch(Exception ex){}
	}
	
	// Ư�� �̹��� decode ������ ���� 
	static class FlushedInputStream extends FilterInputStream {
		
		 public FlushedInputStream(InputStream inputStream) {
	         super(inputStream);
	     }

	     @Override
	     public long skip(long n) throws IOException {
	         long totalBytesSkipped = 0L;
	         while (totalBytesSkipped < n) {
	             long bytesSkipped = in.skip(n - totalBytesSkipped);
	             if (bytesSkipped == 0L) {
	                 int b = read();
	                 if (b < 0) {
	                     break;  // we reached EOF
	                 } else {
	                     bytesSkipped = 1; // we read one byte
	                 }
	             }
	             totalBytesSkipped += bytesSkipped;
	         }
	         return totalBytesSkipped;
	     }
	}
}
