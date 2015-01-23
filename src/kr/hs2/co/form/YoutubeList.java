package kr.hs2.co.form;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import kr.hs2.co.form.GalleryView.FlushedInputStream;
import kr.hs2.co.vo.MovieVO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class YoutubeList extends Activity implements OnItemClickListener {

	private String ServerUrl = "";
	
	private ImageButton ibtnPrev, ibtn = null;
	private Button btn = null;
	private Button btPrev, btNext = null;
	private EditText et = null;
	private XmlListAdapter adapter = null;
	private GridView gvPhoto = null;
	private ArrayList<MovieVO> m_xmlData = null;
	
	private String m_searchTxt = "";
	private int page = 1;
	
	private ProgressDialog mProgressDialog = null;
	private FetchData mFetchData = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_list);
        
        SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        ServerUrl = prefs.getString("key_url", "");
        
        ibtnPrev = (ImageButton)findViewById(R.id.ibtPrev);
        ibtn = (ImageButton)findViewById(R.id.ibtUpload);
        btn = (Button)findViewById(R.id.searchBtn);
        et = (EditText)findViewById(R.id.searchTxt);
        btPrev = (Button)findViewById(R.id.btPrev);
        btNext = (Button)findViewById(R.id.btNext);
        gvPhoto = (GridView)findViewById(R.id.gvPhoto);
        
        ibtnPrev.setOnClickListener(on_prev);
        ibtn.setOnClickListener(on_upload);
        btn.setOnClickListener(on_search);
        btPrev.setOnClickListener(on_prevphoto);
        btNext.setOnClickListener(on_nextphoto);
        
        searchXml(et.getText().toString(), page);
    }
    
    private View.OnClickListener on_prev = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	
	private View.OnClickListener on_prevphoto = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			page--;
			searchXml(et.getText().toString(), page);
		}
	};
	
	private View.OnClickListener on_nextphoto = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			page++;
			searchXml(et.getText().toString(), page);
		}
	};
	
    private View.OnClickListener on_upload = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(YoutubeList.this,YoutubeUpload.class);
			startActivity(intent);
			overridePendingTransition(R.anim.fade,R.anim.hold);
		}
	};
	
    private View.OnClickListener on_search = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			page = 1; // 검색시 첫페이지로 
			String searchTxt = et.getText().toString();
			if(searchTxt==""){
				
			}else{
				searchXml(searchTxt, page);
				// 키보드 감추기
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(et.getWindowToken(), 0); 
			}
		}
	};
	
	public void onItemClick(AdapterView<?> parent, View v, int position, long id){
		Intent intent = new Intent(YoutubeList.this,YoutubeView.class);
		intent.putExtra("idx",Integer.toString(m_xmlData.get(position).getIdx()));
		startActivity(intent);
	}
	
	private void searchXml(String searchTxt, int page){
		
		try {
			m_searchTxt = URLEncoder.encode(searchTxt,"EUC-KR");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String m_sConnectUrl = ServerUrl + "/youtube_data_list.asp?searchStr="+m_searchTxt+"&page="+String.valueOf(page);
		
		Log.d("TAG",m_sConnectUrl);
		
		mFetchData = new FetchData();
		mFetchData.execute(m_sConnectUrl);
		
//		gvPhoto.setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Intent intent = new Intent(PhotoGrid.this,PhotoView.class);
//				intent.putExtra("idx",Integer.toString(m_xmlData.get(position).getIdx()));
//				startActivity(intent);
//			}
//		});
	}
	
	private class XmlListAdapter extends ArrayAdapter<MovieVO> {

		private ArrayList<MovieVO> items;
		
		public XmlListAdapter(Context context, int textViewResourceId, ArrayList<MovieVO> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			
			if(v==null){
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.custom_rowgrid, null);
			}

			MovieVO xmlData = (MovieVO)items.get(position);
			if(xmlData != null){
				TextView tv1 = (TextView)v.findViewById(R.id.x_title);
				ImageView iv = (ImageView)v.findViewById(R.id.x_thumbnail);
				
				if(tv1 != null){
					tv1.setText(xmlData.getTitle());
				}
				if(iv != null){
					try {
						InputStream is = new URL(xmlData.getImgfile()).openStream();
						BitmapFactory.Options options = new BitmapFactory.Options();
						options.inSampleSize = 2;
						Bitmap bm = BitmapFactory.decodeStream(new FlushedInputStream(is), null, options);
						Bitmap resized = Bitmap.createScaledBitmap(bm, 130, 78, true);
						iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
						iv.setImageBitmap(resized);
						is.close();
					} catch (Exception e) {}
				}
			}
			
			return v;
		}
	}
	
	// 데이터 가져오기 클래스
	class FetchData extends AsyncTask<String, Integer, String>{
	    
		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(YoutubeList.this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setMax(100);
			mProgressDialog.setTitle("잠시만 기다려 주세요.");
			mProgressDialog.setMessage("데이터 가져오는 중 ...");
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
			
			m_xmlData = new ArrayList<MovieVO>();
			MovieVO xmlData = null;
			String sTag;
			
			try {
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				
				Log.v("tag","URL : "+url[0].toString());
				
				URL fileurl = new URL(url[0]);
				InputStream in = fileurl.openStream();
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
						if(sTag.equals("totalcnt")){
							xmlData.setTotalcnt(Integer.parseInt(xpp.nextText()));
						}
						if(sTag.equals("pagescale")){
							xmlData.setPagescale(Integer.parseInt(xpp.nextText()));
						}
						if(sTag.equals("imgfile")){
							xmlData.setImgfile(xpp.nextText());
						}
						if(sTag.equals("title")){
							xmlData.setTitle(xpp.nextText());
						}
						if(sTag.equals("fileinfo")){
							xmlData.setFileinfo(xpp.nextText());
						}
												
						break;
					case XmlPullParser.END_TAG:
						sTag = xpp.getName();
						
						if(sTag.equals("item")){
							m_xmlData.add(xmlData);
							xmlData = null;
						}
						break;
					case XmlPullParser.TEXT:
						break;
					default:
						break;
					}
					eventType = xpp.next();
				}
				
			} catch (Exception e) {}
			
	        return null;
	    }
	    
	    protected void onProgressUpdate(Integer... progress) {         
	    	mProgressDialog.setProgress(progress[0]);
	    }
	    
	    protected void onPostExecute(String result) { 
	    	adapter = new XmlListAdapter(YoutubeList.this, R.layout.custom_rowgrid, m_xmlData);
			gvPhoto.setAdapter(adapter);
			gvPhoto.setOnItemClickListener(YoutubeList.this);
	    	
			if(m_xmlData.size() > 0) {
				if(m_xmlData.get(0).getTotalcnt() > page*m_xmlData.get(0).getPagescale()) {
					if (page > 1) btPrev.setVisibility(View.VISIBLE);
					else btPrev.setVisibility(View.INVISIBLE);
					btNext.setVisibility(View.VISIBLE);
				}else{
					if (page > 1) btPrev.setVisibility(View.VISIBLE);
					else btPrev.setVisibility(View.INVISIBLE);
					btNext.setVisibility(View.INVISIBLE);
				}
			}
			
			Log.v("tag","page:" + String.valueOf(page) + ", m_searchTxt : "+m_searchTxt);
			
			if(m_xmlData.size() == 0) {
	    		mProgressDialog.dismiss();
		    	Toast.makeText(getApplicationContext(), "검색된 데이터가 없습니다.", Toast.LENGTH_LONG).show();	
	    	}else{
		    	mProgressDialog.dismiss();
		    	Toast.makeText(getApplicationContext(), "검색을 완료하였습니다.", Toast.LENGTH_SHORT).show();
	    	}
	    }
	    
	    protected void onCancelled() {
	    	mProgressDialog.dismiss();
	    	super.onCancelled();
			Toast.makeText(getApplicationContext(), "데이터 로드를 취소 하였습니다.", Toast.LENGTH_SHORT).show();
	    }
	}
	
}