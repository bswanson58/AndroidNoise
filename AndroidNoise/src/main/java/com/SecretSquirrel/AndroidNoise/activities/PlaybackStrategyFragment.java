package com.SecretSquirrel.AndroidNoise.activities;

// Created by BSwanson on 3/10/14.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;

public class PlaybackStrategyFragment extends Fragment {
	public static PlaybackStrategyFragment newInstance() {
		return( new PlaybackStrategyFragment());
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_playback_strategy, container, false );

		return( myView );
	}
}
