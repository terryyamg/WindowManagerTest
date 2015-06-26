package tw.android;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageButton;

public final class OverlayService extends Service {

	private static final int LayoutParamFlags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
			| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
			| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
			| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
			| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

	private LayoutInflater inflater;
	private Display mDisplay;
	private View layoutView;
	private WindowManager windowManager;
	private WindowManager.LayoutParams params;
	private View.OnTouchListener touchListener;
	private View.OnClickListener clickListener;

	private DisplayMetrics calculateDisplayMetrics() {
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		mDisplay.getMetrics(mDisplayMetrics);
		return mDisplayMetrics;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PRIORITY_PHONE,
				LayoutParamFlags, PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.TOP | Gravity.LEFT; // 圖片按鈕的初始位置
		windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
		mDisplay = windowManager.getDefaultDisplay();
		inflater = LayoutInflater.from(this);
		layoutView = inflater.inflate(R.layout.overlay, null); // 取得layout
		windowManager.addView(layoutView, params);

		final ImageButton button = (ImageButton) layoutView
				.findViewById(R.id.toggle); // 取得圖片按鈕
		// 圖片按鈕-點擊監聽事件
		clickListener = new OnClickListener() {
			public void onClick(View view) {
				try {
					Log.i("Service", "stop!");
					// 關閉service
					Intent intent = new Intent(OverlayService.this,
							OverlayService.class);
					stopService(intent);
				} catch (Exception ex) {
				}
			}
		};

		// 圖片按鈕-移動監聽事件
		touchListener = new View.OnTouchListener() {
			private int initialX;
			private int initialY;
			private float initialTouchX;
			private float initialTouchY;
			private long downTime;

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: // 按下圖片按鈕尚未放開時
					Log.i("downTime", downTime + "");
					downTime = SystemClock.elapsedRealtime();
					initialX = params.x;
					initialY = params.y;
					initialTouchX = event.getRawX();
					initialTouchY = event.getRawY();
					return true;
				case MotionEvent.ACTION_UP: // 放開圖片按鈕時
					long currentTime = SystemClock.elapsedRealtime();
					Log.i("currentTime - downTime", currentTime - downTime + "");
					if (currentTime - downTime < 200) { // 當按下圖片按鈕時
						v.performClick(); // 自動點擊事件
					} else {
						// updateViewLocation(); //黏住邊框功能
					}
					return true;
				case MotionEvent.ACTION_MOVE: // 按住移動時
					params.x = initialX
							+ (int) (event.getRawX() - initialTouchX);
					params.y = initialY
							+ (int) (event.getRawY() - initialTouchY);
					windowManager.updateViewLayout(layoutView, params);
					return true;
				}
				return false;
			}

			// 黏住邊框功能
			private void updateViewLocation() {
				DisplayMetrics metrics = calculateDisplayMetrics();
				int width = metrics.widthPixels / 2;
				if (params.x >= width)
					params.x = (width * 2) - 10;
				else if (params.x <= width)
					params.x = 10;
				windowManager.updateViewLayout(layoutView, params);
			}
		};

		button.setOnClickListener(clickListener); // 圖片按鈕-點擊監聽事件
		layoutView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View view, MotionEvent arg1) {
				return false;
			}
		});
		
		button.setOnTouchListener(touchListener);// 圖片按鈕-移動監聽事件
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		windowManager.removeView(layoutView);
	}
}
