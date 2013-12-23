package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;

public class AlbumFragment extends Fragment {
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View                myView = inflater.inflate( R.layout.fragment_album_shell, container, false );
		FragmentManager     fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		AlbumInfoFragment   albumInfoFragment = new AlbumInfoFragment();
		TrackListFragment   trackListFragment = new TrackListFragment();

		fragmentTransaction.add( R.id.frame_album_info, albumInfoFragment );
		fragmentTransaction.add( R.id.frame_track_list, trackListFragment );
		fragmentTransaction.commit();

		return( myView );
	}
}
