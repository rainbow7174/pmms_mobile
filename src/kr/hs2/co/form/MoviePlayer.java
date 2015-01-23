package kr.hs2.co.form;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

public class MoviePlayer extends Activity {
	
	private String MOVIE_URL = "";
	
	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        
        setContentView(R.layout.movie_player); 
        VideoView vv = (VideoView) findViewById(R.id.vvPlayer); 
        
        MOVIE_URL = getIntent().getExtras().getString("filename").trim(); 
//        MOVIE_URL = "http://www.archive.org/download/Unexpect2001/Unexpect2001_512kb.mp4";
//        MOVIE_URL = "http://172.168.10.100/upload/movie/vod/120110330.mp4";
        
		Log.v("Tag", "MOVIE_URL : " + MOVIE_URL);
		
        Uri video = Uri.parse(MOVIE_URL);
        vv.setVideoURI(video);
        
        MediaController mc = new MediaController(this); 
        mc.setAnchorView(vv); 
        vv.setMediaController(mc); 
        vv.requestFocus();
        vv.start();
    }
	
}
