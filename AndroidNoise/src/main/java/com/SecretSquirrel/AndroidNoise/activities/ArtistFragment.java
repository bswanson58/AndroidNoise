package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.events.EventArtistTrackAlbumRequest;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class ArtistFragment extends Fragment {
	private static final int        DETAIL_ALBUM_LIST = 1;
	private static final int        DETAIL_TRACK_LIST = 2;

	private static final String     ARTIST_KEY = "ArtistFragment_ArtistId";
	private static final String     EXTERNAL_REQUEST = "ArtistFragment_ExternalRequest";
	private static final String     DETAIL_FRAGMENT = "ArtistFragment_DetailFragment";

	private Artist                  mCurrentArtist;
	private boolean                 mIsExternalRequest;
	private int                     mDetailFragment;

	@Inject	EventBus                mEventBus;

	@InjectView( R.id.as_frame_album_list ) View    mUnderlay;
	@InjectView( R.id.as_frame_overlay )    View    mOverlay;

	public static ArtistFragment newInstance( Artist artist, boolean externalRequest ) {
		ArtistFragment  fragment = new ArtistFragment();
		Bundle          args = new Bundle();

		args.putParcelable( ARTIST_KEY, artist );
		args.putBoolean( EXTERNAL_REQUEST, externalRequest );
		fragment.setArguments( args );

		return( fragment );
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		setHasOptionsMenu( true );

		if( savedInstanceState != null ) {
			mCurrentArtist = savedInstanceState.getParcelable( ARTIST_KEY );
			mIsExternalRequest =savedInstanceState.getBoolean( EXTERNAL_REQUEST );
			mDetailFragment = savedInstanceState.getInt( DETAIL_FRAGMENT );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				mCurrentArtist = args.getParcelable( ARTIST_KEY );
				mIsExternalRequest = args.getBoolean( EXTERNAL_REQUEST );

				mDetailFragment = DETAIL_ALBUM_LIST;
			}
		}

		if( mCurrentArtist == null ) {
			Timber.e( "The current artist could not be determined." );
		}
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View                myView = inflater.inflate( R.layout.fragment_artist_shell, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );
		}

		if( mCurrentArtist != null ) {
  			if( getChildFragmentManager().findFragmentById( R.id.as_frame_artist_info ) == null ) {
				getChildFragmentManager()
						.beginTransaction()
						.replace( R.id.as_frame_artist_info, ArtistInfoFragment.newInstance( mCurrentArtist, mIsExternalRequest ))
						.replace( R.id.as_frame_album_list, createDetailFragment())
						.commit();
			}
		}

		return( myView );
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
	public void onDestroy() {
		super.onDestroy();

		ButterKnife.reset( this );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putParcelable( ARTIST_KEY, mCurrentArtist );
		outState.putBoolean( EXTERNAL_REQUEST, mIsExternalRequest );
		outState.putInt( DETAIL_FRAGMENT, mDetailFragment );
	}

	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		inflater.inflate( R.menu.artist, menu );

		super.onCreateOptionsMenu( menu, inflater );
	}

	@Override
	public void onPrepareOptionsMenu( Menu menu ) {
		MenuItem    item = menu.findItem( R.id.action_artist_detail );

		if( item != null ) {
			if( mDetailFragment == DETAIL_TRACK_LIST ) {
				item.setTitle( R.string.action_artist_detail_albums );
			}
			else {
				item.setTitle( R.string.action_artist_detail_tracks );
			}
		}

		super.onPrepareOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		boolean retValue = true;

		switch( item.getItemId()) {
			case R.id.action_artist_detail:
				swapDetailFragment();
				break;

			default:
				retValue = super.onOptionsItemSelected( item );
				break;
		}

		return( retValue );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventArtistTrackAlbumRequest args ) {
		if( args.getDisplayView()) {
			Fragment    fragment = ArtistTracksAlbumsFragment.newInstance( args.getArtist(), args.getTrack());

			getChildFragmentManager()
					.beginTransaction()
					.replace( R.id.as_frame_overlay, fragment )
					.commit();

			mOverlay.setVisibility( View.VISIBLE );
			mUnderlay.setVisibility( View.INVISIBLE );
		}
		else {
			mOverlay.setVisibility( View.INVISIBLE );
			mUnderlay.setVisibility( View.VISIBLE );
		}
	}

	private void swapDetailFragment() {
		if( mDetailFragment == DETAIL_ALBUM_LIST ) {
			mDetailFragment = DETAIL_TRACK_LIST;
		}
		else {
			mDetailFragment = DETAIL_ALBUM_LIST;
		}

		getChildFragmentManager()
				.beginTransaction()
				.replace( R.id.as_frame_album_list, createDetailFragment())
				.commit();
	}

	private Fragment createDetailFragment() {
		Fragment    retValue;

		if( mDetailFragment == DETAIL_ALBUM_LIST ) {
			retValue = AlbumListFragment.newInstance( mCurrentArtist.getArtistId());
		}
		else {
			retValue = ArtistTracksFragment.newInstance( mCurrentArtist );
		}

		return( retValue );
	}
}
