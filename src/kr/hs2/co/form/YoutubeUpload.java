package kr.hs2.co.form;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import kr.hs2.co.util.DateUtil;
import kr.hs2.co.util.FileUtil;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class YoutubeUpload extends Activity {
	
    private static final int VIDEO_CAPTURE = 1;
    private static final int TAKE_GALLERY = 2;
    private String mVideoPath;
    private String mThumbnailPath;
    private int mVideoSize;
    
    private FileInputStream mFileInputStream = null;
	private URL connectUrl = null;
	private String lineEnd = "\r\n";
	private String twoHyphens = "--";
	private String boundary = "*****";
	
	private String ServerUrl = "";
	
	private ProgressDialog mProgressDialog = null;
	private UploadData mUploadData = null;
	private String mResult = "";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_upload);
        
        SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        ServerUrl = prefs.getString("key_url", "");
        
        Button btUpload = (Button)findViewById(R.id.btUpload);
        Button btClose = (Button)findViewById(R.id.btClose);
        Button btCapture = (Button)findViewById(R.id.btCapture) ;
        Button btAlbum = (Button)findViewById(R.id.btAlbum);

        btUpload.setOnClickListener(on_upload);
        btClose.setOnClickListener(on_close);
        btCapture.setOnClickListener(on_capture);
        btAlbum.setOnClickListener(on_album);
        
        mVideoPath = "";
    }
	
    private View.OnClickListener on_upload = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String sName = ((EditText)findViewById(R.id.etName)).getText().toString();
			String sTitle = ((EditText)findViewById(R.id.etTitle)).getText().toString();
			String sPassword = ((EditText)findViewById(R.id.etPassword)).getText().toString();
			String sContents = ((EditText)findViewById(R.id.etContents)).getText().toString();
			String sVodfile = "";
			String sImgfile = "";
			String sFilesize = "";
			
			if("".equals(sName)){ 
				showToast(YoutubeUpload.this,"�̸��� �Է��� �ּ���."); return;
			}
			if("".equals(sTitle)){ 
				showToast(YoutubeUpload.this,"������ �Է��� �ּ���."); return;
			}
			if("".equals(sPassword)){ 
				showToast(YoutubeUpload.this,"������ �Է��� �ּ���."); return;
			}
			if("".equals(mVideoPath)){
				showToast(YoutubeUpload.this,"������ ����� �ּ���."); return;
			}
			
			try {
				sName = URLEncoder.encode(sName,"EUC-KR");
				sTitle = URLEncoder.encode(sTitle,"EUC-KR");
				sPassword = URLEncoder.encode(sPassword,"EUC-KR");
				sContents = URLEncoder.encode(sContents,"EUC-KR");
				sVodfile = DateUtil.getNowDate() + "." + FileUtil.getExtension(mVideoPath);
				sVodfile = URLEncoder.encode(sVodfile,"EUC-KR");
				sImgfile = DateUtil.getNowDate() + "." + FileUtil.getExtension(mThumbnailPath);
				sImgfile = URLEncoder.encode(sImgfile,"EUC-KR");
				sFilesize = URLEncoder.encode(String.valueOf(mVideoSize),"EUC-KR");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			try{
				DoFileUpload("pvodfile="+sVodfile+"&pimgfile="+sImgfile+"&pfilesize="+sFilesize+"&pname="+sName+"&ptitle="+sTitle+"&ppassword="+sPassword+"&pcontents="+sContents);
			}catch(Exception e){}
		}
	};
    
    private View.OnClickListener on_close = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
    
	private View.OnClickListener on_capture = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, 
					MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString()); 
			startActivityForResult(intent, VIDEO_CAPTURE);
		}
	};
	
	private View.OnClickListener on_album = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_GET_CONTENT);
//		    intent.setDataAndType(Uri.parse(url), "video/mp4");
			intent.setType("video/*");
//			intent.setType("video/mp4");
		    startActivityForResult(intent, TAKE_GALLERY);
		}
	};
	
	private MediaPlayer.OnPreparedListener on_prepared = new MediaPlayer.OnPreparedListener() {
		@Override
		public void onPrepared(MediaPlayer mp) {
//			TextView _tvCapture = (TextView)findViewById(R.id.tvCapture);
//			tvCapture.setText("����ð� : " + arg0.getDuration());
			
			// ������ �̸����� �̹����� �����ֱ� ���� ���� �� 1�� �� ���� ��Ŵ
			mp.start();
			try{
				Thread.sleep(1000);
				mp.pause();
			}catch(Exception e){}
		}
	};
	
	@Override
	public void onActivityResult( int requestCode, int resultCode, Intent intent ) {
		super.onActivityResult(requestCode, resultCode, intent);
		
		VideoView videoView = (VideoView)findViewById(R.id.videoView);
		
		if (resultCode == RESULT_CANCELED) {
			showToast(this,"Activity cancelled");
			return;
		}
		switch (requestCode) {	
		case VIDEO_CAPTURE:
 			// ���� ���� ����
 			getVideoInfo(intent.getData());
 			
	        Uri video_cap = Uri.parse(getRealPathFromURI(intent.getData()));
	        videoView.setVideoURI(video_cap);
	        
	        MediaController mc_cap = new MediaController(this); 
	        mc_cap.setAnchorView(videoView);
	        videoView.setMediaController(mc_cap); 
	        videoView.requestFocus();
	        
	        videoView.setOnPreparedListener(on_prepared);
			break;
		case TAKE_GALLERY:
 			// ���� ���� ����
 			getVideoInfo(intent.getData());
 			
	        Uri video = Uri.parse(getRealPathFromURI(intent.getData()));
	        videoView.setVideoURI(video);
	        
	        MediaController mc = new MediaController(this); 
	        mc.setAnchorView(videoView);
	        videoView.setMediaController(mc); 
	        videoView.requestFocus();
	        
	        videoView.setOnPreparedListener(on_prepared);
			break;
		}
	}
	
	private String getRealPathFromURI(Uri contentUri){
		String[] proj = { 
			MediaStore.Video.Media._ID,
			MediaStore.Video.Media.DATA,
			MediaStore.Video.Media.DISPLAY_NAME,
			MediaStore.Video.Media.SIZE };
		Cursor cursor = managedQuery( contentUri, proj, null, null, null); 
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
	private void getVideoInfo(Uri uri){
		String[] proj = {
			MediaStore.Video.VideoColumns._ID,
			MediaStore.Video.VideoColumns.DATA,
			MediaStore.Video.VideoColumns.DISPLAY_NAME,
			MediaStore.Video.VideoColumns.SIZE
		};

		long videoId = 0;
//		String videoName = "";
		Cursor thumbCursor = managedQuery(uri, proj, null, null, null);

		if (thumbCursor != null && thumbCursor.moveToFirst()){
			int id = thumbCursor.getColumnIndex( MediaStore.Video.VideoColumns._ID);
			int pathId = thumbCursor.getColumnIndex( MediaStore.Video.VideoColumns.DATA);
//		    int nameId = thumbCursor.getColumnIndex( MediaStore.Video.VideoColumns.DISPLAY_NAME);
		    int filesize = thumbCursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.SIZE);

		    videoId   = thumbCursor.getLong( id );
//		    videoName = thumbCursor.getString( nameId );
		    mVideoPath = thumbCursor.getString( pathId ); // ������ Path ����
		    mVideoSize = thumbCursor.getInt( filesize ); // ������ ����ũ�� ����
		}
		thumbCursor.close();

		String[] THUMB_PROJECTION = new String[] {
				MediaStore.Video.Thumbnails._ID ,
		        MediaStore.Video.Thumbnails.DATA
		};
		Uri thumbUri = MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI;
		ContentResolver cr = getContentResolver();
		Cursor c = cr.query(thumbUri, THUMB_PROJECTION,
				MediaStore.Video.Thumbnails.VIDEO_ID + "=?",
		        new String[]{String.valueOf(videoId)}, null);  
		try {
			if (c.moveToNext()) {
				long id = c.getLong(0);
		    
				int dataId = c.getColumnIndex( MediaStore.Video.Thumbnails.DATA );
				String strThumPath = c.getString(dataId);
				mThumbnailPath = strThumPath; // ����� Path ����
				
				TextView _tvCapture = (TextView)findViewById(R.id.tvCapture);
				_tvCapture.setText("���ϰ�� : " + mVideoPath + "\n����ũ�� : " + String.valueOf(mVideoSize) + " bytes");
				
				Log.d("tag", "id = " + id );
				Log.d("tag", "strThumPath = " + strThumPath );
		    }
		} finally {
		    if (c != null) c.close();
		}
		
		// ����� �̸�����
//		ContentResolver crThumb = getContentResolver();
//		BitmapFactory.Options options=new BitmapFactory.Options();
//		options.inSampleSize = 1;
//		Bitmap bmp = MediaStore.Video.Thumbnails.getThumbnail(crThumb,videoId,MediaStore.Video.Thumbnails.MICRO_KIND,options);
//		if( bmp != null ){
//			ImageView ivThumbnail = (ImageView)findViewById(R.id.ivThumbnail);
//			ivThumbnail.setImageBitmap(bmp);
//		}
	}
	
	private void showToast(Context mContext, String text) {
		Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
	}
	
	private void DoFileUpload(String params) throws IOException {
		mUploadData = new UploadData();
		mUploadData.execute(params);
	}

	private void HttpFileUpload(String urlString , String fileName) {
		try{
			mFileInputStream = new FileInputStream(fileName);
			connectUrl = new URL(urlString);
			
//			Log.d("File Up" , "mFileInputStream is " + mFileInputStream);
			
			// open connection
			HttpURLConnection conn = (HttpURLConnection)connectUrl.openConnection();
			conn.setConnectTimeout(10000);
			conn.setDoInput(true); // �����͸� ÷���ϴ� ��� : true
			conn.setDoOutput(true); // post��� : true
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			
			// write data
			DataOutputStream dos = new DataOutputStream( conn.getOutputStream()) ;
		    dos.writeBytes(twoHyphens + boundary + lineEnd);

		    // uploadedfile ������ ashx �ڵ鷯���� ������ ã�� �� ��������� �̸��� �ݵ�� �����ؾ���.. 
		    // �̸��� �ٲٸ� ashx ���Ͽ����� �ٲܰ�.
			dos.writeBytes("Content-Disposition:form-data;name=\"uploadedfile\";filename=\"" + fileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);
			
			int bytesAvailable = mFileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			
			byte[] buffer = new byte[bufferSize];
			int bytesRead = mFileInputStream.read( buffer , 0 , bufferSize);
			
			Log.d("File Up", "File byte is " + bytesRead );
			
			// Read ����
			while(bytesRead > 0 ){
				dos.write(buffer , 0 , bufferSize);
				bytesAvailable = mFileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = mFileInputStream.read(buffer,0,bufferSize);
			}
			
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			
			//close streams
			Log.e("File Up" , "File is written");
			mFileInputStream.close();
			dos.flush(); // ���ۿ� �ִ� ���� ��� �о
			
			//���������� ������� �޾� �����ش�
			int ch;
			InputStream is = conn.getInputStream();
			StringBuffer b = new StringBuffer();
			while((ch = is.read()) != -1 ){
				b.append((char)ch);
			}
			String s = b.toString();
			Log.e("File Up" , "result = " + s);
			dos.close();
			
		} catch(Exception e) {
			//Log.d("File Up" , "exception : " + e.getMessage());
			Log.d("File Up" , "exception : " + e);
		}
	}
	
	private void HttpExecute(String urlString , String params) {
		
		StringBuilder result = new StringBuilder();
		
		try{
			connectUrl = new URL(urlString);
			
			// open connection
			HttpURLConnection conn = (HttpURLConnection)connectUrl.openConnection();
			conn.setDoInput(true); // �����͸� ÷���ϴ� ��� : true
			conn.setDoOutput(true); // post��� : true
			conn.setConnectTimeout(10000);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			
			/* �ƿ�ǲ */
			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
			osw.write(params); //�ٵ� ���ְ�
			osw.flush(); //�÷����� ������
			
			/* ��ǲ */
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			for(;;) {
				String line = br.readLine();
				if(line == null) break;
				result.append(line); //����б�
			}
			
			/* ��� �ݱ� */
			osw.close();
			br.close();
			conn.disconnect();
			
			/* ������ũ��Ʈ���� OK ���ڿ��� �������� �������� ó���ϰ� �� */
			if(result.toString().equals("Success")) {
				Log.e("Data Insert" , "result = ����");
				mResult = "Success";
			} else {
				Log.e("Data Insert" , "result = ����");
				mResult = "Failed";
			}
		} catch(Exception e) {
//			Log.d("Data Input" , "exception " + e.getMessage());
		}
	}
	
	// ������ ���ε� Thread
	class UploadData extends AsyncTask<String, Integer, String>{
	    
		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(YoutubeUpload.this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setMax(100);
			mProgressDialog.setTitle("��ø� ��ٷ� �ּ���.");
			mProgressDialog.setMessage("������ ���ε� �� ...");
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
			try{
				HttpFileUpload(ServerUrl + "/upload_youtube_img.ashx?"+url[0], mThumbnailPath); // ����� �̹��� ����
				HttpFileUpload(ServerUrl + "/upload_youtube_vod.ashx?"+url[0], mVideoPath); // ���� ����
				HttpExecute(ServerUrl + "/youtube_data_input.asp", url[0]); // �����͸� DB�� �Է�
			}catch(Exception e){}
	        return null;
	    }
	    
	    protected void onProgressUpdate(Integer... progress) {         
	    	mProgressDialog.setProgress(progress[0]);
	    }
	    
	    protected void onPostExecute(String result) { 
			if(mResult.equals("Success")) {
	    		mProgressDialog.dismiss();
	    		showToast(getApplicationContext(),"������ ��ϵǾ����ϴ�.");
				finish();
	    	}else{
		    	mProgressDialog.dismiss();
		    	showToast(getApplicationContext(),"���� ��Ͽ� �����Ͽ����ϴ�.");
				finish();
	    	}
	    }
	    
	    protected void onCancelled() {
	    	mProgressDialog.dismiss();
	    	super.onCancelled();
	    	showToast(getApplicationContext(),"���� ����� ��� �Ͽ����ϴ�.");
	    }
	}
}
