package com.SecretSquirrel.AndroidNoise.activities;// Created by BSwanson on 3/10/14.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;

public class PlaybackShellFragment extends Fragment {
	public static PlaybackShellFragment newInstance() {
		return( new PlaybackShellFragment());
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		getChildFragmentManager()
				.beginTransaction()
				.replace( R.id.transport_playback_status, PlaybackStatusFragment.newInstance())
				.replace( R.id.transport_playback_information, PlaybackPagerFragment.newInstance())
				.replace( R.id.transport_controls, TransportFragment.newInstance())
				.commit();

		return( inflater.inflate( R.layout.fragment_playback_shell, container, false ));
	}
}

