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

import de.greenrobot.event.EventBus;

public class ArtistFragment extends Fragment {
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View                myView = inflater.inflate( R.layout.fragment_artist_shell, container, false );
		FragmentManager     fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		ArtistInfoFragment  artistInfoFragment = new ArtistInfoFragment();
		AlbumListFragment   albumListFragment = new AlbumListFragment();

		fragmentTransaction.add( R.id.frame_artist_info, artistInfoFragment );
		fragmentTransaction.add( R.id.frame_album_list, albumListFragment );
		fragmentTransaction.commit();

		return( myView );
	}
}
