package kr.hs2.co.form;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ((ImageButton)findViewById(R.id.ibtPhoto)).setOnClickListener(on_photo);
        ((ImageButton)findViewById(R.id.ibtMovie)).setOnClickListener(on_movie);
        ((ImageButton)findViewById(R.id.ibtSettings)).setOnClickListener(on_setting);
        
        ((ImageButton)findViewById(R.id.ibtPhotoCitizen)).setOnClickListener(on_gallery);
        ((ImageButton)findViewById(R.id.ibtMovieCitizen)).setOnClickListener(on_youtube);
        ((ImageButton)findViewById(R.id.ibtExit)).setOnClickListener(on_exit);
    }
	
    private View.OnClickListener on_photo = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(Main.this, PhotoList.class);
			startActivity(i);
			overridePendingTransition(R.anim.fade,R.anim.hold);
		}
	};
	
	private View.OnClickListener on_movie = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(Main.this, MovieList.class);
			startActivity(i);
			overridePendingTransition(R.anim.fade,R.anim.hold);
		}
	};
	
	private View.OnClickListener on_setting = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			final LinearLayout linear = (LinearLayout)
			View.inflate(Main.this, R.layout.dialog_settings, null);
			
			SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
			String prefUrl = prefs.getString("key_url", "");
			TextView tvUrl = (TextView)linear.findViewById(R.id.tvUrl);
			tvUrl.setText("현재설정 URL : " + prefUrl);
			EditText etUrl = (EditText)linear.findViewById(R.id.etUrl);
			etUrl.setText("http://");
			
			new AlertDialog.Builder(Main.this)
			.setTitle("서버 정보를 입력하세요.")
			.setIcon(R.drawable.icon)
			.setView(linear)
			.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					EditText etUrl = (EditText)linear.findViewById(R.id.etUrl);
					
					SharedPreferences prefs = getSharedPreferences("PrefName", MODE_PRIVATE);
					SharedPreferences.Editor ed = prefs.edit();
					ed.putString("key_url", etUrl.getText().toString());
					ed.commit();
					
					Toast.makeText(getApplicationContext(), "설정을 적용하였습니다.", Toast.LENGTH_SHORT).show();
				}
			})
			.setNegativeButton("취소", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Toast.makeText(getApplicationContext(), "설정을 취소하였습니다.", Toast.LENGTH_SHORT).show();
				}
			})
			.show();
					
		}
	};
	
	private View.OnClickListener on_gallery = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(Main.this, GalleryList.class);
			startActivity(i);
		}
	};
	
	private View.OnClickListener on_youtube = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(Main.this, YoutubeList.class);
			startActivity(i);
			overridePendingTransition(R.anim.fade,R.anim.hold);
		}
	};
	
	private View.OnClickListener on_exit = new View.OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			System.exit(0);
//		}
		
		@Override
		public void onClick(View v) {
			Intent i = new Intent(Main.this, ActivationJSON.class);
			startActivity(i);
			overridePendingTransition(R.anim.fade,R.anim.hold);
		}
	};
}