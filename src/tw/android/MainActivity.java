package tw.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button btOpen, btClose;
	WindowManager wm;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btOpen = (Button) findViewById(R.id.btOpen);
		btClose = (Button) findViewById(R.id.btClose);

		// 啟動小圖示
		btOpen.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Intent overlayIntent = new Intent();
				overlayIntent.setClass(MainActivity.this, OverlayService.class);
				MainActivity.this.startService(overlayIntent);
			}
		});

		// 關閉主畫面
		btClose.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

	}
}