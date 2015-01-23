package kr.hs2.co.form;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import kr.hs2.co.vo.PhotoVO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ListActivity;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoList extends ListActivity {
	
	private String ServerUrl = "";
	
	private ImageButton btnPrev, ibtn = null;
	private Button btn = null;
	private EditText et = null;
	private XmlListAdapter adapter = null;
	private ListView lvPhoto = null;
	private ArrayList<PhotoVO> m_xmlData = null;
	
	private LinearLayout footLayout = null;
	private String m_searchTxt = "";
	private int page = 1;
	
	private ProgressDialog mProgressDialog = null;
	private FetchData mFetchData = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_list);
        
        SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
        ServerUrl = prefs.getString("key_url", "");
    	
        btnPrev = (ImageButton)findViewById(R.id.ibtPrev);
        ibtn = (ImageButton)findViewById(R.id.ibtGridView);
        btn = (Button)findViewById(R.id.searchBtn);
        et = (EditText)findViewById(R.id.searchTxt);
        footLayout = (LinearLayout) View.inflate(this, R.layout.photo_list_foot, null);
        
        lvPhoto = (ListView)findViewById(android.R.id.list);
        lvPhoto.addFooterView(footLayout);
        footLayout.setVisibility(View.INVISIBLE);
        
        btnPrev.setOnClickListener(on_prev);
        ibtn.setOnClickListener(on_grid);
        btn.setOnClickListener(on_click);
        ((Button)footLayout.findViewById(R.id.btMore)).setOnClickListener(on_more);
        
        searchXml(et.getText().toString(), page);
    }
    
    /** 리스트뷰 항목 클릭시 호출됨 */
    @Override
	protected void onListItemClick (ListView l, View v, int position, long id){
	   super.onListItemClick(l, v, position, id);
	   
//	   Toast.makeText(this, m_xmlData.get(position).getTitle(), Toast.LENGTH_SHORT).show();
//	   Log.d("TAG",Integer.toString(position));
	   
	   Intent intent = new Intent(PhotoList.this,PhotoView.class);
	   intent.putExtra("idx",Integer.toString(m_xmlData.get(position).getIdx()));
	   startActivity(intent);
	}
    
    private View.OnClickListener on_prev = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
    
    private View.OnClickListener on_grid = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(PhotoList.this,PhotoGrid.class);
			startActivity(intent);
			overridePendingTransition(R.anim.fade,R.anim.hold);
			finish();
		}
	};
	
	private View.OnClickListener on_more = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			page++;
			searchXml(et.getText().toString(), page);
		}
	};
	
    private View.OnClickListener on_click = new View.OnClickListener() {
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
		
	private void searchXml(String searchTxt, int page){
		
		try {
			m_searchTxt = URLEncoder.encode(searchTxt,"EUC-KR");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String m_sConnectUrl = ServerUrl + "/photo_data_list.asp?searchStr="+m_searchTxt+"&page="+String.valueOf(page);
		
		Log.d("TAG",m_sConnectUrl);
		
		mFetchData = new FetchData();
		mFetchData.execute(m_sConnectUrl);
		
//		lvPhoto.setOnScrollListener(new OnScrollListener() {
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//			}
//			
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//					int visibleItemCount, int totalItemCount) {
//				// TODO Auto-generated method stub
//			}
//		});
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
				v = vi.inflate(R.layout.custom_rowlist, null);
			}

			PhotoVO xmlData = (PhotoVO)items.get(position);
			if(xmlData != null){
				TextView tv1 = (TextView)v.findViewById(R.id.x_title);
				TextView tv2 = (TextView)v.findViewById(R.id.x_fileinfo);
				ImageView iv = (ImageView)v.findViewById(R.id.x_thumbnail);
				
				if(tv1 != null){
//					tv1.setText(Html.fromHtml("<a href='" + xmlData.d_link+"'>" + xmlData.getTitle() + "</a>"));
//					tv1.setMovementMethod(LinkMovementMethod.getInstance());
					tv1.setText(xmlData.getTitle());
				}
				if(tv2 != null){
					tv2.setText(xmlData.getFileinfo());
				}
				if(iv != null){
					try {
//						ImageLoader il = new ImageLoader(PhotoList.this);
//						il.DisplayImage(xmlData.getThumbnail(), PhotoList.this, iv);
						
						InputStream is = new URL(xmlData.getThumbnail()).openStream();
						Bitmap bm = BitmapFactory.decodeStream(is);
						iv.setImageBitmap(bm);
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
			mProgressDialog = new ProgressDialog(PhotoList.this);
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
			
			m_xmlData = new ArrayList<PhotoVO>();
			PhotoVO xmlData = null;
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
							xmlData = new PhotoVO();
							xmlData.setIdx(Integer.parseInt(xpp.nextText()));
//							Log.v("tag","totalCount : "+String.valueOf(totalCount)+", viewCount : "+String.valueOf(viewCount));
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
						if(sTag.equals("fileinfo")){
							xmlData.setFileinfo(xpp.nextText());
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
			
	        return null;
	    }
	    
	    protected void onProgressUpdate(Integer... progress) {         
	    	mProgressDialog.setProgress(progress[0]);
	    }
	    
	    protected void onPostExecute(String result) { 
	    	adapter = new XmlListAdapter(PhotoList.this, R.layout.custom_rowlist, m_xmlData);
			lvPhoto.setAdapter(adapter);
	    	
			if(m_xmlData.size() > 0) {
				if(m_xmlData.get(0).getTotalcnt() > page*m_xmlData.get(0).getPagescale()) {
					footLayout.setVisibility(View.VISIBLE);
				}else{
					footLayout.setVisibility(View.GONE);
				}
			}
			
//			Log.v("tag","page:" + String.valueOf(page) + ", totalCount : "+String.valueOf(m_xmlData.get(0).getTotalcnt())+", pageScale : "+String.valueOf(m_xmlData.get(0).getPagescale()));
			
			if(m_xmlData.size() == 0) {
	    		mProgressDialog.dismiss();
		    	Toast.makeText(getApplicationContext(), "검색된 데이터가 없습니다.", Toast.LENGTH_LONG).show();	
	    	}else{
		    	mProgressDialog.dismiss();
		    	Toast.makeText(getApplicationContext(), "검색을 완료하였습니다.", Toast.LENGTH_SHORT).show();
//		    	Toast.makeText(getApplicationContext(), "총 "+String.valueOf(m_xmlData.size())+"개의 데이터가 검색되었습니다.", Toast.LENGTH_LONG).show();
	    	}
	    }
	    
	    protected void onCancelled() {
	    	mProgressDialog.dismiss();
	    	super.onCancelled();
			Toast.makeText(getApplicationContext(), "데이터 로드를 취소 하였습니다.", Toast.LENGTH_SHORT).show();
	    }
	}
	
	
}