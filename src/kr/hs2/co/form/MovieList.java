package kr.hs2.co.form;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import kr.hs2.co.vo.MovieVO;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class MovieList extends ListActivity {
    
	private String ServerUrl = "http://pmms.hs2.co.kr";
	
	private ImageButton btnPrev = null;
	private Button btn = null;
	private EditText et = null;
	private XmlListAdapter adapter = null;
	private ListView lvMovie = null;
	private ArrayList<MovieVO> m_xmlData = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_list);
        
        btnPrev = (ImageButton)findViewById(R.id.btPrev);
        btn = (Button)findViewById(R.id.searchBtn);
        et = (EditText)findViewById(R.id.searchTxt);
        
        btnPrev.setOnClickListener(on_prev);
        btn.setOnClickListener(on_click);
        searchXml("");
    }
    
    /** 리스트뷰 항목 클릭시 호출됨 */
    @Override
	protected void onListItemClick (ListView l, View v, int position, long id){
	   super.onListItemClick(l, v, position, id);
	   
//	   Toast.makeText(this, m_xmlData.get(position).getTitle(), Toast.LENGTH_SHORT).show();
//	   Log.d("TAG",Integer.toString(position));
	   
	   Intent intent = new Intent(MovieList.this,MovieView.class);
	   intent.putExtra("idx",Integer.toString(m_xmlData.get(position).getIdx()));
	   startActivity(intent);

	}
    
    private View.OnClickListener on_prev = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	
    private View.OnClickListener on_click = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String searchTxt = et.getText().toString();
			if(searchTxt==""){
				
			}else{
				searchXml(searchTxt);
				// 키보드 감추기
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(et.getWindowToken(), 0); 
			}
		}
	};
		
	private void searchXml(String searchTxt){
		
		String m_searchTxt = "";
		try {
			m_searchTxt = URLEncoder.encode(searchTxt,"EUC-KR");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String m_sConnectUrl = ServerUrl + "/movie_data_list.asp?searchStr="+m_searchTxt;
		
		Log.d("TAG",m_sConnectUrl);
		
		MovieVO xmlData = null;
		String sTag;
		m_xmlData = new ArrayList<MovieVO>();
		
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
					if(sTag.equals("fileinfo")){
						xmlData.setFileinfo(xpp.nextText());
					}
					if(sTag.equals("imgfile")){
						xmlData.setImgfile(xpp.nextText());
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
			
		} catch (Exception e) {
		}
		
		adapter = new XmlListAdapter(this, R.layout.custom_rowlist, m_xmlData);
		
		lvMovie = (ListView)findViewById(android.R.id.list);
		//setListAdapter(adapter);
		lvMovie.setAdapter(adapter);
		//lvMovie.setItemsCanFocus(false);
		
		lvMovie.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
			}
		});
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
				v = vi.inflate(R.layout.custom_rowlist, null);
			}

			MovieVO xmlData = (MovieVO)items.get(position);
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
						InputStream is = new URL(xmlData.getImgfile()).openStream();
						Bitmap bm = BitmapFactory.decodeStream(is);
						iv.setImageBitmap(bm);
						is.close();
					} catch (Exception e) {}
				}
			}
			
			return v;
		}
		
		
	}
	
}