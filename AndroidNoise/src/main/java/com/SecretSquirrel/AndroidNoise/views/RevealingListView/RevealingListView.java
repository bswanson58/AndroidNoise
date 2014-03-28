package com.secretSquirrel.sandbox.RevealingListView;

// Secret Squirrel Software - Created by BSwanson on 3/21/14.

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ListView;

import com.secretSquirrel.sandbox.R;

@SuppressWarnings( "unused" )
public class RevealingListView extends ListView {
	public final static int             REVEAL_MODE_NONE = 0;
	public final static int             REVEAL_MODE_RIGHT = 1;
	public final static int             REVEAL_MODE_LEFT = 2;
	public final static int             REVEAL_MODE_BOTH = 3;

	private final static int            TOUCH_STATE_REST = 0;
	private final static int            TOUCH_STATE_SCROLLING_X = 1;
	private final static int            TOUCH_STATE_SCROLLING_Y = 2;

	private int                         mFrontView;
	private int                         mRevealLeftView;
	private int                         mRevealRightView;
	private boolean                     mRevealOnLongPress;
	private RevealingTouchListener      mTouchListener;
	private int                         mTouchState = TOUCH_STATE_REST;
	private float                       mLastMotionX;
	private float                       mLastMotionY;
	private int                         mTouchSlop;

	public RevealingListView( Context context ) {
		super( context );
	}

	public RevealingListView( Context context, AttributeSet attrs ) {
		super( context, attrs );

		if(!isInEditMode()) {
			initialize( attrs );
		}
	}

	public RevealingListView( Context context, AttributeSet attrs, int defStyle ) {
		super( context, attrs, defStyle );

		if(!isInEditMode()) {
			initialize( attrs );
		}
	}

	private void initialize( AttributeSet attributes ) {
		int         revealMode = REVEAL_MODE_NONE;
		boolean     allowMultipleReveals = false;
		long        animationTime =	0;
		int         rightRevealAction = 1;
		int         leftRevealAction = 2;

		if( getContext() != null ) {
			ViewConfiguration vc = ViewConfiguration.get( getContext());

			mTouchSlop = vc.getScaledTouchSlop();
		}

		if ( attributes != null ) {
			TypedArray styleAttributes = getContext().obtainStyledAttributes( attributes, R.styleable.RevealingListView );

			if( styleAttributes != null ) {
				revealMode = styleAttributes.getInt( R.styleable.RevealingListView_revealMode, REVEAL_MODE_NONE );
				allowMultipleReveals = styleAttributes.getBoolean( R.styleable.RevealingListView_revealAllowMultiple, false );
				mRevealOnLongPress = styleAttributes.getBoolean( R.styleable.RevealingListView_revealOnLongPress, false );
				animationTime = styleAttributes.getInteger( R.styleable.RevealingListView_revealAnimationTime, 0 );
				mFrontView = styleAttributes.getResourceId( R.styleable.RevealingListView_revealFrontView, 0 );
				mRevealLeftView = styleAttributes.getResourceId( R.styleable.RevealingListView_revealLeftView, 0 );
				mRevealRightView = styleAttributes.getResourceId( R.styleable.RevealingListView_revealRightView, 0 );
				rightRevealAction = styleAttributes.getInt( R.styleable.RevealingListView_revealRightAction, 1 );
				leftRevealAction = styleAttributes.getInt( R.styleable.RevealingListView_revealLeftAction, 2 );
			}
		}

		if(( mFrontView == 0 ) ||
		   ( mRevealLeftView == 0 )) {
			throw( new RuntimeException( "RevealingListView: Identifiers for the front and back view must be specified." ));
		}

		mTouchListener = new RevealingTouchListener( this, mFrontView, mRevealLeftView, mRevealRightView );

		mTouchListener.setRevealMode( revealMode );
		mTouchListener.setAllowMultipleReveals( allowMultipleReveals );
		mTouchListener.setRevealOnLongPress( mRevealOnLongPress );
		mTouchListener.setRevealRightAction( rightRevealAction );
		mTouchListener.setRevealLeftAction( leftRevealAction );
		if( animationTime != 0 ) {
			mTouchListener.setAnimationTime( animationTime );
		}

		setOnTouchListener( mTouchListener );
	}

	public void setRevealingListViewListener( RevealingListViewListener listener ) {
		mTouchListener.setRevealingListViewListener( listener );
	}

	// see: http://neevek.net/posts/2013/10/13/implementing-onInterceptTouchEvent-and-onTouchEvent-for-ViewGroup.html
	@Override
	public boolean onInterceptTouchEvent( MotionEvent motionEvent ) {
		boolean retValue = false;
		int     action = MotionEventCompat.getActionMasked( motionEvent );
		final   float x = motionEvent.getX();
		final   float y = motionEvent.getY();

		if(( isEnabled()) &&
		   ( mTouchListener != null ) &&
		   ( mTouchListener.isRevealEnabled())) {
			if( mTouchState == TOUCH_STATE_SCROLLING_X ) {
				retValue = mTouchListener.onTouch( this, motionEvent );
			}
			else {
				switch( action ) {
					case MotionEvent.ACTION_MOVE:
						determineIfMoving( x, y );

						retValue = mTouchState == TOUCH_STATE_SCROLLING_Y;
						break;

					case MotionEvent.ACTION_DOWN:
						mTouchListener.onTouch( this, motionEvent );

						mTouchState = TOUCH_STATE_REST;
						mLastMotionX = x;
						mLastMotionY = y;
						break;

					case MotionEvent.ACTION_CANCEL:
						mTouchState = TOUCH_STATE_REST;
						retValue = super.onInterceptTouchEvent( motionEvent );
						break;

					case MotionEvent.ACTION_UP:
						mTouchListener.onTouch( this, motionEvent );
						retValue = mTouchState == TOUCH_STATE_SCROLLING_Y;
						mTouchState = TOUCH_STATE_REST;
						break;
				}

				onTouchEvent( motionEvent );
			}
		}
		else {
			retValue = super.onInterceptTouchEvent( motionEvent );
		}

		return( retValue );
	}

	public void resetTouchInterceptor() {
		mTouchState = TOUCH_STATE_REST;
	}

	private void determineIfMoving( float x, float y ) {
		final int xDiff = (int) Math.abs( x - mLastMotionX );
		final int yDiff = (int) Math.abs( y - mLastMotionY );

		final int touchSlop = this.mTouchSlop;
		boolean xMoved = xDiff > touchSlop;
		boolean yMoved = yDiff > touchSlop;

		if( xMoved ) {
			mTouchState = TOUCH_STATE_SCROLLING_X;
			mLastMotionX = x;
			mLastMotionY = y;
		}

		if( yMoved ) {
			mTouchState = TOUCH_STATE_SCROLLING_Y;
			mLastMotionX = x;
			mLastMotionY = y;
		}
	}
}
