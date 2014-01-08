package com.SecretSquirrel.AndroidNoise.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.SecretSquirrel.AndroidNoise.support.Constants;

// Created by BSwanson on 1/6/14.

public class BaseShellFragment extends Fragment {
	private static final String     TAG = BaseShellFragment.class.getName();

	protected static final String   SHELL_FRAGMENT_KEY = "ShellFragment_FragmentId";

	private int                     mFragmentId;

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		if( savedInstanceState != null ) {
			mFragmentId = savedInstanceState.getInt( SHELL_FRAGMENT_KEY );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				setFragmentId( args.getInt( SHELL_FRAGMENT_KEY ));
			}
		}

		if( Constants.LOG_ERROR ) {
			Log.e( TAG, "ShellActivity Fragment ID cannot be determined." );
		}
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putInt( SHELL_FRAGMENT_KEY, mFragmentId );
	}

	public int getFragmentId() {
		return( mFragmentId );
	}

	public void setFragmentId( int fragmentId ) {
		mFragmentId = fragmentId;
	}
}
