package jp.dip.sys1.multitouch;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;

/**
 * 
 * @author yagi
 * 
 */
public class MultiTouch extends Activity{
	private final static String TAG = MultiTouch.class.getSimpleName();
	private ScalableView sv1;
	private TopScalableView tsv;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		tsv = (TopScalableView)findViewById(R.id.TopScalableView);//上のビュー
		sv1 = (ScalableView)findViewById(R.id.ImageView01);//下(メニュー)のビュー
		
		//下のビューでタッチイベントを拾って、上のビューに渡す
		sv1.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.v(TAG, "---------------");
				sv1.onTouch(v, event);
				tsv.onTouch(v, event);
				return true;
			}
		});
	}
}