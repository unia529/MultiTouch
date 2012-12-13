package jp.dip.sys1.multitouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

/**
 * 
 * @author yagi
 *
 */
public class ScalableView extends ImageView{
	private static final String TAG = ScalableView.class.getSimpleName();
	private static final float MAX_SCALE = 5;
	private static final float MIN_SCALE = 0.3f;
	private static final float MIN_LENGTH = 30f;
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private static final int TAP = 3;
	/** MatrixのgetValues用 */
	private float[] values = new float[9];
	/** ドラッグ用マトリックス */
	private Matrix moveMatrix = new Matrix();
	/** マトリックス */
	private Matrix matrix = new Matrix();
	/** 画像移動用の位置 */
	private PointF point = new PointF();
	/** ズーム時の座標 */
	private PointF middle = new PointF();
	/** タッチモード。何も無し、ドラッグ、ズーム */
	private int mode = NONE;
	/** Zoom開始時の二点間距離 */
	private float initLength = 1;
	/** 元画像の幅と高さ格納用 */
	private Rect imgRect = new Rect();
	/** 移動可能かどうか */
	public boolean canMoveState = true;
	
	private static long downTime = 0;
	private static long downTimeDelta = 0;
	
	
	public ScalableView(Context context) {
		this(context, null, 0);
	}

	public ScalableView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScalableView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		matrix = new Matrix();
		matrix.setScale(1, 1);
//		setOnTouchListener(this);
//		setBackgroundResource(R.drawable.grandmenu);
		imgRect = getDrawable().getBounds();
	}
	
//	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.v(TAG, "BOTTOM" + event.getAction());
//		ImageView view = (ImageView) v;
		ImageView view = (ImageView) this;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.d(TAG, "mode=DRAG\t" + v.getId());
			showTouchImgPoint(matrix, event, v);////////
			mode = DRAG;
			downTime = event.getEventTime();
//			if(event.getDownTime() < 100){
//				//mode =TAP;
//				MenuBottonData mbd = new MenuBottonData();
//				mbd.init();
//				Log.d(TAG, "AAAAAAAAAAAA");
//				if(mbd.filter(event)){
//					Log.d(TAG, "領域内をタッチしました。");
//				}else{
//					Log.d(TAG, "領域外をタッチしました。");
//				}
//			}else{
//				Log.d(TAG, "BBBBBBBB");
//			}
			point.set(event.getX(), event.getY());
			moveMatrix.set(matrix);
			break;
		case MotionEvent.ACTION_POINTER_2_UP:
		case MotionEvent.ACTION_UP:
			Log.d(TAG, "mode=NONE");
			downTimeDelta = event.getEventTime() - downTime;
			if(downTimeDelta < 100){//TAPした時の処理
				MenuBottonData mbd = new MenuBottonData();
				mbd.init();
				Point p = getOriginalPoint(matrix, event, view);
				if(mbd.filter(p)){
					Log.d(TAG, "領域内をタッチしました。");
				}
			}
			mode = NONE;
			break;
		case MotionEvent.ACTION_POINTER_2_DOWN:
			initLength = getLength(event);
			if (initLength > MIN_LENGTH) {
				Log.d(TAG, "mode=ZOOM");
				moveMatrix.set(matrix);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			switch (mode) {
			case DRAG:
				int mvx = (int) (event.getX() - point.x);
				int mvy = (int)(event.getY() - point.y);
				//TODO 画像の移動範囲設定
				canMoveState = true;
				matrix.set(moveMatrix);
				matrix.postTranslate(mvx, mvy);
				view.setImageMatrix(matrix);
				showImgPoint(matrix);//////////////////
				break;
			case ZOOM:
				if (mode == ZOOM) {
					float currentLength = getLength(event);
					middle = getMiddle(event, middle);
					if (currentLength > MIN_LENGTH) {
						matrix.set(moveMatrix);
						float scale = filter(matrix,currentLength / initLength);
						showImgPoint(matrix);//////////////////
						matrix.postScale(scale, scale, middle.x, middle.y);
						view.setImageMatrix(matrix);
					}
					break;
				}
				break;
			}
		}
		return false;
	}
//	public boolean shouldMove(MotionEvent event){
//		switch(event.getAction()){
//		case MotionEvent.ACTION_DOWN:
//			mode = DRAG;
//			point.set(event.getX(), event.getY());
//			moveMatrix.set(matrix);
//			break;
//		case MotionEvent.ACTION_MOVE:
//			if(mode == DRAG){
//				int mvx = (int) (event.getX() - point.x);
//				int mvy = (int)(event.getY() - point.y);
//				if(canMove(mat, mvx, mvy, view))
//			}
//		}
//			
//		return true;
//	}
	
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.v(TAG, "BOTTOM");
		return super.onTouchEvent(event);
	}
	
	private void showImgPoint(Matrix mat){
		float[] values = new float[9];
		mat.getValues(values);
		Log.v(TAG, "MTRANS_X="+values[Matrix.MTRANS_X]+"\t"+"MTRANS_Y"+values[Matrix.MTRANS_Y]);
////	Log.v(TAG, ""+values[Matrix.MTRANS_X]);
//		Log.v(TAG, "("+view.getLeft()+", "+view.getTop() + ")");
	}
	/**
	 * ImageView内のスケール後画像のタッチした座標を元スケール画像の座標を表示
	 * @param mat
	 * @param event
	 * @param view
	 */
	private void showTouchImgPoint(Matrix mat, MotionEvent event, View view){
		Point p = new Point();
		p = getOriginalPoint(mat, event, view);
		Log.v(TAG, "==("+ p.x +", "+ p.y + ")==="+view.getLeft()+"////"+view.getTop());
	}
	/**
	 * ImageView内のスケール後画像のタッチした座標を元スケール画像の座標として取得
	 * @param mat
	 * @param event
	 * @param view
	 * @return
	 */
	private Point getOriginalPoint(Matrix mat, MotionEvent event, View view){
		Point p = new Point();
		float[] values = new float[9];
		mat.getValues(values);
		p.x = (int) ((event.getX() - values[Matrix.MTRANS_X])/values[Matrix.MSCALE_X]);
		p.y = (int) ((event.getY() - values[Matrix.MTRANS_Y])/values[Matrix.MSCALE_Y]);
		return p;
	}
	
	
	/**
	 * ImageView内の画像が画面外に出そうならfalseを返す
	 * @param mat
	 * @param mvx
	 * @param mvy
	 * @param view
	 * @return
	 */
	private boolean canMove(Matrix mat, int mvx, int mvy, View view){
		float[] values = new float[9];
		mat.getValues(values);
		int width = (int)(imgRect.width()*values[Matrix.MSCALE_X]);
		int height = (int)(imgRect.height()*values[Matrix.MSCALE_Y]);
		if(values[Matrix.MTRANS_X] >= view.getWidth()-70 && mvx >= 0){//画像の左が画面の右に消えそうならダメ
			return false;
		}
		if(values[Matrix.MTRANS_X] + width <= 70 && mvx <= 0){//画像の右が画面の左に消えそうならダメ
			return false;
		}
		if(values[Matrix.MTRANS_Y] >= view.getHeight()-70 && mvy>=0){//画像の上が画面下に消えそうならダメ
			return false;
		}
		if(values[Matrix.MTRANS_Y] + height <= 70 && mvy<=0){//画像の下が画面の上に消えそうならダメ
			return false;
		}
		return true;
	}
	
	public Matrix getImageMatrix(){
		return matrix;
	}
	
	private void drawpoint(Matrix mat){
		float[] values = new float[9];
		mat.getValues(values);
//		float cx = values[Matrix.MTRANS_X];
//		float cy = values[Matrix.MTRANS_Y];
		float cx = 100;
		float cy = 100;
		float radius = 100;
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		Canvas canvas = new Canvas();
//		canvas.drawCircle(cx, cy, radius, paint);
		canvas.drawPoint(cx, cy, paint);
//		draw(canvas);
	}
//	@Override
//	protected void onDraw(Canvas canvas) {
////		super.onDraw(canvas);
////		drawpoint(matrix);
//////		canvas.draw
//		float[] values = new float[9];
//		matrix.getValues(values);
////		float cx = values[Matrix.MTRANS_X];
////		float cy = values[Matrix.MTRANS_Y];
//		float cx = 100;
//		float cy = 100;
//		Paint paint = new Paint();
//
//		paint.setColor(Color.BLACK);
//		paint.setStrokeWidth(4);
////		canvas.drawCircle(cx, cy, radius, paint);
////		canvas.drawPoint(cx, cy, paint);
////		draw(canvas);
//	}
	
	/**
	 * 拡大縮小可能かどうかを判定する
	 * @param m
	 * @param s
	 * @return
	 */
	private float filter(Matrix m, float s){
		m.getValues(values);
		float nextScale = values[0]*s;
		if(nextScale > MAX_SCALE){ 
			s=MAX_SCALE/values[0];
		}
		else if(nextScale < MIN_SCALE){
			s=MIN_SCALE/values[0];
		}
		return s;
	}
	
	/**
	 * 比率を計算
	 * @param x
	 * @param y
	 * @return
	 */
	private float getLength(MotionEvent e) {
		if(e.getPointerCount() >= 2){
			Log.v(TAG, "count = " + e.getPointerCount());
			Log.v(TAG, "[1-x]="+e.getX(1)+"\t[0-x]="+e.getX(0)+"\t[1-y]="+e.getY(1)+"\t[0-y]="+e.getY(0));
			float xx = e.getX(1) - e.getX(0);
			float yy = e.getY(1) - e.getY(0);
			return FloatMath.sqrt(xx * xx + yy * yy);
		}
		return 0;
	}

	/**
	 * 中間点を求める
	 * @param e
	 * @param p
	 * @return
	 */
	private PointF getMiddle(MotionEvent e, PointF p) {
		if(e.getPointerCount() >= 2){
			Log.v(TAG, "count = " + e.getPointerCount());
			float x = e.getX(0) + e.getX(1);
			float y = e.getY(0) + e.getY(1);
			p.set(x / 2, y / 2);
		}
		return p;
	}
}
