package jp.dip.sys1.multitouch;

import android.graphics.RectF;


public class BottonData {
	private RectF Rect;

	public BottonData(RectF Rect) {
		super();
		this.Rect = Rect;
	}
	
	public RectF getRect() {
		return Rect;
	}

	public void setRect(RectF Rect) {
		this.Rect = Rect;
	}

}
