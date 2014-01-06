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

public class ArtistFragment extends Fragment {
	private static final String     TAG = ArtistFragment.class.getName();
	private static final String     ARTIST_KEY = "ArtistFragment_ArtistId";

	private long        mCurrentArtist;

	public static ArtistFragment newInstance( long artistId ) {
		ArtistFragment  fragment = new ArtistFragment();
		Bundle          args = new Bundle();

		args.putLong( ARTIST_KEY, artistId );
		fragment.setArguments( args );

		return( fragment );
	}

	public ArtistFragment() {
		mCurrentArtist = Constants.NULL_ID;
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View                myView = inflater.inflate( R.layout.fragment_artist_shell, container, false );

		if( savedInstanceState != null ) {
			mCurrentArtist = savedInstanceState.getLong( ARTIST_KEY, Constants.NULL_ID );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				mCurrentArtist = args.getLong( ARTIST_KEY, Constants.NULL_ID );
			}
		}

		if( mCurrentArtist != Constants.NULL_ID ) {
			getChildFragmentManager()
					.beginTransaction()
					.replace( R.id.frame_artist_info, ArtistInfoFragment.newInstance( mCurrentArtist ))
					.replace( R.id.frame_album_list, AlbumListFragment.newInstance( mCurrentArtist ))
					.commit();
		}
		else {
			if( Constants.LOG_ERROR ) {
				Log.e( TAG, "The current artist could not be determined." );
			}
		}

		return( myView );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putLong( ARTIST_KEY, mCurrentArtist );
	}
}
