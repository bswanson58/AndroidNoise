package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/18/13.

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.AlbumInfo;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.events.EventArtistRequest;
import com.SecretSquirrel.AndroidNoise.events.EventArtistSelected;
import com.SecretSquirrel.AndroidNoise.events.EventArtistViewed;
import com.SecretSquirrel.AndroidNoise.events.EventNavigationUpEnable;
import com.SecretSquirrel.AndroidNoise.events.EventPlayAlbum;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;
import com.SecretSquirrel.AndroidNoise.support.Constants;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;
import com.SecretSquirrel.AndroidNoise.support.NoiseUtils;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class AlbumInfoFragment extends Fragment
							   implements ServiceResultReceiver.Receiver {
	private static final String     TAG              = AlbumInfoFragment.class.getName();
	private static final String     ARTIST_KEY       = "AlbumInfoFragment_Artist";
	private static final String     ALBUM_KEY        = "AlbumInfoFragment_Album";
	private static final String     ALBUM_INFO_KEY   = "AlbumInfoFragment_AlbumInfo";
	private static final String     EXTERNAL_REQUEST = "AlbumInfoFragment_ExternalRequest";

	private Artist                  mArtist;
	private Album                   mAlbum;
	private AlbumInfo               mAlbumInfo;
	private Bitmap                  mUnknownAlbum;
	private boolean                 mIsExternalRequest;

	@Inject EventBus                mEventBus;
	@Inject	INoiseData              mNoiseData;
	@Inject ServiceResultReceiver   mServiceResultReceiver;

	@InjectView( R.id.ai_album_cover_image )ImageView   mAlbumCover;
	@InjectView( R.id.ai_artist_name )	    TextView    mArtistName;
	@InjectView( R.id.ai_album_name )	    TextView    mAlbumName;
	@InjectView( R.id.ai_published )	    TextView    mPublishedYear;
	@InjectView( R.id.ai_published_header )	TextView    mPublishedYearHeader;
	@InjectView( R.id.ai_track_count )      TextView    mTrackCount;

	public static AlbumInfoFragment newInstance( Artist artist, Album album, boolean isExternalRequest ) {
		AlbumInfoFragment   fragment = new AlbumInfoFragment();
		Bundle              args = new Bundle();

		args.putParcelable( ARTIST_KEY, artist );
		args.putParcelable( ALBUM_KEY, album );
		args.putBoolean( EXTERNAL_REQUEST, isExternalRequest );

		fragment.setArguments( args );

		return( fragment );
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		setHasOptionsMenu( true );

		mUnknownAlbum = BitmapFactory.decodeResource( getResources(), R.drawable.unknown_album );

		if( savedInstanceState != null ) {
			mArtist = savedInstanceState.getParcelable( ARTIST_KEY );
			mAlbum = savedInstanceState.getParcelable( ALBUM_KEY );
			mAlbumInfo = savedInstanceState.getParcelable( ALBUM_INFO_KEY );
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

		if( mArtist == null ) {
			if( Constants.LOG_ERROR ) {
				Log.e( TAG, "The current artist could not be determined." );
			}
		}

		if( mAlbum == null ) {
			if( Constants.LOG_ERROR ) {
				Log.e( TAG, "The current album could not be determined." );
			}
		}
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View myView = inflater.inflate( R.layout.fragment_album_info, container, false );

		ButterKnife.inject( this, myView );

		updateDisplay( false );

		return( myView );
	}

	@Override
	public void onResume() {
		super.onResume();

		if( mArtist != null ) {
			mEventBus.post( new EventArtistViewed( mArtist ));
		}

		if(( mAlbum != null ) &&
		   ( mAlbumInfo == null )) {
			mServiceResultReceiver.setReceiver( this );

			mNoiseData.GetAlbumInfo( mAlbum.getAlbumId(), mServiceResultReceiver );
		}

		mEventBus.post( new EventNavigationUpEnable());
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
		outState.putParcelable( ALBUM_KEY, mAlbum );
		if( mAlbumInfo != null ) {
			outState.putParcelable( ALBUM_INFO_KEY, mAlbumInfo );
		}
	}

	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		inflater.inflate( R.menu.album_info, menu );

		super.onCreateOptionsMenu( menu, inflater );
	}

	@Override
	public void onPrepareOptionsMenu( Menu menu ) {
		MenuItem    item = menu.findItem( R.id.action_play_album );

		if( item != null ) {
			item.setEnabled( mAlbum != null );
		}

		super.onPrepareOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		boolean retValue = true;

		switch( item.getItemId()) {
			case R.id.action_play_album:
				if( mAlbum != null ) {
					EventBus.getDefault().post( new EventPlayAlbum( mAlbum ));
				}
				break;

			case android.R.id.home:
				if( mIsExternalRequest ) {
					mEventBus.post( new EventArtistRequest( mArtist.getArtistId()));
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

	@SuppressWarnings( "unused" )
	@OnClick( R.id.ai_artist_name )
	public void onArtistNameClick() {
		if( mArtist != null ) {
			EventBus.getDefault().post( new EventArtistSelected( mArtist ));
		}
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			mAlbumInfo = resultData.getParcelable( NoiseRemoteApi.AlbumInfo );
		}

		updateDisplay( true );
	}

	private void updateDisplay( boolean withDefaults ) {
		if( mArtist != null ) {
			mArtistName.setText( mArtist.getName());
		}

		if( mAlbum != null ) {
			mAlbumName.setText( mAlbum.getName());
			mTrackCount.setText( String.format( "%d", mAlbum.getTrackCount()));

			if( mAlbum.getHasPublishedYear()) {
				mPublishedYear.setText( NoiseUtils.FormatPublishedYear( getActivity(), mAlbum.getPublishedYear()));
				mPublishedYear.setVisibility( View.VISIBLE );
				mPublishedYearHeader.setVisibility( View.VISIBLE );
			}
			else {
				mPublishedYear.setVisibility( View.INVISIBLE );
				mPublishedYearHeader.setVisibility( View.INVISIBLE );
			}
		}

		if( mAlbumInfo != null ) {
			Bitmap  albumImage = mAlbumInfo.getAlbumCover();

			if( albumImage == null ) {
				albumImage = mUnknownAlbum;
			}

			mAlbumCover.setImageBitmap( albumImage );
		}
		else {
			if( withDefaults ) {
				mAlbumCover.setImageBitmap( mUnknownAlbum );
			}
		}
	}
}
