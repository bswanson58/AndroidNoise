package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/18/13.

import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.support.Constants;

public class AlbumInfoFragment extends Fragment {
	private static final String     TAG = AlbumInfoFragment.class.getName();
	private static final String     ALBUM_KEY = "AlbumInfoFragment_AlbumId";

	private long        mCurrentAlbum;

	public static AlbumInfoFragment newInstance( long albumId ) {
		AlbumInfoFragment   fragment = new AlbumInfoFragment();
		Bundle              args = new Bundle();

		args.putLong( ALBUM_KEY, albumId );
		fragment.setArguments( args );

		return( fragment );
	}

	public AlbumInfoFragment() {
		mCurrentAlbum = Constants.NULL_ID;
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View myView = inflater.inflate( R.layout.fragment_album_info, container, false );

		if( savedInstanceState != null ) {
			mCurrentAlbum = savedInstanceState.getLong( ALBUM_KEY, Constants.NULL_ID );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				mCurrentAlbum = args.getLong( ALBUM_KEY, Constants.NULL_ID );
			}
		}

		// myView.findViewById(  )

		if( mCurrentAlbum != Constants.NULL_ID ) {

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
