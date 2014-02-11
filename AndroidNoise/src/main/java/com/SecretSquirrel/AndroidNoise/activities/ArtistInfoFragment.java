package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;
import com.SecretSquirrel.AndroidNoise.support.Constants;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class ArtistInfoFragment extends Fragment
								implements ServiceResultReceiver.Receiver {
	private static final String     TAG              = ArtistInfoFragment.class.getName();
	private static final String     ARTIST_KEY       = "ArtistInfoFragment_Artist";
	private static final String     ARTIST_INFO_KEY  = "ArtistInfoFragment_ArtistInfo";
	private static final String     EXTERNAL_REQUEST = "ArtistInfoFragment_ExternalRequest";

	private ServiceResultReceiver   mServiceResultReceiver;
	private Artist                  mArtist;
	private ArtistInfo              mArtistInfo;
	private ImageView               mArtistImage;
	private TextView                mArtistName;
	private TextView                mArtistGenre;
	private TextView                mAlbumCount;
	private Button                  mMoreButton;
	private Bitmap                  mUnknownArtist;
	private boolean                 mIsExternalRequest;

	@Inject EventBus                mEventBus;
	@Inject	INoiseData              mNoiseData;

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

		mServiceResultReceiver = new ServiceResultReceiver( new Handler());
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
			if( Constants.LOG_ERROR ) {
				Log.e( TAG, "Artist is null." );
			}
		}
		else {
			mEventBus.post( new EventArtistViewed( mArtist ) );
		}
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View myView = inflater.inflate( R.layout.fragment_artist_info, container, false );

		if( myView != null ) {
			mArtistImage = (ImageView)myView.findViewById( R.id.ai_artist_image );
			mArtistName = (TextView)myView.findViewById( R.id.ai_artist_name );
			mArtistGenre = (TextView)myView.findViewById( R.id.ai_artist_genre );
			mAlbumCount = (TextView)myView.findViewById( R.id.ai_album_count );

			mMoreButton = (Button)myView.findViewById( R.id.ai_artist_more );
			mMoreButton.setOnClickListener( new View.OnClickListener() {
				@Override
				public void onClick( View view ) {
					if(( mArtist != null ) &&
					   ( mArtistInfo != null )) {
						mEventBus.post( new EventArtistInfoRequest( mArtist, mArtistInfo ) );
					}
				}
			} );
		}

		updateDisplay( false );

		return( myView );
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			mArtistInfo = resultData.getParcelable( NoiseRemoteApi.ArtistInfo );
		}

		updateDisplay( true );
	}

	@Override
	public void onResume() {
		super.onResume();

		if( mArtistInfo == null ) {
			mServiceResultReceiver.setReceiver( this );

			retrieveArtistInfo();
		}

		mEventBus.post( new EventNavigationUpEnable());
	}

	@Override
	public void onPause() {
		super.onPause();

		mServiceResultReceiver.clearReceiver();
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		boolean retValue = true;

		switch( item.getItemId()) {
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
			mAlbumCount.setText( String.format( "%d", mArtist.getAlbumCount() ) );
		}
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
}
