package kr.hs2.co.form;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import kr.hs2.co.vo.PhotoVO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoView extends Activity {

	private String ServerUrl = "";
	
	private PhotoVO xmlData = null;
	private ProgressDialog mProgressDialog = null;
	private DownloadFile downloadFile = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_view);
		
		SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        ServerUrl = prefs.getString("key_url", "");
        
		String idx = getIntent().getExtras().getString("idx"); 
		
		Log.v("Tag", "IDX : " + idx);
		
		searchXml(idx);
		
		((ImageButton)findViewById(R.id.btPrev)).setOnClickListener(on_prev);
		((Button)findViewById(R.id.btDownload)).setOnClickListener(on_save);
		((Button)findViewById(R.id.btExpand)).setOnClickListener(on_expand);
		
		if(xmlData != null) {
			ImageView ivMPhoto = (ImageView)findViewById(R.id.ivMPhoto);
			TextView tvDetail = (TextView)findViewById(R.id.tvTitle);
			TextView tvTakedate = (TextView)findViewById(R.id.tvTakedate);
			TextView tvImgsize = (TextView)findViewById(R.id.tvImgsize);
			TextView tvFilesize = (TextView)findViewById(R.id.tvFilesize);
			TextView tvMemo = (TextView)findViewById(R.id.tvMemo);
			
			tvDetail.setText(xmlData.getTitle());
			tvTakedate.setText(xmlData.getTakedate());
			tvImgsize.setText(xmlData.getImgsize());
			tvFilesize.setText(xmlData.getFilesize());
			tvMemo.setText(xmlData.getMemo());
			
			try {
				InputStream is = new URL(xmlData.getThumbnail()).openStream();
				Bitmap bm = BitmapFactory.decodeStream(is);
//				Bitmap bm = BitmapFactory.decodeStream(new FlushedInputStream(is));
				ivMPhoto.setImageBitmap(bm);
				is.close();
			} catch (Exception e) {}
		}
	}
	
	private View.OnClickListener on_prev = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	
	private View.OnClickListener on_save = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			downloadFile = new DownloadFile();
			downloadFile.execute(xmlData.getOImgUrl());
		}
	};
	
	private View.OnClickListener on_expand = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			
			final LinearLayout linear = (LinearLayout)
			View.inflate(PhotoView.this, R.layout.dialog_imageview, null);
		
			AlertDialog.Builder ab = new AlertDialog.Builder(PhotoView.this);
			ab.setTitle("��������");
			ab.setView(linear);
			
			ImageView image = (ImageView)linear.findViewById(R.id.ivPhoto);
			try {
				InputStream is = new URL(xmlData.getOImgUrl()).openStream();
//				Bitmap bm = BitmapFactory.decodeStream(is);
				Bitmap bm = BitmapFactory.decodeStream(new FlushedInputStream(is));
				image.setImageBitmap(bm);
				is.close();
			} catch (Exception e) {}
			
			ab.setPositiveButton("ok", null);
			ab.show();
			
		}
	};

	private void searchXml(String idx){
		
		String m_sConnectUrl = ServerUrl + "/photo_data_view.asp?idx="+idx;
		
		Log.d("TAG",m_sConnectUrl);
		
		String sTag;
		
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			
			URL url = new URL(m_sConnectUrl);
			InputStream in = url.openStream();
			//xpp.setInput(in, "utf-8");
			xpp.setInput(in, "euc-kr");
			
			int eventType = xpp.getEventType();
			
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.END_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					sTag = xpp.getName();
					
					//Log.d("TAG",sTag);
					
					if(sTag.equals("idx")){
						xmlData = new PhotoVO();
						xmlData.setIdx(Integer.parseInt(xpp.nextText()));
					}
					if(sTag.equals("title")){
						xmlData.setTitle(xpp.nextText());
					}
					if(sTag.equals("filename")){
						xmlData.setFilename(xpp.nextText());
					}
					if(sTag.equals("mphoto")){
						xmlData.setThumbnail(xpp.nextText());
					}
					if(sTag.equals("ophoto")){
						xmlData.setOImgUrl(xpp.nextText());
					}
					if(sTag.equals("takedate")){
						xmlData.setTakedate(xpp.nextText());
					}
					if(sTag.equals("imgsize")){
						xmlData.setImgsize(xpp.nextText());
					}
					if(sTag.equals("filesize")){
						xmlData.setFilesize(xpp.nextText());
					}
					if(sTag.equals("memo")){
						xmlData.setMemo(xpp.nextText());
					}
					
					break;
				case XmlPullParser.END_TAG:
//					sTag = xpp.getName();
//					
//					if(sTag.equals("item")){
//						m_xmlData.add(xmlData);
//						xmlData = null;
//					}
					break;
				case XmlPullParser.TEXT:
					break;
				default:
					break;
				}
				eventType = xpp.next();
			}
			
		} catch (Exception e) {
		}
		
	}
	
	// ���� �ٿ�ε� Ŭ����
	class DownloadFile extends AsyncTask<String, Integer, String>{
	    
		private int percent = 0;
		
		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(PhotoView.this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setMax(100);
			mProgressDialog.setTitle("��ø� ��ٷ� �ּ���.");
			mProgressDialog.setMessage("���� �ٿ�ε� �� ...");
			mProgressDialog.setCancelable(false);
			mProgressDialog.setProgress(0);
			mProgressDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					cancel(true);
				}
			});
			mProgressDialog.show();
		}
		
		@Override
	    protected String doInBackground(String... url) {
//			while (isCancelled() == false) { 
				int count;
				InputStream is = null;
//				FileOutputStream fos = null;
				OutputStream os = null;
		        try {
		            URL fileurl = new URL(url[0]);
		            URLConnection connect = fileurl.openConnection();
		            connect.connect();
		            // show 0-100% progress bar
		            int lenghtOfFile = connect.getContentLength();
		            
//		            Log.v("Tag","lenghtOfFile : " + String.valueOf(lenghtOfFile));
		            
		            byte data[] = new byte[1024];
		            String filename = Environment.getExternalStorageDirectory() + "/" + xmlData.getFilename();
		            
		            is = new BufferedInputStream(fileurl.openStream());
		            os = new FileOutputStream(filename);
	
		            long total = 0;
	
		            while ((count = is.read(data)) != -1) {
		                total += count;

		                percent = (int)(total*100/lenghtOfFile);
		                
		                if (isCancelled() == false) {
		                	
//		                	Log.v("Tag","total : " + String.valueOf(total) + ", D : " +String.valueOf((int)(total*100/lenghtOfFile)) );
		                	
			                if (percent <= 100) {
								publishProgress(percent);
							} else {
								break;
							}
			                os.write(data, 0, count);
			                os.flush();
		                }
		            }
		        } catch (MalformedURLException e) {
					Log.v("Tag","MalformedURLException : " + e.toString());    
		        } catch (IOException e) {
		        	Log.v("Tag","IOException : " + e.toString());
				} finally {
					try { 
						if (is != null) is.close();
						if (os != null) os.close();
					} catch(IOException ie) {}
				}
//			}
	        return null;
	    }
	    
	    protected void onProgressUpdate(Integer... progress) {         
	    	mProgressDialog.setProgress(progress[0]);
//	    	mProgressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.progressbar_custom));;
	    }
	    
	    protected void onPostExecute(String result) { 
	    	mProgressDialog.dismiss();
	    	Toast.makeText(getApplicationContext(), "���������� ������ �ٿ�ε� �Ͽ����ϴ�.", Toast.LENGTH_SHORT).show();
	    }
	    
	    protected void onCancelled() {
	    	mProgressDialog.dismiss();
	    	super.onCancelled();
			Toast.makeText(getApplicationContext(), "���� �ٿ�ε带 ��� �Ͽ����ϴ�.", Toast.LENGTH_SHORT).show();
	    }
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
