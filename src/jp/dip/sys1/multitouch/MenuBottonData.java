package jp.dip.sys1.multitouch;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

public class MenuBottonData {
	
	List<BottonData> btnList;
	
	public MenuBottonData() {
		 btnList = new ArrayList<BottonData>();
	}
	public void init(){
		RectF rect = new RectF(440, 260, 560, 350);
		BottonData data = new BottonData(rect);
		btnList.add(data);
	}
	public boolean filter(MotionEvent event){
		int x = (int) event.getX();
		int y = (int) event.getY();
		for(BottonData data : btnList){
			if(data.getRect().contains(x, y)){
				return true;
			}
		}
		return false;
	}
	public boolean filter(Point p){
		for(BottonData data : btnList){
			if(data.getRect().contains(p.x, p.y)){
				return true;
			}
		}
		return false;
	}
}
