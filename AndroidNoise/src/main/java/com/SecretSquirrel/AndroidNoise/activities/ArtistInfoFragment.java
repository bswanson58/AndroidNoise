package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by BSwanson on 12/17/13.

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.dto.ArtistInfo;
import com.SecretSquirrel.AndroidNoise.events.EventArtistInfoRequest;
import com.SecretSquirrel.AndroidNoise.events.EventArtistListRequest;
import com.SecretSquirrel.AndroidNoise.events.EventArtistViewed;
import com.SecretSquirrel.AndroidNoise.events.EventNavigationUpEnable;
import com.SecretSquirrel.AndroidNoise.events.EventPlayTrackList;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class ArtistInfoFragment extends Fragment
								implements ServiceResultReceiver.Receiver {
	private static final String     ARTIST_KEY       = "ArtistInfoFragment_Artist";
	private static final String     ARTIST_INFO_KEY  = "ArtistInfoFragment_ArtistInfo";
	private static final String     EXTERNAL_REQUEST = "ArtistInfoFragment_ExternalRequest";

	private Artist                  mArtist;
	private ArtistInfo              mArtistInfo;
	private boolean                 mIsExternalRequest;
	private Bitmap                  mUnknownArtist;

	@Inject EventBus                mEventBus;
	@Inject	INoiseData              mNoiseData;
	@Inject ServiceResultReceiver   mServiceResultReceiver;

	@InjectView( R.id.ai_artist_image ) ImageView   mArtistImage;
	@InjectView( R.id.ai_artist_name )  TextView    mArtistName;
	@InjectView( R.id.ai_artist_genre ) TextView    mArtistGenre;
	@InjectView( R.id.ai_album_count )  TextView    mAlbumCount;
	@InjectView( R.id.ai_artist_more )  Button      mMoreButton;

	public static ArtistInfoFragment newInstance( Artist artist, boolean isExternalRequest ) {
		ArtistInfoFragment  fragment = new ArtistInfoFragment();
		Bundle              args = new Bundle();

		args.putParcelable( ARTIST_KEY, artist );
		args.putBoolean( EXTERNAL_REQUEST, isExternalRequest );

		fragment.setArguments( args );

		return( fragment );
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		setHasOptionsMenu( true );

		mUnknownArtist = BitmapFactory.decodeResource( getResources(), R.drawable.unknown_artist );

		if( savedInstanceState != null ) {
			mArtist = savedInstanceState.getParcelable( ARTIST_KEY );
			mArtistInfo = savedInstanceState.getParcelable( ARTIST_INFO_KEY );
			mIsExternalRequest = savedInstanceState.getBoolean( EXTERNAL_REQUEST );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				mArtist = args.getParcelable( ARTIST_KEY );
				mIsExternalRequest = args.getBoolean( EXTERNAL_REQUEST );
			}
		}

		if( mArtist == null ) {
			Timber.e( "Artist is null." );
		}
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View myView = inflater.inflate( R.layout.fragment_artist_info, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );

			updateDisplay( false );
		}

		return( myView );
	}

	@SuppressWarnings( "unused" )
	@OnClick( R.id.ai_artist_more )
	public void onInfoClick() {
		if(( mArtist != null ) &&
		   ( mArtistInfo != null )) {
			mEventBus.post( new EventArtistInfoRequest( mArtist, mArtistInfo ) );
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if( mArtistInfo == null ) {
			mServiceResultReceiver.setReceiver( this );

			retrieveArtistInfo();
		}

		mEventBus.post( new EventNavigationUpEnable());
		mEventBus.post( new EventArtistViewed( mArtist ));
	}

	@Override
	public void onPause() {
		super.onPause();

		mServiceResultReceiver.clearReceiver();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		ButterKnife.reset( this );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putParcelable( ARTIST_KEY, mArtist );
		if( mArtistInfo != null ) {
			outState.putParcelable( ARTIST_INFO_KEY, mArtistInfo );
		}
		outState.putBoolean( EXTERNAL_REQUEST, mIsExternalRequest );
	}

	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		inflater.inflate( R.menu.artist_info, menu );

		super.onCreateOptionsMenu( menu, inflater );
	}

	@Override
	public void onPrepareOptionsMenu( Menu menu ) {
		MenuItem    item = menu.findItem( R.id.action_play_artist_top_tracks );

		if( item != null ) {
			item.setEnabled(( mArtistInfo != null ) && ( mArtistInfo.getTopTrackIds().length > 0 ));
		}

		super.onPrepareOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		boolean retValue = true;

		switch( item.getItemId()) {
			case R.id.action_play_artist_top_tracks:
				if(( mArtistInfo != null ) &&
				   ( mArtistInfo.getTopTrackIds().length > 0 )) {
					mEventBus.post( new EventPlayTrackList( mArtistInfo.getTopTrackIds()));
				}
				break;

			case android.R.id.home:
				if( mIsExternalRequest ) {
					mEventBus.post( new EventArtistListRequest());
				}
				else {
					getActivity().onBackPressed();
				}
				break;

			default:
				retValue = super.onOptionsItemSelected( item );
				break;
		}

		return( retValue );
	}

	private void retrieveArtistInfo() {
		if( mArtist != null ) {
			mNoiseData.GetArtistInfo( mArtist.getArtistId(), mServiceResultReceiver );
		}
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			mArtistInfo = resultData.getParcelable( NoiseRemoteApi.ArtistInfo );
		}

		updateDisplay( true );

		ActivityCompat.invalidateOptionsMenu( getActivity() );
	}

	private void updateDisplay( boolean withDefaults ) {
		if( mArtistInfo != null ) {
			Bitmap  artistImage = mArtistInfo.getArtistImage();

			if( artistImage == null ) {
				artistImage = mUnknownArtist;
			}

			mArtistImage.setImageBitmap( artistImage );

			if(!TextUtils.isEmpty( mArtistInfo.getBiography())) {
				mMoreButton.setVisibility( View.VISIBLE );
			}
			else {
				mMoreButton.setVisibility( View.INVISIBLE );
			}
		}
		else {
			if( withDefaults ) {
				mArtistImage.setImageBitmap( mUnknownArtist );
			}

			mMoreButton.setVisibility( View.INVISIBLE );
		}

		if( mArtist != null ) {
			mArtistName.setText( mArtist.getName());
			mArtistGenre.setText( mArtist.getGenre());
			mAlbumCount.setText( String.format( "%d", mArtist.getAlbumCount()));
		}
	}
}
