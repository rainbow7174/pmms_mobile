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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class GalleryUpload extends Activity {
	
    private static final int CAMERA_CAPTURE = 0;
    private static final int TAKE_GALLERY = 2;
    private Intent mIntent;
    private String mGalleryPath;
    
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
        setContentView(R.layout.gallery_upload);
        
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
        
        mIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        mGalleryPath = "";
    }
	
    private View.OnClickListener on_upload = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String sName = ((EditText)findViewById(R.id.etName)).getText().toString();
			String sTitle = ((EditText)findViewById(R.id.etTitle)).getText().toString();
			String sPassword = ((EditText)findViewById(R.id.etPassword)).getText().toString();
			String sContents = ((EditText)findViewById(R.id.etContents)).getText().toString();
			String sFilename = "";
			
			if("".equals(sName)){ 
				showToast(GalleryUpload.this,"�̸��� �Է��� �ּ���."); return;
			}
			if("".equals(sTitle)){ 
				showToast(GalleryUpload.this,"������ �Է��� �ּ���."); return;
			}
			if("".equals(sPassword)){ 
				showToast(GalleryUpload.this,"������ �Է��� �ּ���."); return;
			}
			if("".equals(mGalleryPath)){
				showToast(GalleryUpload.this,"������ ����� �ּ���."); return;
			}
			
			try {
				sName = URLEncoder.encode(sName,"EUC-KR");
				sTitle = URLEncoder.encode(sTitle,"EUC-KR");
				sPassword = URLEncoder.encode(sPassword,"EUC-KR");
				sContents = URLEncoder.encode(sContents,"EUC-KR");
				sFilename = DateUtil.getNowDate() + "." + FileUtil.getExtension(mGalleryPath);
				sFilename = URLEncoder.encode(sFilename,"EUC-KR");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			try{
				DoFileUpload("pfilename="+sFilename+"&pname="+sName+"&ptitle="+sTitle+"&ppassword="+sPassword+"&pcontents="+sContents);
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
			mIntent.putExtra(MediaStore.EXTRA_OUTPUT, 
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()); 
			startActivityForResult(mIntent, CAMERA_CAPTURE);
		}
	};
	
	private View.OnClickListener on_album = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent i = new Intent();
			i.setAction(Intent.ACTION_GET_CONTENT);
			i.setType("image/*");
			startActivityForResult(i, TAKE_GALLERY);
			
//			Intent intent = new Intent(Intent.ACTION_PICK);
//		    intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
//		    startActivityForResult(intent, TAKE_GALLERY);
		}
	};

	@Override
	public void onActivityResult( int requestCode, int resultCode, Intent intent ) {
		super.onActivityResult(requestCode, resultCode, intent);
		
		if (resultCode == RESULT_CANCELED) {
			showToast(this,"Activity cancelled");
			return;
		}
		switch (requestCode) {		
		case CAMERA_CAPTURE: 
			Bitmap bm = (Bitmap)intent.getExtras().get("data");
			ImageView imgView = (ImageView) findViewById( R.id.imgView ) ;
			imgView.setImageBitmap(bm); 
			
			String [] proj={MediaStore.Images.Media.DATA};
			final Uri uriImages = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;     
			try{
				final Cursor cursorImages = managedQuery(uriImages, proj, null, null, null);
			    if(cursorImages != null && cursorImages.moveToLast()){         
			    	mGalleryPath = cursorImages.getString(0);
			    	cursorImages.close();
			    } 
			}catch(Exception e){}
			
//			String fileName = null;
//		    File[] listFiles = (new File(Environment.getExternalStorageDirectory()+"/dcim/camera/").listFiles()); 
//		    if(listFiles[0].getName().endsWith(".jpg") || listFiles[0].getName().endsWith(".bmp")) 
//		    	fileName = listFiles[0].getName();
		    
//		    Log.e("TAG","Path : " + mGalleryPath);
		    
			Bundle b = mIntent.getExtras();
			if (b != null && b.containsKey(MediaStore.EXTRA_OUTPUT)) { // large image?
			    // Shouldn't have to do this ... but
//				MediaStore.Images.Media.insertImage(getContentResolver(), bm, null, null);
			} else {
				// Shouldn't have to do this ... but
//				MediaStore.Images.Media.insertImage(getContentResolver(), bm, null, null);
			}
			break;
		case TAKE_GALLERY:
			Uri currImageURI = intent.getData();
			mGalleryPath = getRealPathFromURI(currImageURI);
//			showToast(this,"Path : " + mGalleryPath);
			
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			Bitmap bmp = BitmapFactory.decodeFile(mGalleryPath, options);
//			Bitmap resized = null;
//			if(bmp != null) resized = Bitmap.createScaledBitmap(bmp, 95, 95, true);
			ImageView imageview = (ImageView) findViewById(R.id.imgView) ;
			imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
 			imageview.setImageBitmap(bmp);
			
 			// Case 1 : �۵�����
//			try { 
//				Bitmap bitmap; 
//            	InputStream is = getContentResolver().openInputStream(currImageURI); 
//            	bitmap = BitmapFactory.decodeStream(new FlushedInputStream(is)); 
//            	ImageView imageview = (ImageView) findViewById(R.id.imgView) ;
//				imageview.setImageBitmap(bitmap);
//				is.close();
//			} catch (IOException e) {}
 			
			// Case 2 : �۵�����
//			try {
//				Bitmap bitmap = Images.Media.getBitmap(getContentResolver(), currImageURI); //Bitmap �ε�
//				ImageView imageview = (ImageView)findViewById( R.id.imgView ) ;
//				imageview.setImageBitmap(bitmap);
//			} catch(Exception e) {}
			
			break;
		}
	}
	
	private String getRealPathFromURI(Uri contentUri){
		String [] proj={MediaStore.Images.Media.DATA};
		Cursor cursor = managedQuery( contentUri, proj, null, null, null); 
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
	private void showToast(Context mContext, String text) {
		Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
	}
	
	private void DoFileUpload(String params) throws IOException {
		mUploadData = new UploadData();
		mUploadData.execute(params);
	}

	private void HttpFileUpload(String urlString , String params, String fileName) {
		try{
			mFileInputStream = new FileInputStream(fileName);
			connectUrl = new URL(urlString);
			
//			Log.d("File Up" , "mFileInputStream is " + mFileInputStream);
			
			// open connection
			HttpURLConnection conn = (HttpURLConnection)connectUrl.openConnection();
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
			
//			Log.d("File Up", "image byte is " + bytesRead );
			
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
//			Log.e("File Up" , "File is written");
			mFileInputStream.close();
			dos.flush(); // ���ۿ� �ִ� ���� ��� �о
			
			//���������� ������� �޾� �����ش�
			int ch;
			InputStream is = conn.getInputStream();
			StringBuffer b = new StringBuffer();
			while((ch = is.read()) != -1 ){
				b.append((char)ch);
			}
//			String s = b.toString();
//			Log.e("File Up" , "result = " + s);
			dos.close();
			
			// �����͸� DB�� �Է�
			HttpExecute(ServerUrl + "/gallery_data_input.asp", params);
			
		} catch(Exception e) {
//			Log.d("File Up" , "exception : " + e.getMessage());
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
			mProgressDialog = new ProgressDialog(GalleryUpload.this);
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
				HttpFileUpload(ServerUrl + "/upload_gallery.ashx?"+url[0], url[0], mGalleryPath);
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
