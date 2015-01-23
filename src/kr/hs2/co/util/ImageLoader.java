package kr.hs2.co.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Stack;

import kr.hs2.co.form.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader {
	
	private HashMap<String, Bitmap> cache = new HashMap<String,Bitmap>();
	
	private File cacheDir;
	//ui handle 을 위해 작성
	private Handler handler = new Handler();  
	
	public ImageLoader(Context context){
		//유아이 실행에 영향을 안주기위해 쓰레드를 낮춰줌
		photoLoaderThread.setPriority(Thread.NORM_PRIORITY -1);
		
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(),"LazyList");
		else
			cacheDir = context.getCacheDir();
		
		if(!cacheDir.exists())
			cacheDir.mkdir();
	}
	
	final int stub_id = R.drawable.icon;
	
	//캐쉬에 저장되어있으면 불러다 쓰고 없을시에는  기본이미지를 띄움
	public void DisplayImage(String url, Activity activity, ImageView imageView){
		if(cache.containsKey(url))
			imageView.setImageBitmap(cache.get(url));
		else{
			queuePhoto(url,activity,imageView);
			imageView.setImageResource(stub_id);
		}
	}
	
	private void queuePhoto(String url, Activity activity, ImageView imageView){
		photosQueue.Clean(imageView);
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		synchronized (photosQueue.photosToLoad) {
			photosQueue.photosToLoad.push(p);
			photosQueue.photosToLoad.notifyAll();
		}
		
		if(photoLoaderThread.getState() == Thread.State.NEW)
			photoLoaderThread.start();
	}
	
	
	private Bitmap getBitmap(String url){
		
		String filename = String.valueOf(url.hashCode());
		File f = new File(cacheDir,filename);
		
		Bitmap b = decodeFile(f);
		if(b!= null)
			return b;
		
		//내부파일인지 웹 파일인지 구분
		if(url.contains("/sdcard")){
			File file = new File(url);
			Bitmap bitmap = decodeFile(file);
			Log.i("sdcard","sdcard");
			return bitmap;
		}else{
			try{
				Bitmap bitmap = null;
				InputStream is = new URL(url).openStream();
				OutputStream os = new FileOutputStream(f);
				BitmapUtil.CopyStream(is, os);
				
				bitmap = decodeFile(f);
				return bitmap;
				
			}catch(Exception ex){
				ex.printStackTrace();
				return null;
			}			
		}
	}
	
	private Bitmap decodeFile(File f){
		try{
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f),null,o);
			
			final int REQUIRED_SIZE = 70;
			int width_tmp =o.outWidth, height_tmp= o.outHeight;
			int scale=1;
			
			while(true){
				if(width_tmp/2 < REQUIRED_SIZE || height_tmp/2 < REQUIRED_SIZE)
					break;
				width_tmp/=2;
				height_tmp/=2;
				scale++;
			}
			
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			
			return BitmapFactory.decodeStream(new FileInputStream(f),null,o2);
		}catch(FileNotFoundException e){}
		return null;
	}
	
	
	private class PhotoToLoad{
		public String url;
		public ImageView imageView;
		public PhotoToLoad(String u, ImageView i){
			url = u;
			imageView = i;
		}
	}
	
	PhotosQueue photosQueue = new PhotosQueue();
	
	public void stopThread(){
		photoLoaderThread.interrupt();
	}
	
	class PhotosQueue{
		private Stack<PhotoToLoad> photosToLoad = new Stack<PhotoToLoad>();
		public void Clean(ImageView image){
			for(int j=0;j<photosToLoad.size();){
				if(photosToLoad.get(j).imageView == image)
					photosToLoad.remove(j);
				else
					++j;
			}
		}
	}
	
	class PhotosLoader extends Thread{
		public void run(){
			try{
				while(true){
					if(photosQueue.photosToLoad.size()==0)
						synchronized (photosQueue.photosToLoad) {
							photosQueue.photosToLoad.wait();
						}
					if(photosQueue.photosToLoad.size()!=0){
						PhotoToLoad photoToLoad;
						synchronized (photosQueue.photosToLoad) {
							photoToLoad = photosQueue.photosToLoad.pop();
						}
						Bitmap bmp = getBitmap(photoToLoad.url);
						cache.put(photoToLoad.url, bmp);
//						if(((String)photoToLoad.imageView.getTag()).equals(photoToLoad.url)){
							BitmapDisplayer bd = new BitmapDisplayer(bmp,photoToLoad.imageView);
							handler.post(bd);
//						}
					}
					
					if(Thread.interrupted())
						break;
				}
				
			}catch(InterruptedException e){
				
			}
		}
		
	}
	
	PhotosLoader photoLoaderThread = new PhotosLoader();
	
	class BitmapDisplayer implements Runnable{
		
		Bitmap bitmap;
		ImageView imageView;
		
		public BitmapDisplayer(Bitmap bitmap, ImageView imageView){this.bitmap = bitmap; this.imageView= imageView;}

		@Override
		public void run() {
			if(bitmap!=null){
				imageView.setImageBitmap(bitmap);
			}else
				imageView.setImageResource(stub_id);
		}
		
	}
	
	//엑티비티 데스토리 하실때 쓰시면 됨
	public void clearCache(){
		cache.clear();
		File[] files = cacheDir.listFiles();
		for(File f: files)
			f.delete();
	}
}

