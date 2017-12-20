package com.massky.new119eproject.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * wrap content gridview
 * @author Administrator
 *
 */
public class WrapContentGridView extends GridView {
	
	protected OnTouchInvalidPositionListener mTouchInvalidPosListener;
	/**
	 * 
	 * @param context
	 */
	public WrapContentGridView(Context context) {
		super(context);
		
	}
	
	public WrapContentGridView(Context context , AttributeSet attrs){
		super(context, attrs);
	}
	
	public WrapContentGridView(Context context, AttributeSet attrs , int style){
		super(context , attrs , style);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec,expandSpec);
	}
	
	 public interface OnTouchInvalidPositionListener {//CTRL+ ALT + H  查看方法的引用
		    /**
		     * motionEvent 可使用 MotionEvent.ACTION_DOWN 或者 MotionEvent.ACTION_UP等来按需要进行判断
		     * @return 是否要终止事件的路由
		     */
		    boolean onTouchInvalidPosition(int motionEvent);
		  }
	 
		  /**
		   * 点击空白区域时的响应和处理接口
		   */
		  public void setOnTouchInvalidPositionListener(OnTouchInvalidPositionListener listener) {
		    mTouchInvalidPosListener = listener;
		  }
		  
		  @Override
		  public boolean onTouchEvent(MotionEvent event) {
		    if(mTouchInvalidPosListener == null) {
		      return super.onTouchEvent(event);
		    }

		    if (!isEnabled()) {
		      // A disabled view that is clickable still consumes the touch
		      // events, it just doesn't respond to them.
		      return isClickable() || isLongClickable(); // 这句话的意思是，该view是可点击，但不响应点击事件
		    }
		    final int motionPosition = pointToPosition((int)event.getX(), (int)event.getY());//无效点击区域
		    if( motionPosition == INVALID_POSITION ) {
		      super.onTouchEvent(event);
		      return mTouchInvalidPosListener.onTouchInvalidPosition(event.getActionMasked());//return mTouchInvalidPosListener
		      //.onTouchInvalidPosition(event.getActionMasked());
		    }
		    return super.onTouchEvent(event);

		  }
}
