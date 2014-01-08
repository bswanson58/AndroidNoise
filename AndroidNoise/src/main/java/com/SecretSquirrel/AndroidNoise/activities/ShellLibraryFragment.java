package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.events.EventAlbumSelected;
import com.SecretSquirrel.AndroidNoise.events.EventArtistSelected;

import de.greenrobot.event.EventBus;

public class ShellLibraryFragment extends BaseShellFragment {
	private static final String LIBRARY_STATE               = "ShellLibraryFragment_LibraryState";
	private static final int    LIBRARY_STATE_NONE          = 0;
	private static final int    LIBRARY_STATE_ARTIST_LIST   = 1;
	private static final int    LIBRARY_STATE_ARTIST        = 2;
	private static final int    LIBRARY_STATE_ALBUM         = 3;

	private static final String LIBRARY_CURRENT_ARTIST      = "ShellLibraryFragment_CurrentArtist";
	private static final String LIBRARY_CURRENT_ALBUM       = "ShellLibraryFragment_CurrentAlbum";

	private int                 mCurrentState;
	private int                 mFragmentToCreate;
	private Artist              mCurrentArtist;
	private Album               mCurrentAlbum;

	public static ShellLibraryFragment newInstance( int fragmentId ) {
		return( new ShellLibraryFragment( fragmentId ));
	}

	protected ShellLibraryFragment( int fragmentId ) {
		super( fragmentId );
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mCurrentState = LIBRARY_STATE_ARTIST_LIST;
		mCurrentArtist = null;
		mCurrentAlbum = null;

		if( savedInstanceState != null ) {
			mCurrentState = savedInstanceState.getInt( LIBRARY_STATE, LIBRARY_STATE_ARTIST_LIST );
			mCurrentArtist = savedInstanceState.getParcelable( LIBRARY_CURRENT_ARTIST );
			mCurrentAlbum = savedInstanceState.getParcelable( LIBRARY_CURRENT_ALBUM );
		}

		// Only create the child fragment if we are being created.
		mFragmentToCreate = mCurrentState;
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Fragment    fragment = null;

		switch( mFragmentToCreate ) {
			case LIBRARY_STATE_ARTIST_LIST:
				fragment = ArtistListFragment.newInstance();
				break;

			case LIBRARY_STATE_ARTIST:
				fragment = ArtistFragment.newInstance( mCurrentArtist );
				break;

			case LIBRARY_STATE_ALBUM:
				fragment = AlbumFragment.newInstance( mCurrentAlbum.AlbumId );
				break;
		}

		if( fragment != null ) {
			getChildFragmentManager()
					.beginTransaction()
					.replace( R.id.LibraryShellFrame, fragment )
					.commit();

			mFragmentToCreate = LIBRARY_STATE_NONE;
		}

		return( inflater.inflate( R.layout.fragment_library_shell, container, false ));
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
		// Do not save the current fragment - which the base class method will do.
		// super.onSaveInstanceState( outState );

		outState.putInt( LIBRARY_STATE, mCurrentState );
		outState.putParcelable( LIBRARY_CURRENT_ARTIST, mCurrentArtist );
		outState.putParcelable( LIBRARY_CURRENT_ALBUM, mCurrentAlbum );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventArtistSelected args ) {
		Artist artist = args.getArtist();

		if( artist != null ) {
			mCurrentState = LIBRARY_STATE_ARTIST;
			mCurrentArtist = artist;

			getChildFragmentManager()
					.beginTransaction()
					.setCustomAnimations( android.R.anim.fade_in, android.R.anim.fade_out )
					.replace( R.id.LibraryShellFrame, ArtistFragment.newInstance( mCurrentArtist ))
					.addToBackStack( null )
					.commit();
		}
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventAlbumSelected args ) {
		Album   album = args.getAlbum();

		if( album != null ) {
			mCurrentState = LIBRARY_STATE_ALBUM;
			mCurrentAlbum = album;

			getChildFragmentManager()
					.beginTransaction()
					.setCustomAnimations( android.R.anim.fade_in, android.R.anim.fade_out )
					.replace( R.id.LibraryShellFrame, AlbumFragment.newInstance( mCurrentAlbum.AlbumId ))
					.addToBackStack( null )
					.commit();
		}
	}
}
