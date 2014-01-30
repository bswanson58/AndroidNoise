package com.SecretSquirrel.AndroidNoise.views;

// Secret Squirrel Software - Created by bswanson on 1/30/14.

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

// from: http://stackoverflow.com/questions/3554377/handling-click-events-on-a-drawable-within-an-edittext

@SuppressWarnings("unused")
public class ButtonEditText extends EditText {
	public interface DrawableClickListener {
		public static enum  DrawablePosition { TOP, BOTTOM, LEFT, RIGHT }

		public void         onClick( DrawablePosition target );
	}

	public static final String LOG_TAG = "ClickableButtonEditText";

	private Drawable                mDrawableRight;
	private Drawable                mDrawableLeft;
	private Drawable                mDrawableTop;
	private Drawable                mDrawableBottom;

	private boolean                 mConsumeEvent = false;
	private int                     mClickPadding = 7;

	private DrawableClickListener   mClickListener;

	public ButtonEditText( Context context, AttributeSet attrs, int defStyle ) {
		super( context, attrs, defStyle );
	}

	public ButtonEditText( Context context, AttributeSet attrs ) {
		super( context, attrs );
	}

	public ButtonEditText( Context context ) {
		super( context );
	}

	public void consumeEvent() {
		this.setConsumeEvent( true );
	}

	public void setConsumeEvent( boolean b ) {
		this.mConsumeEvent = b;
	}

	public void setClickPadding( int z ) {
		this.mClickPadding = z;
	}

	public int getClickPadding() {
		return mClickPadding;
	}

	@Override
	public void setCompoundDrawables( Drawable left, Drawable top, Drawable right, Drawable bottom ) {
		mDrawableRight = right;
		mDrawableLeft = left;
		mDrawableTop = top;
		mDrawableBottom = bottom;

		super.setCompoundDrawables( left, top, right, bottom );
	}

	@Override
	protected void onTextChanged( CharSequence text, int start, int lengthBefore, int lengthAfter ) {
		super.onTextChanged( text, start, lengthBefore, lengthAfter );

		int alpha = 255;

		if(!isInEditMode()) {
			if( lengthAfter < 1 ) {
				alpha = 0;
			}
		}

		if( mDrawableLeft != null ) {
			mDrawableLeft.setAlpha( alpha );
		}
		if( mDrawableRight != null ) {
			mDrawableRight.setAlpha( alpha );
		}
		if( mDrawableTop != null ) {
			mDrawableTop.setAlpha( alpha );
		}
		if( mDrawableBottom != null ) {
			mDrawableBottom.setAlpha( alpha );
		}
	}

	@Override
	public boolean onTouchEvent( MotionEvent event ) {
		if(( mClickListener != null ) &&
		   ( event.getAction() == MotionEvent.ACTION_DOWN )) {
			int x = (int)event.getX();
			int y = (int)event.getY();

			if( mDrawableLeft != null ) {
				Rect    bounds = mDrawableLeft.getBounds();

				if(( x >= ( getPaddingLeft() - mClickPadding)) &&
				   ( x <= ( getPaddingLeft() + bounds.width() + mClickPadding)) &&
				   ( y >= ( getPaddingTop() - mClickPadding)) &&
				   ( y <= ( getHeight() - getPaddingBottom()) + mClickPadding)) {
					mClickListener.onClick( DrawableClickListener.DrawablePosition.LEFT );

					if( mConsumeEvent ) {
						event.setAction( MotionEvent.ACTION_CANCEL );
						return false;
					}
				}
			}
			else if( mDrawableRight != null ) {
				Rect    bounds = mDrawableRight.getBounds();

				if(( x >= ( getRight() - getPaddingRight() - bounds.width() - mClickPadding)) &&
				   ( x <= ( getRight() + mClickPadding)) &&
				   ( y >= ( getPaddingTop() - mClickPadding)) &&
				   ( y <= ( getHeight() - getPaddingBottom()) + mClickPadding)) {
					mClickListener.onClick( DrawableClickListener.DrawablePosition.RIGHT );

					if( mConsumeEvent ) {
						event.setAction( MotionEvent.ACTION_CANCEL );
						return false;
					}
				}
			}
			else if( mDrawableTop != null ) {
				Rect    bounds = mDrawableTop.getBounds();
				int     center = getWidth() / 2;

				if(( x >= ( center - ( bounds.width() / 2 ) - mClickPadding)) &&
				   ( x <= ( center + ( bounds.width() / 2 ) + mClickPadding)) &&
				   ( y >= ( getTop() - mClickPadding)) &&
				   ( y <= ( getTop() + getPaddingTop() + bounds.height() + mClickPadding))) {
					mClickListener.onClick( DrawableClickListener.DrawablePosition.TOP );

					if( mConsumeEvent ) {
						event.setAction( MotionEvent.ACTION_CANCEL );
						return false;
					}
				}
			}
			else if( mDrawableBottom != null ) {
				Rect    bounds = mDrawableBottom.getBounds();
				int     center = getWidth() / 2;

				if(( x >= ( center - ( bounds.width() / 2 ) - mClickPadding)) &&
				   ( x <= ( center + ( bounds.width() / 2 ) + mClickPadding)) &&
				   ( y >= ( getBottom() - getPaddingBottom() - bounds.height() - mClickPadding)) &&
				   ( y <= ( getBottom() + mClickPadding))) {
					mClickListener.onClick( DrawableClickListener.DrawablePosition.BOTTOM );

					if( mConsumeEvent ) {
						event.setAction( MotionEvent.ACTION_CANCEL );
						return false;
					}
				}
			}
		}

		return super.onTouchEvent( event );
	}

	@Override
	protected void finalize() throws Throwable {
		mDrawableRight = null;
		mDrawableBottom = null;
		mDrawableLeft = null;
		mDrawableTop = null;

		super.finalize();
	}

	public void setDrawableClickListener( DrawableClickListener listener ) {
		this.mClickListener = listener;
	}
}