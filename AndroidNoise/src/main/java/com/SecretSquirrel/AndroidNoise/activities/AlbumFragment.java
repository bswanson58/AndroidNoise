package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.support.Constants;

public class AlbumFragment extends Fragment {
	private static final String     TAG = AlbumFragment.class.getName();
	private static final String     ARTIST_KEY  = "AlbumFragment_Artist";
	private static final String     ALBUM_KEY   = "AlbumFragment_Album";

	private Artist  mArtist;
	private Album   mAlbum;

	public static AlbumFragment newInstance( Artist artist, Album album ) {
		AlbumFragment   fragment = new AlbumFragment();
		Bundle          bundle = new Bundle();

		bundle.putParcelable( ARTIST_KEY, artist );
		bundle.putParcelable( ALBUM_KEY, album );
		fragment.setArguments( bundle );

		return( fragment );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_album_shell, container, false );

		if( savedInstanceState != null ) {
			mAlbum = savedInstanceState.getParcelable( ALBUM_KEY );
			mArtist = savedInstanceState.getParcelable( ARTIST_KEY );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				mArtist = args.getParcelable( ARTIST_KEY );
				mAlbum = args.getParcelable( ALBUM_KEY );
			}
		}

		if( mAlbum != null ) {
			if( getChildFragmentManager().findFragmentById( R.id.frame_album_info ) == null ) {
				getChildFragmentManager()
						.beginTransaction()
						.replace( R.id.frame_album_info, AlbumInfoFragment.newInstance( mArtist, mAlbum ))
						.replace( R.id.frame_track_list, TrackListFragment.newInstance( mAlbum.getAlbumId()))
						.commit();
			}
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

		outState.putParcelable( ARTIST_KEY, mArtist );
		outState.putParcelable( ALBUM_KEY, mAlbum );
	}
}
