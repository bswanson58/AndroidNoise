package com.SecretSquirrel.AndroidNoise.support;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.SecretSquirrel.AndroidNoise.R;

// Created by BSwanson on 1/26/14.

public class NoiseUtils {
	private static final String     TAG = NoiseUtils.class.getName();

	public static String FormatPublishedYear( Context context, long year ) {
		String retValue = "";

		if( year > 0 ) {
			if( year == Constants.VARIOUS_YEARS ) {
				retValue = context.getResources().getString( R.string.published_year_various );
			}
			else {
				retValue = String.format( "%4d", year );
			}
		}

		return (retValue);
	}

	public static void hideKeyboard( Activity activity ) {
		if( activity != null ) {
			try {
				InputMethodManager inputManager = (InputMethodManager)activity.getSystemService( Context.INPUT_METHOD_SERVICE );

				if( activity.getCurrentFocus() != null ) {
					inputManager.hideSoftInputFromWindow( activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS );
				}
			}
			catch( Exception e ) {
				if( Constants.LOG_ERROR ) {
					Log.e( TAG, e.toString(), e );
				}
			}
		}
	}
}
