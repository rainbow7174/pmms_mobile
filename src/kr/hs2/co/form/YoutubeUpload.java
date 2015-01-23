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
				showToast(YoutubeUpload.this,"이름을 입력해 주세요."); return;
			}
			if("".equals(sTitle)){ 
				showToast(YoutubeUpload.this,"제목을 입력해 주세요."); return;
			}
			if("".equals(sPassword)){ 
				showToast(YoutubeUpload.this,"제목을 입력해 주세요."); return;
			}
			if("".equals(mVideoPath)){
				showToast(YoutubeUpload.this,"영상을 등록해 주세요."); return;
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
//			tvCapture.setText("재생시간 : " + arg0.getDuration());
			
			// 동영상 미리보기 이미지를 보여주기 위해 시작 후 1초 후 정지 시킴
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
 			// 비디오 정보 설정
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
 			// 비디오 정보 설정
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
		    mVideoPath = thumbCursor.getString( pathId ); // 동영상 Path 설정
		    mVideoSize = thumbCursor.getInt( filesize ); // 동영상 파일크기 설정
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
				mThumbnailPath = strThumPath; // 썸네일 Path 설정
				
				TextView _tvCapture = (TextView)findViewById(R.id.tvCapture);
				_tvCapture.setText("파일경로 : " + mVideoPath + "\n파일크기 : " + String.valueOf(mVideoSize) + " bytes");
				
				Log.d("tag", "id = " + id );
				Log.d("tag", "strThumPath = " + strThumPath );
		    }
		} finally {
		    if (c != null) c.close();
		}
		
		// 썸네일 미리보기
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
			conn.setDoInput(true); // 데이터를 첨부하는 경우 : true
			conn.setDoOutput(true); // post방식 : true
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			
			// write data
			DataOutputStream dos = new DataOutputStream( conn.getOutputStream()) ;
		    dos.writeBytes(twoHyphens + boundary + lineEnd);

		    // uploadedfile 파일이 ashx 핸들러에서 파일을 찾을 때 사용함으로 이름이 반드시 동일해야함.. 
		    // 이름을 바꾸면 ashx 파일에서도 바꿀것.
			dos.writeBytes("Content-Disposition:form-data;name=\"uploadedfile\";filename=\"" + fileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);
			
			int bytesAvailable = mFileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			
			byte[] buffer = new byte[bufferSize];
			int bytesRead = mFileInputStream.read( buffer , 0 , bufferSize);
			
			Log.d("File Up", "File byte is " + bytesRead );
			
			// Read 파일
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
			dos.flush(); // 버퍼에 있는 값을 모두 밀어냄
			
			//웹서버에서 결과값을 받아 보여준다
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
			conn.setDoInput(true); // 데이터를 첨부하는 경우 : true
			conn.setDoOutput(true); // post방식 : true
			conn.setConnectTimeout(10000);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			
			/* 아웃풋 */
			OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
			osw.write(params); //바디를 써주고
			osw.flush(); //플러쉬로 보내기
			
			/* 인풋 */
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			for(;;) {
				String line = br.readLine();
				if(line == null) break;
				result.append(line); //결과읽기
			}
			
			/* 모두 닫기 */
			osw.close();
			br.close();
			conn.disconnect();
			
			/* 서버스크립트에서 OK 문자열을 내뱉으면 성공으로 처리하게 함 */
			if(result.toString().equals("Success")) {
				Log.e("Data Insert" , "result = 성공");
				mResult = "Success";
			} else {
				Log.e("Data Insert" , "result = 실패");
				mResult = "Failed";
			}
		} catch(Exception e) {
//			Log.d("Data Input" , "exception " + e.getMessage());
		}
	}
	
	// 데이터 업로드 Thread
	class UploadData extends AsyncTask<String, Integer, String>{
	    
		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(YoutubeUpload.this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setMax(100);
			mProgressDialog.setTitle("잠시만 기다려 주세요.");
			mProgressDialog.setMessage("데이터 업로드 중 ...");
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
				HttpFileUpload(ServerUrl + "/upload_youtube_img.ashx?"+url[0], mThumbnailPath); // 썸네일 이미지 저장
				HttpFileUpload(ServerUrl + "/upload_youtube_vod.ashx?"+url[0], mVideoPath); // 비디오 저장
				HttpExecute(ServerUrl + "/youtube_data_input.asp", url[0]); // 데이터를 DB에 입력
			}catch(Exception e){}
	        return null;
	    }
	    
	    protected void onProgressUpdate(Integer... progress) {         
	    	mProgressDialog.setProgress(progress[0]);
	    }
	    
	    protected void onPostExecute(String result) { 
			if(mResult.equals("Success")) {
	    		mProgressDialog.dismiss();
	    		showToast(getApplicationContext(),"영상이 등록되었습니다.");
				finish();
	    	}else{
		    	mProgressDialog.dismiss();
		    	showToast(getApplicationContext(),"영상 등록에 실패하였습니다.");
				finish();
	    	}
	    }
	    
	    protected void onCancelled() {
	    	mProgressDialog.dismiss();
	    	super.onCancelled();
	    	showToast(getApplicationContext(),"영상 등록을 취소 하였습니다.");
	    }
	}
}
