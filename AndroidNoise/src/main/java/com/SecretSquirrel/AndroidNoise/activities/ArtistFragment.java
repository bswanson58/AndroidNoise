package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.support.Constants;

public class ArtistFragment extends Fragment {
	private static final String     TAG = ArtistFragment.class.getName();
	private static final String     ARTIST_KEY = "ArtistFragment_ArtistId";

	private Artist                  mCurrentArtist;

	public static ArtistFragment newInstance( Artist artist ) {
		ArtistFragment  fragment = new ArtistFragment();
		Bundle          args = new Bundle();

		args.putParcelable( ARTIST_KEY, artist );
		fragment.setArguments( args );

		return( fragment );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View                myView = inflater.inflate( R.layout.fragment_artist_shell, container, false );

		if( savedInstanceState != null ) {
			mCurrentArtist = savedInstanceState.getParcelable( ARTIST_KEY );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				mCurrentArtist = args.getParcelable( ARTIST_KEY );
			}
		}

		if( mCurrentArtist != null ) {
			getChildFragmentManager()
					.beginTransaction()
					.replace( R.id.frame_artist_info, ArtistInfoFragment.newInstance( mCurrentArtist ))
					.replace( R.id.frame_album_list, AlbumListFragment.newInstance( mCurrentArtist.getArtistId()))
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

		outState.putParcelable( ARTIST_KEY, mCurrentArtist );
	}
}
