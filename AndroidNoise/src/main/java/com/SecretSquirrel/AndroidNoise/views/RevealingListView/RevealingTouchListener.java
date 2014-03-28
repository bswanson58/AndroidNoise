package com.secretSquirrel.sandbox.RevealingListView;

// Secret Squirrel Software - Created by BSwanson on 3/21/14.

import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ListView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewHelper;

import java.util.HashMap;
import java.util.Set;

import static com.nineoldandroids.view.ViewHelper.setTranslationX;
import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

@SuppressWarnings( "unused" )
public class RevealingTouchListener implements View.OnTouchListener {
	private static final String         TAG = RevealingTouchListener.class.getName();

	private static final int            OPEN_NEITHER = 0;
	private static final int            OPEN_RIGHT = 1;
	private static final int            OPEN_LEFT = 2;

	private final RevealingListView     mListView;
	private final int                   mFrontViewId;
	private final int                   mRevealLeftViewId;
	private final int                   mRevealRightViewId;
	private long                        mAnimationTime;
	private int                         mRevealMode;
	private boolean                     mRevealOnLongPress;
	private boolean                     mAllowMultipleReveals;
	private VelocityTracker             mVelocityTracker;
	private View                        mFrontView;
	private View                        mBackView;
	private View                        mBackLeftView;
	private View                        mBackRightView;
	private HashMap<Integer, Integer>   mOpenPositions;
	private int                         mBackViewWidth;
	private float                       mTouchDownX;
	private int                         mTouchDownListPosition;
	private boolean                     mIsRevealing;
	private int                         mMinimumFlingVelocity;
	private int                         mMaximumFlingVelocity;
	private int                         mTouchSlop;

	private int swipeActionLeft;
	private int swipeActionRight;

	public RevealingTouchListener( RevealingListView listView, int frontView, int revealLeftView, int revealRightView ) {
		mListView = listView;
		mFrontViewId = frontView;
		mRevealLeftViewId = revealLeftView;
		mRevealRightViewId = revealRightView;
		mTouchDownListPosition = ListView.INVALID_POSITION;

		mOpenPositions = new HashMap<Integer, Integer>();

		if( mListView.getContext() != null ) {
			ViewConfiguration vc = ViewConfiguration.get( mListView.getContext());

			mTouchSlop = vc.getScaledTouchSlop();
			mMinimumFlingVelocity = vc.getScaledMinimumFlingVelocity();
			mMaximumFlingVelocity = vc.getScaledMaximumFlingVelocity();
		}

		mAnimationTime = mListView.getContext().getResources().getInteger( android.R.integer.config_shortAnimTime );
	}

	public boolean isRevealEnabled() {
		return( mRevealMode != RevealingListView.REVEAL_MODE_NONE );
	}

	public void setRevealMode( int mode ) {
		mRevealMode = mode;
	}

	public void setAllowMultipleReveals( boolean allowMultipleReveals ) {
		mAllowMultipleReveals = allowMultipleReveals;
	}

	public void setRevealOnLongPress( boolean revealOnLongPress ) {
		mRevealOnLongPress = revealOnLongPress;
	}

	public void setAnimationTime( long animationTime ) {
		mAnimationTime = animationTime;
	}

	@Override
	public boolean onTouch( View view, MotionEvent motionEvent ) {
		boolean retValue = false;

		if( canReveal()) {
			switch ( MotionEventCompat.getActionMasked( motionEvent )) {
				case MotionEvent.ACTION_DOWN:
					retValue = onTouchDown( view, motionEvent );
					break;

				case MotionEvent.ACTION_MOVE:
					retValue = onTouchMove( view, motionEvent );
					break;

				case MotionEvent.ACTION_UP:
					retValue = onTouchUp( view, motionEvent );
					break;
			}
		}

		return( retValue );
	}

	private boolean onTouchDown( View view, MotionEvent motionEvent ) {
		int[] listViewCoordinates = new int[2];
		mListView.getLocationOnScreen( listViewCoordinates );

		int x = (int)motionEvent.getRawX() - listViewCoordinates[0];
		int y = (int)motionEvent.getRawY() - listViewCoordinates[1];

		for( int i = 0; i < mListView.getChildCount(); i++ ) {
			View    child;
			Rect    rect = new Rect();

			child = mListView.getChildAt( i );
			if( child != null ) {
				child.getHitRect( rect );

				int childPosition = mListView.getPositionForView( child );

				// don't allow swiping if this is on the header or footer or IGNORE_ITEM_VIEW_TYPE or enabled is false on the adapter
				boolean allowSwipe = (( mListView.getAdapter().isEnabled( childPosition )) &&
									  ( mListView.getAdapter().getItemViewType( childPosition ) >= 0 ));

				if(( allowSwipe ) &&
				   ( rect.contains( x, y ))) {
					setFrontView( child.findViewById( mFrontViewId ));

					mTouchDownX = motionEvent.getRawX();
					mTouchDownListPosition = childPosition;

					mVelocityTracker = VelocityTracker.obtain();
					if( mVelocityTracker != null ) {
						mVelocityTracker.addMovement( motionEvent );
					}

					if( mRevealLeftViewId > 0 ) {
						mBackLeftView = child.findViewById( mRevealLeftViewId );

						if( mBackLeftView != null ) {
							if( canRevealLeft() && !isOpenRight( mTouchDownListPosition )) {
								setBackView( mBackLeftView );
							}
							else {
								mBackLeftView.setVisibility( View.INVISIBLE );
							}
						}
					}
					if( mRevealRightViewId > 0 ) {
						mBackRightView = child.findViewById( mRevealRightViewId );

						if( mBackRightView != null ) {
							if(!canRevealBoth() && canRevealRight()) {
								setBackView( mBackRightView );
							}
							else {
								if(!isOpenRight( mTouchDownListPosition )) {
									mBackRightView.setVisibility( View.INVISIBLE );
								}
							}
						}
					}

					break;
				}
			}
		}

		return( mTouchDownListPosition != ListView.INVALID_POSITION );
	}

	private boolean onTouchMove( View view, MotionEvent motionEvent ) {
		boolean retValue = false;

		if(( mVelocityTracker == null ) ||
		   ( mTouchDownListPosition == ListView.INVALID_POSITION )) {
			return( false );
		}

		mVelocityTracker.addMovement( motionEvent );
		mVelocityTracker.computeCurrentVelocity( 300 );

		float velocityX = Math.abs( mVelocityTracker.getXVelocity());
		float velocityY = Math.abs( mVelocityTracker.getYVelocity());
		float deltaX    = motionEvent.getRawX() - mTouchDownX;

		boolean canMove = canRevealBoth();
		if(!canRevealBoth()) {
			canMove = true;

			if( isOpen( mTouchDownListPosition )) {
				if( canRevealLeft() && deltaX > 0 ) {
					canMove = false;
				}
				if( canRevealRight() && deltaX < 0 ) {
					canMove = false;
				}
			}
			else {
				if( canRevealLeft() && deltaX < 0 ) {
					canMove = false;
				}
				if( canRevealRight() && deltaX > 0 ) {
					canMove = false;
				}
			}
		}

		if(( Math.abs( deltaX ) > mTouchSlop ) &&
		   (!mIsRevealing) &&
		   ( velocityX > velocityY )) {
			mIsRevealing = true;

//			if( isOpen( mTouchDownListPosition )) {
//				mListView.onStartClose(downPosition, swipingRight);
//			} else {
//				mListView.onStartOpen(downPosition, swipeCurrentAction, swipingRight);
//			}

			mListView.requestDisallowInterceptTouchEvent( true );

			MotionEvent cancelEvent = MotionEvent.obtain( motionEvent );
			if( cancelEvent != null ) {
				cancelEvent.setAction( MotionEvent.ACTION_CANCEL | ( MotionEventCompat.getActionIndex(motionEvent) << MotionEventCompat.ACTION_POINTER_INDEX_SHIFT ));
				mListView.onTouchEvent( cancelEvent );
			}

			if(!mAllowMultipleReveals ) {
				Log.d( TAG, String.format( "Closing all except: %d", mTouchDownListPosition ));

				closeAllExcept( mTouchDownListPosition );
			}
		}

		if(( mIsRevealing ) &&
		   ( canMove )) {
			if( isOpen( mTouchDownListPosition )) {
				deltaX += isOpenRight( mTouchDownListPosition ) ? -mBackViewWidth : mBackViewWidth;
			}

			deltaX = Math.min( deltaX, mBackViewWidth );
			deltaX = Math.max( deltaX, -mBackViewWidth );

			moveFrontView( deltaX );

			retValue = true;
		}

		return( retValue );
	}

	private boolean onTouchUp( View view, MotionEvent motionEvent ) {
		if(( mVelocityTracker == null ) ||
		   ( mTouchDownListPosition == ListView.INVALID_POSITION )) {
			return( false );
		}

		if( mIsRevealing ) {
			float deltaX = motionEvent.getRawX() - mTouchDownX;
			mVelocityTracker.addMovement(motionEvent);
			mVelocityTracker.computeCurrentVelocity( 300 );

			float velocityX = Math.abs( mVelocityTracker.getXVelocity());
			float velocityY = Math.abs( mVelocityTracker.getYVelocity());

			if(( velocityX <= mMinimumFlingVelocity ) ||
			   ( velocityX >= mMaximumFlingVelocity ) ||
			   ( velocityY * 2 > velocityX )) {
				velocityX = 0;
			}

			int moveToState = OPEN_NEITHER;

			if( isOpen( mTouchDownListPosition )) {
				if( velocityX > 0 ) {
					if( mVelocityTracker.getXVelocity() > 0 ) {
						moveToState = isOpenRight( mTouchDownListPosition ) ? OPEN_NEITHER : OPEN_RIGHT;
					}
					else {
						moveToState = isOpenRight( mTouchDownListPosition ) ? OPEN_LEFT : OPEN_NEITHER;
					}
				}
				else {
					if( Math.abs( ViewHelper.getX( mFrontView )) > ( mBackViewWidth / 2 )) {
						if( deltaX > 0 ) {
							moveToState = canRevealLeft() ? OPEN_LEFT : OPEN_NEITHER;
						}
						else {
							moveToState = canRevealRight() ? OPEN_RIGHT : OPEN_NEITHER;
						}
					}
				}
			}
			else {
				if( velocityX > 0 ) {
					moveToState = mVelocityTracker.getXVelocity() > 0 ? OPEN_LEFT : OPEN_RIGHT;
				}
				else {
					if( Math.abs( ViewHelper.getX( mFrontView )) > ( mBackViewWidth / 2 )) {
						if( deltaX > 0 ) {
							moveToState = canRevealLeft() ? OPEN_LEFT : OPEN_NEITHER;
						}
						else {
							moveToState = canRevealRight() ? OPEN_RIGHT : OPEN_NEITHER;
						}
					}
				}
			}

			animateViewToState( mFrontView, moveToState, mTouchDownListPosition, mBackView );
			setOpenState( mTouchDownListPosition, moveToState );
		}

		releaseCell( mTouchDownListPosition );

		mVelocityTracker.recycle();
		mVelocityTracker = null;

		return( false );
	}

	public void closeAll() {
		closeAllExcept( ListView.INVALID_POSITION );
	}

	private void closeAllExcept( int exceptPosition ) {
		Integer[]   keySet = new Integer[mOpenPositions.size()];

		mOpenPositions.keySet().toArray( keySet );

		for( int position : keySet ) {
			int state = mOpenPositions.get( position );

			if(( position != exceptPosition ) &&
			   ( state != OPEN_NEITHER )) {
				View    view = mListView.getChildAt( position - mListView.getFirstVisiblePosition());

				if( view != null ) {
					View    frontView = view.findViewById( mFrontViewId );

					if( frontView != null ) {
						animate( frontView )
								.translationX( 0 )
								.setDuration( mAnimationTime )
								.setListener( new AnimatorListenerAdapter() {
									@Override
									public void onAnimationEnd( Animator animation ) {
										Log.d( TAG, String.format( "closeAllExcept" ));
									}
								});

					}
				}

				setOpenState( position, OPEN_NEITHER );
			}
		}
	}

	public void moveFrontView( float deltaX ) {
//		mListView.onMove( mTouchDownListPosition, deltaX );

		float posX = ViewHelper.getX( mFrontView ) + deltaX;

		if( posX > 0 ) {
			setBackView( mBackLeftView );
		}
		if( posX < 0 ) {
			setBackView( mBackRightView );
		}

		setTranslationX( mFrontView, deltaX );
	}

	private void animateViewToState( final View view, final int toState, final int position, final View backView ) {
		int moveTo = 0;

		switch( toState ) {
			case OPEN_NEITHER:
				moveTo = 0;
				break;

			case OPEN_RIGHT:
				setBackView( mBackRightView );

				moveTo = -mBackViewWidth;
				break;

			case OPEN_LEFT:
				setBackView( mBackLeftView );

				moveTo = mBackViewWidth;
				break;
		}

		animate( view )
				.translationX( moveTo )
				.setDuration( mAnimationTime )
				.setListener( new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd( Animator animation ) {
						if(( toState == OPEN_NEITHER ) &&
						   ( backView != null )) {
							backView.setVisibility( View.INVISIBLE );
						}

						Log.d( TAG, String.format( "animateViewToState, position: %d", position ));

						//							if( isOpen( position )) {
						//								mListView.onOpened( position, swapRight );
						//							} else {
						//								mListView.onClosed( position, isOpenRight( position ));
						//							}
					}
				} );
	}

	private void releaseCell( int position ) {
		clearFrontView();

		if( mBackView != null ) {
			mBackView.setOnClickListener( null );
			mBackView = null;
		}

		mBackLeftView = null;
		mBackRightView = null;

		mIsRevealing = false;
		mTouchDownX = 0;
		mTouchDownListPosition = ListView.INVALID_POSITION;
		mListView.resetTouchInterceptor();
	}

	private int getOpenState( int position ) {
		int retValue = OPEN_NEITHER;

		if( mOpenPositions.containsKey( position )) {
			retValue = mOpenPositions.get( position );
		}

		return( retValue );
	}

	private void setOpenState( int position, int state ) {
		if( state == OPEN_NEITHER ) {
			mOpenPositions.remove( position );
		}
		else {
			mOpenPositions.put( position, state );
		}
	}

	private boolean isOpen( int position ) {
		return( mOpenPositions.containsKey( position ));
	}

	private boolean isOpenRight( int position ) {
		boolean retValue = isOpen( position );

		if( retValue ) {
			retValue = mOpenPositions.get( position ) == OPEN_RIGHT;
		}

		return( retValue );
	}

	private void setFrontView( View frontView ) {
		clearFrontView();

		mFrontView = frontView;
		if( mRevealOnLongPress ) {
			mFrontView.setOnLongClickListener( new View.OnLongClickListener() {
				@Override
				public boolean onLongClick( View view ) {
					if(( mTouchDownListPosition != ListView.INVALID_POSITION ) &&
					   (!mIsRevealing )) {
						int     openPosition = OPEN_NEITHER;

						if(!isOpen( mTouchDownListPosition )) {
							openPosition = canRevealLeft() ? OPEN_LEFT : canRevealRight() ? OPEN_RIGHT : OPEN_NEITHER;
						}

						animateViewToState( view, openPosition, mTouchDownListPosition, mBackView );

						closeAllExcept( mTouchDownListPosition );
						setOpenState( mTouchDownListPosition, openPosition );
						releaseCell( mTouchDownListPosition );
					}

					return( false );
				}
			});
		}
	}

	private void clearFrontView() {
		if( mFrontView != null ) {
			mFrontView.setOnLongClickListener( null );
			mFrontView = null;
		}
	}

	private void setBackView( View backView ) {
		if( mBackView != backView ) {
			clearBackView();

			mBackView = backView;
			mBackView.setVisibility( View.VISIBLE );
			mBackViewWidth = mBackView.getWidth();
		}
	}

	private void clearBackView() {
		if( mBackView != null ) {
			mBackView.setOnClickListener( null );
			mBackView.setVisibility( View.INVISIBLE );
			mBackView = null;
		}
	}

	private boolean canReveal() {
		return( mRevealMode != RevealingListView.REVEAL_MODE_NONE );
	}

	private boolean canRevealLeft() {
		return( mRevealMode == RevealingListView.REVEAL_MODE_BOTH || mRevealMode == RevealingListView.REVEAL_MODE_LEFT );
	}

	private boolean canRevealRight() {
		return( mRevealMode == RevealingListView.REVEAL_MODE_BOTH || mRevealMode == RevealingListView.REVEAL_MODE_RIGHT );
	}

	private boolean canRevealBoth() {
		return ( mRevealMode == RevealingListView.REVEAL_MODE_BOTH );
	}
}
