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
import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.events.EventAlbumSelected;
import com.SecretSquirrel.AndroidNoise.events.EventArtistSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;

import de.greenrobot.event.EventBus;

public class ShellLibraryFragment extends Fragment {
	private static final String LIBRARY_STATE               = "ShellLibraryFragment_LibraryState";
	private static final int    LIBRARY_STATE_ARTIST_LIST   = 0;
	private static final int    LIBRARY_STATE_ARTIST        = 1;
	private static final int    LIBRARY_STATE_ALBUM         = 2;

	private static final String LIBRARY_CURRENT_ARTIST      = "ShellLibraryFragment_CurrentArtist";
	private static final String LIBRARY_CURRENT_ALBUM      = "ShellLibraryFragment_CurrentAlbum";

	private FragmentManager     mFragmentManager;
	private int                 mCurrentState;
	private long                mCurrentArtist;
	private long                mCurrentAlbum;

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mFragmentManager = getFragmentManager();

		mCurrentState = LIBRARY_STATE_ARTIST_LIST;
		mCurrentArtist = 0;
		mCurrentAlbum = 0;
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View                myView = inflater.inflate( R.layout.fragment_library_shell, container, false );
		Fragment            fragment = null;

		if( savedInstanceState != null ) {
			mCurrentState = savedInstanceState.getInt( LIBRARY_STATE, LIBRARY_STATE_ARTIST_LIST );
			mCurrentArtist = savedInstanceState.getLong( LIBRARY_CURRENT_ARTIST, 0 );
			mCurrentAlbum = savedInstanceState.getLong( LIBRARY_CURRENT_ALBUM, 0 );

			switch( mCurrentState ) {
				case LIBRARY_STATE_ARTIST_LIST:
					fragment = ArtistListFragment.newInstance();
					break;

				case LIBRARY_STATE_ARTIST:
					fragment = ArtistFragment.newInstance( mCurrentArtist );
					break;

				case LIBRARY_STATE_ALBUM:
					fragment = AlbumFragment.newInstance( mCurrentAlbum );
					break;
			}
		}
		else {
			fragment = ArtistListFragment.newInstance();
		}

		if( fragment != null ) {
			mFragmentManager.beginTransaction().replace( R.id.LibraryShellFrame, fragment, "artistListFragment" ).commit();
		}

		return( myView );
	}

	@Override
	public void onResume() {
		super.onResume();

		EventBus.getDefault().register( this );
	}

	@Override
	public void onPause() {
		super.onPause();

		EventBus.getDefault().unregister( this );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putInt( LIBRARY_STATE, mCurrentState );
		outState.putLong( LIBRARY_CURRENT_ARTIST, mCurrentArtist );
		outState.putLong( LIBRARY_CURRENT_ALBUM, mCurrentAlbum );
	}

	public void onEvent( EventArtistSelected args ) {
		Artist artist = args.getArtist();

		if( artist != null ) {
			getApplicationState().setCurrentArtist( artist );
			mCurrentArtist = artist.ArtistId;

			ArtistFragment      fragment = ArtistFragment.newInstance( mCurrentArtist );
			FragmentTransaction transaction = mFragmentManager.beginTransaction().replace( R.id.LibraryShellFrame, fragment );

			transaction.addToBackStack( "artistFragment" );
			transaction.commit();
		}
	}

	public void onEvent( EventAlbumSelected args ) {
		Album   album = args.getAlbum();

		if( album != null ) {
			getApplicationState().setCurrentAlbum( album );
			mCurrentAlbum = album.AlbumId;

			AlbumFragment       fragment = AlbumFragment.newInstance( mCurrentAlbum );
			FragmentTransaction transaction = mFragmentManager.beginTransaction().replace( R.id.LibraryShellFrame, fragment );

			transaction.addToBackStack( "albumFragment" );
			transaction.commit();
		}
	}

	private IApplicationState getApplicationState() {
		NoiseRemoteApplication application = (NoiseRemoteApplication)getActivity().getApplication();

		return( application.getApplicationState());
	}
}
