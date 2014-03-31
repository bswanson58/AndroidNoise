package com.SecretSquirrel.AndroidNoise.support;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.SecretSquirrel.AndroidNoise.R;

import java.util.concurrent.TimeUnit;

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

	public static String formatPlaybackPosition( long duration, boolean countdown ) {
		String  retValue = formatTrackDuration( duration );

		if( countdown ) {
			retValue = "-" + retValue;
		}

		return( retValue );
	}

	public static String formatTrackDuration( long duration ) {
		return( String.format( "%d:%02d",
				TimeUnit.MILLISECONDS.toMinutes( duration ),
				TimeUnit.MILLISECONDS.toSeconds( duration ) -
						TimeUnit.MINUTES.toSeconds( TimeUnit.MILLISECONDS.toMinutes( duration ))));

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

	public static void displayKeyboard( Activity activity, View forView ) {
		if( forView.requestFocus()) {
			InputMethodManager inputManager = (InputMethodManager)activity.getSystemService( Context.INPUT_METHOD_SERVICE );

			inputManager.toggleSoftInput( InputMethodManager.SHOW_IMPLICIT, 0 );
//			inputManager.showSoftInput( forView, InputMethodManager.SHOW_IMPLICIT );
		}
	}

	// from: http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string?rq=1
	public static String convertStreamToString( java.io.InputStream is ) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter( "\\A" );
		return s.hasNext() ? s.next() : "";
	}
}
