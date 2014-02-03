package com.SecretSquirrel.AndroidNoise.ui;

// Secret Squirrel Software - Created by bswanson on 2/3/14.

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;

//from: http://stackoverflow.com/questions/20973089/android-add-view-with-expand-animation-without-blinking

@SuppressWarnings("unused")
public class ScaledHeightAnimation extends ScaleAnimation {
	private View    mViewToScale;
	private float   mFromHeight;
	private float   mToHeight;

	public ScaledHeightAnimation( View viewToScale, float fromY, float toY ) {
		super( 1, 1, fromY, toY );
		init( viewToScale, fromY, toY );
	}

	private void init( View viewToScale, float fromY, float toY ) {
		this.mViewToScale = viewToScale;
		viewToScale.measure( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
		mFromHeight = viewToScale.getMeasuredHeight() * fromY;
		mToHeight = viewToScale.getMeasuredHeight() * toY;
	}

	@Override
	protected void applyTransformation( float interpolatedTime, Transformation t ) {
		super.applyTransformation( interpolatedTime, t );

		mViewToScale.getLayoutParams().height = (int)( mFromHeight * ( 1 - interpolatedTime ) + mToHeight * interpolatedTime );
		mViewToScale.requestLayout();
	}
}
