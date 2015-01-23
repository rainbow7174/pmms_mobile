package kr.hs2.co.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Hashtable;

import android.graphics.BitmapFactory;

public class BitmapUtil {
	
	/** Get Bitmap's Width (파라미터로 URL을 받음) **/
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
	
	/** Get Bitmap's Width (파라미터로 URL을 받음) **/
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
	
	/** Get Bitmap's Width (파라미터로 파일명을 받음) **/
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
	 
	/** Get Bitmap's height (파라미터로 파일명을 받음 **/
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
		
        int album_x = 130; // 썸네일이미지 Width
        int album_y = 98; // 썸네일이미지 Height

//      int album_x = 100; // 썸네일이미지 Width
//		int album_y = 75; // 썸네일이미지 Height
		
        int resize_x = 0; // 변환한 이미지 Width
        int resize_y = 0; // 변환한 이미지 Height

        if (origin_x > origin_y)
        {   //가로사이즈가 세로사이즈보다 크면
        	resize_x = (int)((album_y * origin_x) / origin_y);
        	resize_y = (int)((album_x * origin_y) / origin_x);
            if (resize_x > album_x) resize_x = album_x;
            if (resize_y > album_y) resize_y = album_y;
        }
        else if (origin_y > origin_x)
        {   //세로사이즈가 가로사이즈보다 크면
        	resize_x = (int)((album_y * origin_x) / origin_y);
        	resize_y = album_y;
        }
        else
        {   //그외
        	resize_x = album_y;
        	resize_y = album_y;
        }
        
        Hashtable<String,String> table = new Hashtable<String,String>(); 
        table.put("width", Integer.toString(resize_x));
        table.put("height", Integer.toString(resize_y));
        
		return table;
	}
	
	public static Hashtable<String,String> getBitmapResizeToView(int origin_x, int origin_y) {
        int album_x = 800; // 뷰이미지 Width
        int album_y = 600; // 뷰이미지 Height
        
        int resize_x = 0; // 변환한 이미지 Width
        int resize_y = 0; // 변환한 이미지 Height

        if (origin_x > origin_y)
        {   //가로사이즈가 세로사이즈보다 크면
        	resize_x = (int)((album_y * origin_x) / origin_y);
        	resize_y = (int)((album_x * origin_y) / origin_x);
            if (resize_x > album_x) resize_x = album_x;
            if (resize_y > album_y) resize_y = album_y;
        }
        else if (origin_y > origin_x)
        {   //세로사이즈가 가로사이즈보다 크면
        	resize_x = (int)((album_y * origin_x) / origin_y);
        	resize_y = album_y;
        }
        else
        {   //그외
        	resize_x = album_y;
        	resize_y = album_y;
        }
        
        Hashtable<String,String> table = new Hashtable<String,String>(); 
        table.put("width", Integer.toString(resize_x));
        table.put("height", Integer.toString(resize_y));
        
        return table;
	}
	
	public static Hashtable<String,String> getBitmapResizeToMiddle(int origin_x, int origin_y) {
        int album_x = 290; // 중간이미지 Width
        int album_y = 218; // 중간이미지 Height
        
        int resize_x = 0; // 변환한 이미지 Width
        int resize_y = 0; // 변환한 이미지 Height

        if (origin_x > origin_y)
        {   //가로사이즈가 세로사이즈보다 크면
        	resize_x = (int)((album_y * origin_x) / origin_y);
        	resize_y = (int)((album_x * origin_y) / origin_x);
            if (resize_x > album_x) resize_x = album_x;
            if (resize_y > album_y) resize_y = album_y;
        }
        else if (origin_y > origin_x)
        {   //세로사이즈가 가로사이즈보다 크면
        	resize_x = (int)((album_y * origin_x) / origin_y);
        	resize_y = album_y;
        }
        else
        {   //그외
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
	
	// 특정 이미지 decode 에러로 인해 
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
