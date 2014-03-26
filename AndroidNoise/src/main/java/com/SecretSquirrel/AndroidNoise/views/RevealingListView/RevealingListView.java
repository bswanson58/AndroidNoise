package com.secretSquirrel.sandbox.RevealingListView;

// Secret Squirrel Software - Created by BSwanson on 3/21/14.

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

import com.secretSquirrel.sandbox.R;

@SuppressWarnings( "unused" )
public class RevealingListView extends ListView {
	public static final int         REVEAL_MODE_NONE = 0;
	public static final int         REVEAL_MODE_RIGHT = 1;
	public static final int         REVEAL_MODE_LEFT = 2;
	public static final int         REVEAL_MODE_BOTH = 3;

	private int                     mFrontView;
	private int                     mRevealLeftView;
	private int                     mRevealRightView;
	private int                     mRevealMode;
	private boolean                 mRevealOnLongPress;
	private boolean                 mCloseOnMoveList;
	private long                    mRevealAnimationTime;
	private RevealingTouchListener  mTouchListener;

	public RevealingListView( Context context ) {
		super( context );
	}

	public RevealingListView( Context context, AttributeSet attrs ) {
		super( context, attrs );

		initialize( attrs );
	}

	public RevealingListView( Context context, AttributeSet attrs, int defStyle ) {
		super( context, attrs, defStyle );

		initialize( attrs );
	}

	private void initialize( AttributeSet attributes ) {
		mRevealMode = REVEAL_MODE_NONE;
		mCloseOnMoveList = true;

		if ( attributes != null ) {
			TypedArray styleAttributes = getContext().obtainStyledAttributes( attributes, R.styleable.RevealingListView );

			if( styleAttributes != null ) {
				mRevealMode = styleAttributes.getInt( R.styleable.RevealingListView_revealMode, REVEAL_MODE_NONE );
				mRevealOnLongPress = styleAttributes.getBoolean( R.styleable.RevealingListView_revealOnLongPress, false );
				mCloseOnMoveList = styleAttributes.getBoolean( R.styleable.RevealingListView_revealCloseAllItemsWhenMoveList, true );
				mRevealAnimationTime = styleAttributes.getInteger( R.styleable.RevealingListView_revealAnimationTime, 0 );
				mFrontView = styleAttributes.getResourceId( R.styleable.RevealingListView_revealFrontView, 0 );
				mRevealLeftView = styleAttributes.getResourceId( R.styleable.RevealingListView_revealLeftView, 0 );
				mRevealRightView = styleAttributes.getResourceId( R.styleable.RevealingListView_revealRightView, 0 );
			}
		}

		if(( mFrontView == 0 ) ||
		   ( mRevealLeftView == 0 )) {
			throw( new RuntimeException( "RevealingListView: Identifiers for the front and back view must be specified." ));
		}

		mTouchListener = new RevealingTouchListener( this, mFrontView, mRevealLeftView, mRevealRightView );

		mTouchListener.setRevealMode( mRevealMode );
		mTouchListener.setRevealOnLongPress( mRevealOnLongPress );

		setOnTouchListener( mTouchListener );
	}

	/**
	 * Indicates no movement
	 */
	private final static int TOUCH_STATE_REST = 0;

	/**
	 * State scrolling x position
	 */
	private final static int TOUCH_STATE_SCROLLING_X = 1;

	/**
	 * State scrolling y position
	 */
	private final static int TOUCH_STATE_SCROLLING_Y = 2;

	private int touchState = TOUCH_STATE_REST;
	private float lastMotionX;
	private float lastMotionY;
	private int touchSlop;

	@Override
	public boolean onInterceptTouchEvent( MotionEvent motionEvent ) {
		int action = MotionEventCompat.getActionMasked( motionEvent );
		final float x = motionEvent.getX();
		final float y = motionEvent.getY();

		if(( isEnabled()) &&
		   ( mTouchListener != null ) &&
		   ( mTouchListener.isRevealEnabled())) {

			if( touchState == TOUCH_STATE_SCROLLING_X ) {
				return mTouchListener.onTouch( this, motionEvent );
			}

			switch( action ) {
				case MotionEvent.ACTION_MOVE:
					checkInMoving( x, y );
					return touchState == TOUCH_STATE_SCROLLING_Y;

				case MotionEvent.ACTION_DOWN:
					super.onInterceptTouchEvent( motionEvent );
					mTouchListener.onTouch( this, motionEvent );
					touchState = TOUCH_STATE_REST;
					lastMotionX = x;
					lastMotionY = y;
					return false;

				case MotionEvent.ACTION_CANCEL:
					touchState = TOUCH_STATE_REST;
					break;

				case MotionEvent.ACTION_UP:
					mTouchListener.onTouch( this, motionEvent );
					return touchState == TOUCH_STATE_SCROLLING_Y;

				default:
					break;
			}
		}

		return super.onInterceptTouchEvent( motionEvent );
	}

	/**
	 * Check if the user is moving the cell
	 *
	 * @param x Position X
	 * @param y Position Y
	 */
	private void checkInMoving(float x, float y) {
		final int xDiff = (int) Math.abs( x - lastMotionX );
		final int yDiff = (int) Math.abs( y - lastMotionY );

		final int touchSlop = this.touchSlop;
		boolean xMoved = xDiff > touchSlop;
		boolean yMoved = yDiff > touchSlop;

		if( xMoved ) {
			touchState = TOUCH_STATE_SCROLLING_X;
			lastMotionX = x;
			lastMotionY = y;
		}

		if( yMoved ) {
			touchState = TOUCH_STATE_SCROLLING_Y;
			lastMotionX = x;
			lastMotionY = y;
		}
	}
}
