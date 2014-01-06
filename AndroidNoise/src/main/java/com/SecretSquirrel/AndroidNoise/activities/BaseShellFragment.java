package com.SecretSquirrel.AndroidNoise.activities;

import android.support.v4.app.Fragment;

// Created by BSwanson on 1/6/14.

public class BaseShellFragment extends Fragment {
	private final int   mFragmentId;

	protected BaseShellFragment( int fragmentId ) {
		mFragmentId = fragmentId;
	}

	public int getFragmentId() {
		return( mFragmentId );
	}
}
