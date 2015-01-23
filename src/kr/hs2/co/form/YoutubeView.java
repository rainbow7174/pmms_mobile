package kr.hs2.co.form;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import kr.hs2.co.form.GalleryView.FlushedInputStream;
import kr.hs2.co.vo.MovieVO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class YoutubeView extends Activity {

	private String ServerUrl = "";
	
	private MovieVO xmlData = null;
	private ProgressBar mProgress = null;
	private DownloadFile downloadFile = null;
	
	private TextView tvPercent, tvVolume;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.youtube_view);
		
		SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        ServerUrl = prefs.getString("key_url", "");
        
		String idx = getIntent().getExtras().getString("idx"); 
//		Log.v("Tag", "IDX : " + idx);
		searchXml(idx);
		
		downloadFile = new DownloadFile();
		mProgress = (ProgressBar)findViewById(R.id.pbDownload);
		tvPercent = (TextView)findViewById(R.id.tvPercent);
		tvVolume = (TextView)findViewById(R.id.tvVolume);
		
		if(xmlData != null) {
			ImageView ivMPhoto = (ImageView)findViewById(R.id.ivMPhoto);
			TextView tvDetail = (TextView)findViewById(R.id.tvTitle);
			TextView tvRegdate= (TextView)findViewById(R.id.tvRegdate);
			TextView tvFilesize = (TextView)findViewById(R.id.tvFilesize);
			TextView tvContents = (TextView)findViewById(R.id.tvContents);
			
			tvDetail.setText(xmlData.getTitle());
			tvRegdate.setText(xmlData.getRegdate());
			tvFilesize.setText(xmlData.getFilesize());
			tvContents.setText(xmlData.getMemo());
			
			try {
				InputStream is = new URL(xmlData.getImgfile()).openStream();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				Bitmap bm = BitmapFactory.decodeStream(new FlushedInputStream(is), null, options);
				Bitmap resized = Bitmap.createScaledBitmap(bm, 290, 174, true);
				ivMPhoto.setScaleType(ImageView.ScaleType.FIT_CENTER);
				ivMPhoto.setImageBitmap(resized);
				is.close();
			} catch (Exception e) {}
		}
		
		((ImageButton)findViewById(R.id.btPrev)).setOnClickListener(on_prev);
		((Button)findViewById(R.id.btPlayer)).setOnClickListener(on_play);
		((Button)findViewById(R.id.btDownload)).setOnClickListener(on_save);
		((ImageButton)findViewById(R.id.ibtCancel)).setOnClickListener(on_cancel);
	}
	
	private View.OnClickListener on_prev = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	
	private View.OnClickListener on_play = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(YoutubeView.this,MoviePlayer.class);
			intent.putExtra("filename",xmlData.getVodfile());
			
			Log.e("tag","vodifile : "+xmlData.getVodfile());
			startActivity(intent);
		}
	};
	
	private View.OnClickListener on_save = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			TableLayout tblLayout = (TableLayout) findViewById(R.id.tloDownload);
			tblLayout.setVisibility(View.VISIBLE);
			
			downloadFile.execute(xmlData.getVodfile());
		}
	};
	
	private View.OnClickListener on_cancel = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			downloadFile.cancel(true);
		}
	};
	
	private void searchXml(String idx){
		
		String m_sConnectUrl = ServerUrl + "/youtube_data_view.asp?idx="+idx;
		
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
						xmlData = new MovieVO();
						xmlData.setIdx(Integer.parseInt(xpp.nextText()));
					}
					if(sTag.equals("title")){
						xmlData.setTitle(xpp.nextText());
					}
					if(sTag.equals("filename")){
						xmlData.setFilename(xpp.nextText());
					}
					if(sTag.equals("imgfile")){
						xmlData.setImgfile(xpp.nextText());
					}
					if(sTag.equals("vodfile")){
						xmlData.setVodfile(xpp.nextText());
					}
					if(sTag.equals("regdate")){
						xmlData.setRegdate(xpp.nextText());
					}
					if(sTag.equals("filesize")){
						xmlData.setFilesize(xpp.nextText());
					}
					if(sTag.equals("contents")){
						xmlData.setMemo(xpp.nextText());
					}
					
					break;
				case XmlPullParser.END_TAG:
					sTag = xpp.getName();
					
					if(sTag.equals("item")){
//						m_xmlData.add(xmlData);
//						xmlData = null;
					}
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
	
	class DownloadFile extends AsyncTask<String, String, String> {
		
		@Override
		protected void onPreExecute() {
			mProgress.setIndeterminate(false);
			mProgress.setMax(100);
			mProgress.setProgress(0);
		}

		@Override
		protected String doInBackground(String... url) {
			
			int percent = 0;
			String totalVolume = "0";
			String downloadVolume = "0";
			
			int count;
			InputStream is = null;
			OutputStream os = null;
			try {
				URL fileurl = new URL(url[0]);
				URLConnection connect = fileurl.openConnection();
				connect.connect();
				int lengthOfFile = connect.getContentLength();
				
				totalVolume = String.format("%.2f", (float)lengthOfFile/(1024*1024));
				
				byte[] data = new byte[1024];
				String filename = Environment.getExternalStorageDirectory() + "/" + xmlData.getFilename();
				
				is = new BufferedInputStream(fileurl.openStream());
				os = new FileOutputStream(filename);
				
				long total = 0;
				
				while ((count = is.read(data)) != -1) {
					total += count;
					
					downloadVolume = String.format("%.2f", (float)total/(1024*1024));
					percent = (int)(total*100/lengthOfFile);
					
					// 취소버튼 클릭시
					if (isCancelled()) {
						try {
							if (is != null) is.close();
							if (os != null) os.close();
						} catch (Exception ie) {}
						return null;
					}
						
					Log.v("Tag","total : " + String.valueOf(total) + ", D : " +String.valueOf((int)(total*100/lengthOfFile)) );
						
					if (percent <= 100) {
						publishProgress(String.valueOf(percent),downloadVolume,totalVolume);
					} else {
						break;
					}
					os.write(data, 0, count);
					os.flush();
				}
			} catch (MalformedURLException e) {
				Log.v("Tag","MalformedURLException : " + e.toString());
			} catch (IOException e) {
				Log.v("Tag","IOException : " + e.toString());
			} finally {
				try {
					if (is != null) is.close();
					if (os != null) os.close();
				} catch (Exception ie) {}
			}
			
			return null;
		}
		
		protected void onProgressUpdate(String... progress) {         
	    	mProgress.setProgress(Integer.parseInt(progress[0]));
	    	tvPercent.setText(progress[0] + "%");
	    	tvVolume.setText(progress[1]+" Mbytes "+"/"+progress[2]+" Mbytes");
	    }
	    
	    protected void onPostExecute(String result) {
	    	TableLayout tblLayout = (TableLayout) findViewById(R.id.tloDownload);
			tblLayout.setVisibility(View.GONE);
	    	Toast.makeText(getApplicationContext(), "정상적으로 파일을 다운로드 하였습니다.", Toast.LENGTH_SHORT).show();
	    }
	    
	    protected void onCancelled() {
	    	super.onCancelled();
	    	
	    	try{
	    		new File(Environment.getExternalStorageDirectory() + "/" + xmlData.getFilename()).delete();
	    	} catch (Exception e) {}
	    	
	    	TableLayout tblLayout = (TableLayout) findViewById(R.id.tloDownload);
			tblLayout.setVisibility(View.GONE);
			Toast.makeText(getApplicationContext(), "파일 다운로드를 취소 하였습니다.", Toast.LENGTH_SHORT).show();
	    }
		
	}
}
