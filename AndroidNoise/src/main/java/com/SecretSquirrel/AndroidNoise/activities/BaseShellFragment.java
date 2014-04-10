package com.SecretSquirrel.AndroidNoise.activities;

// Created by BSwanson on 1/6/14.

import android.os.Bundle;
import android.support.v4.app.Fragment;

import timber.log.Timber;

public class BaseShellFragment extends Fragment {
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

		if( mFragmentId == 0 ) {
			Timber.e( "LibraryActivity Fragment ID cannot be determined." );
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
