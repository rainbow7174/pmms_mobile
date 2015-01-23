package kr.hs2.co.form;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import kr.hs2.co.util.ImageCache;
import kr.hs2.co.vo.PhotoVO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Context;
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
import android.widget.AdapterView.OnItemClickListener;

public class GalleryList extends Activity implements OnItemClickListener {

	private String ServerUrl = "";
	
	private ImageButton ibtnPrev, ibtn = null;
	private Button btn = null;
	private Button btPrev, btNext = null;
	private EditText et = null;
	private XmlListAdapter adapter = null;
	private GridView gvPhoto = null;
	private ArrayList<PhotoVO> m_xmlData = null;
	
	private String m_searchTxt = "";
	private int page = 1;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_list);
        
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
			Intent intent = new Intent(GalleryList.this,GalleryUpload.class);
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
		Intent intent = new Intent(GalleryList.this,GalleryView.class);
		intent.putExtra("idx",Integer.toString(m_xmlData.get(position).getIdx()));
		startActivity(intent);
	}
	
	private void searchXml(String searchTxt, int page){
		
		try {
			m_searchTxt = URLEncoder.encode(searchTxt,"EUC-KR");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String m_sConnectUrl = ServerUrl + "/gallery_data_list.asp?searchStr="+m_searchTxt+"&page="+String.valueOf(page);
		
		Log.d("TAG",m_sConnectUrl);
		
		m_xmlData = new ArrayList<PhotoVO>();
		PhotoVO xmlData = null;
		String sTag;
		
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			
			Log.v("tag","URL : "+m_sConnectUrl);
			
			URL fileurl = new URL(m_sConnectUrl);
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
						xmlData = new PhotoVO();
						xmlData.setIdx(Integer.parseInt(xpp.nextText()));
					}
					if(sTag.equals("totalcnt")){
						xmlData.setTotalcnt(Integer.parseInt(xpp.nextText()));
					}
					if(sTag.equals("pagescale")){
						xmlData.setPagescale(Integer.parseInt(xpp.nextText()));
					}
					if(sTag.equals("title")){
						xmlData.setTitle(xpp.nextText());
					}
					if(sTag.equals("thumbnail")){
						xmlData.setThumbnail(xpp.nextText());
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
		
		adapter = new XmlListAdapter(GalleryList.this, R.layout.custom_rowgrid, m_xmlData);
		gvPhoto.setAdapter(adapter);
		gvPhoto.setOnItemClickListener(GalleryList.this);
	}
	
	private class XmlListAdapter extends ArrayAdapter<PhotoVO> {

		private ArrayList<PhotoVO> items;
		
		public XmlListAdapter(Context context, int textViewResourceId, ArrayList<PhotoVO> items) {
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

			final PhotoVO xmlData = (PhotoVO)items.get(position);
			if(xmlData != null){
				TextView tv1 = (TextView)v.findViewById(R.id.x_title);
				ImageView iv = (ImageView)v.findViewById(R.id.x_thumbnail);
				
				if(tv1 != null){
					tv1.setText(xmlData.getTitle());
				}
				if(iv != null){			
					new FetchData(iv).execute(xmlData.getThumbnail());
				}
			}
			
			return v;
		}
	}
	
	// 데이터 가져오기 클래스
	class FetchData extends AsyncTask<String, Integer, Bitmap>{
	    
		ImageView imageView;
		
		public FetchData(ImageView imageView) {  
	        this.imageView = imageView;  
		} 
		
		@Override
		protected void onPreExecute() {
		}
		
		@Override
	    protected Bitmap doInBackground(String... url) {
			Bitmap image = ImageCache.getImage(url[0]);
			if(image==null){
				image = ImageDownLoad(url[0]);
				ImageCache.setImage(url[0], image);
			}
	        return image;  
	    }
	    
		@Override
	    protected void onProgressUpdate(Integer... progress) {         
	    }
	    
		@Override
	    protected void onPostExecute(Bitmap bm) {
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
			
			if(bm != null) imageView.setImageBitmap(bm);
			
//			Log.v("tag","page:" + String.valueOf(page) + ", m_searchTxt : "+m_searchTxt);
	    }
	    
		@Override
	    protected void onCancelled() {
	    	super.onCancelled();
	    }
	}
	
	public Bitmap ImageDownLoad(String image_url){
//		Hashtable<String,String> table = null;
//		table = BitmapUtil.getBitmapResizeToSmall(BitmapUtil.getBitmapOfWidth(image_url), BitmapUtil.getBitmapOfHeight(image_url));
		try {
			URL imageURL = new URL(image_url);
//			HttpURLConnection conn = (HttpURLConnection)imageURL.openConnection();             
			URLConnection conn = imageURL.openConnection();
			BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), 1024);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 8;
			Bitmap bm = BitmapFactory.decodeStream(new FlushedInputStream(bis), null, options);
//			Bitmap resized = Bitmap.createScaledBitmap(bm, Integer.parseInt(table.get("width").toString()), Integer.parseInt(table.get("height").toString()), true);
			final Bitmap resized = Bitmap.createScaledBitmap(bm, 100, 100, true);
			bm.recycle(); bm = null;
			bis.close();
			
			return resized;
		} catch (Exception e) {
			return null;
		}
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