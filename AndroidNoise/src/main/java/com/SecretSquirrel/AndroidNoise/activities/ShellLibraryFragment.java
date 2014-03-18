package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.dto.ArtistInfo;
import com.SecretSquirrel.AndroidNoise.dto.LibraryFocusArgs;
import com.SecretSquirrel.AndroidNoise.events.EventAlbumSelected;
import com.SecretSquirrel.AndroidNoise.events.EventArtistInfoRequest;
import com.SecretSquirrel.AndroidNoise.events.EventArtistSelected;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class ShellLibraryFragment extends BaseShellFragment {
	private static final String LIBRARY_STATE               = "ShellLibraryFragment_LibraryState";
	private static final int    LIBRARY_STATE_NONE          = 0;
	private static final int    LIBRARY_STATE_ARTIST_LIST   = 1;
	private static final int    LIBRARY_STATE_ARTIST        = 2;
	private static final int    LIBRARY_STATE_ARTIST_INFO   = 3;
	private static final int    LIBRARY_STATE_ALBUM         = 4;

	private static final String LIBRARY_CURRENT_ARTIST      = "ShellLibraryFragment_CurrentArtist";
	private static final String LIBRARY_CURRENT_ARTIST_INFO = "ShellLibraryFragment_CurrentArtistInfo";
	private static final String LIBRARY_CURRENT_ALBUM       = "ShellLibraryFragment_CurrentAlbum";
	private static final String LIBRARY_REQUEST_ARTIST      = "ShellLibraryFragment_RequestArtist";
	private static final String LIBRARY_REQUEST_ALBUM       = "ShellLibraryFragment_RequestAlbum";

	private int                 mCurrentState;
	private int                 mFragmentToCreate;
	private boolean             mExternalRequest;
	private Artist              mCurrentArtist;
	private ArtistInfo          mCurrentArtistInfo;
	private Album               mCurrentAlbum;

	@Inject EventBus            mEventBus;

	public static ShellLibraryFragment newInstance( int fragmentId, LibraryFocusArgs focusArgs ) {
		ShellLibraryFragment    fragment = new ShellLibraryFragment();
		Bundle                  args = new Bundle();

		args.putInt( SHELL_FRAGMENT_KEY, fragmentId );
		if( focusArgs != null ) {
			args.putParcelable( LIBRARY_REQUEST_ARTIST, focusArgs.getArtist());
			if( focusArgs.getIsAlbumFocusRequest()) {
				args.putParcelable( LIBRARY_REQUEST_ALBUM, focusArgs.getAlbum());
			}
		}

		fragment.setArguments( args );

		return( fragment );
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		mCurrentState = LIBRARY_STATE_ARTIST_LIST;
		mCurrentArtist = null;
		mCurrentAlbum = null;

		if( savedInstanceState != null ) {
			mCurrentState = savedInstanceState.getInt( LIBRARY_STATE, LIBRARY_STATE_ARTIST_LIST );
			mCurrentArtist = savedInstanceState.getParcelable( LIBRARY_CURRENT_ARTIST );
			mCurrentArtistInfo = savedInstanceState.getParcelable( LIBRARY_CURRENT_ARTIST_INFO );
			mCurrentAlbum = savedInstanceState.getParcelable( LIBRARY_CURRENT_ALBUM );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				mCurrentArtist = args.getParcelable( LIBRARY_REQUEST_ARTIST );
				mCurrentAlbum = args.getParcelable( LIBRARY_REQUEST_ALBUM );

				if( mCurrentArtist != null ) {
					mExternalRequest = true;

					mCurrentState = mCurrentAlbum != null ? LIBRARY_STATE_ALBUM : LIBRARY_STATE_ARTIST;
				}
			}
		}

		// Only create the child fragment if we are being created.
		mFragmentToCreate = mCurrentState;
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		if( getChildFragmentManager().findFragmentById( R.id.LibraryShellFrame ) == null ) {
			Fragment    fragment = null;

			switch( mFragmentToCreate ) {
				case LIBRARY_STATE_ARTIST_LIST:
					fragment = ArtistListFragment.newInstance();
					break;

				case LIBRARY_STATE_ARTIST:
					fragment = ArtistFragment.newInstance( mCurrentArtist, mExternalRequest );
					break;

				case LIBRARY_STATE_ARTIST_INFO:
					fragment = ArtistExtendedInfoFragment.newInstance( mCurrentArtist, mCurrentArtistInfo );
					break;

				case LIBRARY_STATE_ALBUM:
					fragment = AlbumPagerFragment.newInstance( mCurrentArtist, mCurrentAlbum, mExternalRequest );
					break;
			}

			if( fragment != null ) {
				getChildFragmentManager()
						.beginTransaction()
						.replace( R.id.LibraryShellFrame, fragment )
						.commit();
			}
		}

		mFragmentToCreate = LIBRARY_STATE_NONE;

		return( inflater.inflate( R.layout.fragment_library_shell, container, false ));
	}

	@Override
	public void onResume() {
		super.onResume();

		mEventBus.register( this );
	}

	@Override
	public void onPause() {
		super.onPause();

		mEventBus.unregister( this );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putInt( SHELL_FRAGMENT_KEY, getFragmentId());

		outState.putInt( LIBRARY_STATE, mCurrentState );
		outState.putParcelable( LIBRARY_CURRENT_ARTIST, mCurrentArtist );
		outState.putParcelable( LIBRARY_CURRENT_ARTIST_INFO, mCurrentArtistInfo );
		outState.putParcelable( LIBRARY_CURRENT_ALBUM, mCurrentAlbum );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventArtistSelected args ) {
		Artist artist = args.getArtist();

		if( artist != null ) {
			mCurrentArtist = artist;

			displayCurrentArtist();
		}
	}

	private void displayCurrentArtist() {
		if( mCurrentArtist != null ) {
			mCurrentState = LIBRARY_STATE_ARTIST;

			getChildFragmentManager()
					.beginTransaction()
					.setCustomAnimations( android.R.anim.fade_in, android.R.anim.fade_out )
					.replace( R.id.LibraryShellFrame, ArtistFragment.newInstance( mCurrentArtist, false ))
					.addToBackStack( null )
					.commit();

			ActivityCompat.invalidateOptionsMenu( getActivity());
		}
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventAlbumSelected args ) {
		Album   album = args.getAlbum();

		if( album != null ) {
			mCurrentAlbum = album;

			displayCurrentAlbum();
		}
	}

	private void displayCurrentAlbum() {
		if(( mCurrentArtist != null ) &&
		   ( mCurrentAlbum != null )) {
			mCurrentState = LIBRARY_STATE_ALBUM;

			getChildFragmentManager()
					.beginTransaction()
					.setCustomAnimations( android.R.anim.fade_in, android.R.anim.fade_out )
					.replace( R.id.LibraryShellFrame, AlbumPagerFragment.newInstance( mCurrentArtist, mCurrentAlbum, false ))
					.addToBackStack( null )
					.commit();

			ActivityCompat.invalidateOptionsMenu( getActivity());
		}
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventArtistInfoRequest args ) {
		mCurrentArtist = args.getArtist();
		mCurrentArtistInfo = args.getArtistInfo();

		if(( mCurrentArtist != null ) &&
		   ( mCurrentArtistInfo != null )) {
			mCurrentState = LIBRARY_STATE_ARTIST_INFO;

			getChildFragmentManager()
					.beginTransaction()
					.setCustomAnimations( android.R.anim.fade_in, android.R.anim.fade_out )
					.replace( R.id.LibraryShellFrame, ArtistExtendedInfoFragment.newInstance( mCurrentArtist, mCurrentArtistInfo ))
					.addToBackStack( null )
					.commit();

			ActivityCompat.invalidateOptionsMenu( getActivity());
		}
	}
}
