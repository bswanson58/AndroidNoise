package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.support.Constants;

public class AlbumFragment extends Fragment {
	private static final String     TAG = AlbumFragment.class.getName();
	private static final String     ALBUM_KEY = "AlbumFragment_AlbumId";

	private long        mCurrentAlbum;

	public static AlbumFragment newInstance( long albumId ) {
		AlbumFragment   fragment = new AlbumFragment();
		Bundle          bundle = new Bundle();

		bundle.putLong( ALBUM_KEY, albumId );
		fragment.setArguments( bundle );

		return( fragment );
	}

	public AlbumFragment() {
		mCurrentAlbum = Constants.NULL_ID;
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_album_shell, container, false );

		if( savedInstanceState != null ) {
			mCurrentAlbum = savedInstanceState.getLong( ALBUM_KEY, Constants.NULL_ID );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				mCurrentAlbum = args.getLong( ALBUM_KEY, Constants.NULL_ID );
			}
		}

		if( mCurrentAlbum != Constants.NULL_ID ) {
			getChildFragmentManager()
					.beginTransaction()
					.replace( R.id.frame_album_info, AlbumInfoFragment.newInstance( mCurrentAlbum ))
					.replace( R.id.frame_track_list, TrackListFragment.newInstance( mCurrentAlbum ))
					.commit();
		}
		else {
			if( Constants.LOG_ERROR ) {
				Log.e( TAG, "The current album could not be determined." );
			}
		}

		return( myView );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putLong( ALBUM_KEY, mCurrentAlbum );
	}
}
