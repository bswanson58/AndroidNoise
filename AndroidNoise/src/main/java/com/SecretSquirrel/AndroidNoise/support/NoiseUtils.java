package com.SecretSquirrel.AndroidNoise.support;

import android.content.Context;

import com.SecretSquirrel.AndroidNoise.R;

// Created by BSwanson on 1/26/14.

public class NoiseUtils {
	public static String FormatPublishedYear( Context context, long year ) {
		String  retValue = "";

		if( year > 0 ) {
			if( year == Constants.VARIOUS_YEARS ) {
				retValue = context.getResources().getString( R.string.published_year_various );
			}
			else {
				retValue = String.format( "%4d", year );
			}
		}

		return( retValue );
	}
}
