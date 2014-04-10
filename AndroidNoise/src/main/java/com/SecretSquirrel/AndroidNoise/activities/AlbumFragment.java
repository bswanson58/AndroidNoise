package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by BSwanson on 12/23/13.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Artist;

import timber.log.Timber;

public class AlbumFragment extends Fragment {
	private static final String     ARTIST_KEY       = "AlbumFragment_Artist";
	private static final String     ALBUM_KEY        = "AlbumFragment_Album";
	private static final String     EXTERNAL_REQUEST = "AlbumFragment_ExternalRequest";

	private Artist      mArtist;
	private Album       mAlbum;
	private boolean     mIsExternalRequest;

	public static AlbumFragment newInstance( Artist artist, Album album, boolean isExternalRequest ) {
		AlbumFragment   fragment = new AlbumFragment();
		Bundle          bundle = new Bundle();

		bundle.putParcelable( ARTIST_KEY, artist );
		bundle.putParcelable( ALBUM_KEY, album );
		bundle.putBoolean( EXTERNAL_REQUEST, isExternalRequest );

		fragment.setArguments( bundle );

		return( fragment );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_album_shell, container, false );

		if( savedInstanceState != null ) {
			mAlbum = savedInstanceState.getParcelable( ALBUM_KEY );
			mArtist = savedInstanceState.getParcelable( ARTIST_KEY );
			mIsExternalRequest = savedInstanceState.getBoolean( EXTERNAL_REQUEST );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				mArtist = args.getParcelable( ARTIST_KEY );
				mAlbum = args.getParcelable( ALBUM_KEY );
				mIsExternalRequest = args.getBoolean( EXTERNAL_REQUEST );
			}
		}

		if( mAlbum != null ) {
			if( getChildFragmentManager().findFragmentById( R.id.frame_album_info ) == null ) {
				getChildFragmentManager()
						.beginTransaction()
						.replace( R.id.frame_album_info, AlbumInfoFragment.newInstance( mArtist, mAlbum, mIsExternalRequest ))
						.replace( R.id.frame_track_list, TrackListFragment.newInstance( mAlbum.getAlbumId()))
						.commit();
			}
		}
		else {
			Timber.e( "The current album could not be determined." );
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
