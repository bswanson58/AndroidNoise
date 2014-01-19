package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.dto.ArtistInfo;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;
import com.SecretSquirrel.AndroidNoise.support.Constants;

public class ArtistInfoFragment extends Fragment
								implements ServiceResultReceiver.Receiver {
	private static final String     TAG             = ArtistInfoFragment.class.getName();
	private static final String     ARTIST_KEY      = "ArtistInfoFragment_Artist";
	private static final String     ARTIST_INFO_KEY = "ArtistInfoFragment_ArtistInfo";

	private ServiceResultReceiver   mServiceResultReceiver;
	private Artist                  mArtist;
	private ArtistInfo              mArtistInfo;
	private ImageView               mArtistImage;
	private TextView                mArtistName;
	private TextView                mArtistGenre;
	private Bitmap                  mUnknownArtist;

	public static ArtistInfoFragment newInstance( Artist artist ) {
		ArtistInfoFragment  fragment = new ArtistInfoFragment();
		Bundle              args = new Bundle();

		args.putParcelable( ARTIST_KEY, artist );
		fragment.setArguments( args );

		return( fragment );
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mServiceResultReceiver = new ServiceResultReceiver( new Handler());
		mServiceResultReceiver.setReceiver( this );

		mUnknownArtist = BitmapFactory.decodeResource( getResources(), R.drawable.unknown_artist );	}

	@Override
	public void onPause() {
		super.onPause();

		mServiceResultReceiver.clearReceiver();
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View myView = inflater.inflate( R.layout.fragment_artist_info, container, false );

		if( savedInstanceState != null ) {
			mArtist = savedInstanceState.getParcelable( ARTIST_KEY );
			mArtistInfo = savedInstanceState.getParcelable( ARTIST_INFO_KEY );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				mArtist = args.getParcelable( ARTIST_KEY );
			}
		}

		if( myView != null ) {
			mArtistImage = (ImageView)myView.findViewById( R.id.ai_artist_image );
			mArtistName = (TextView)myView.findViewById( R.id.ai_artist_name );
			mArtistGenre = (TextView)myView.findViewById( R.id.ai_artist_genre );
		}

		if( mArtist != null ) {
			if( mArtistInfo == null ) {
				if( getApplicationState().getIsConnected()) {
					getApplicationState().getDataClient().GetArtistInfo( mArtist.getArtistId(), mServiceResultReceiver );
				}
			}
		}
		else {
			if( Constants.LOG_ERROR ) {
				Log.e( TAG, "The current artist could not be determined." );
			}
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

	private void updateDisplay( boolean withDefaults ) {
		if( mArtistInfo != null ) {
			Bitmap  artistImage = mArtistInfo.getArtistImage();

			if( artistImage == null ) {
				artistImage = mUnknownArtist;
			}

			mArtistImage.setImageBitmap( artistImage );
		}
		else {
			if( withDefaults ) {
				mArtistImage.setImageBitmap( mUnknownArtist );
			}
		}

		if( mArtist != null ) {
			mArtistName.setText( mArtist.getName());
			mArtistGenre.setText( mArtist.getGenre());
		}
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putParcelable( ARTIST_KEY, mArtist );
		if( mArtistInfo != null ) {
			outState.putParcelable( ARTIST_INFO_KEY, mArtistInfo );
		}
	}

	private IApplicationState getApplicationState() {
		NoiseRemoteApplication application = (NoiseRemoteApplication)getActivity().getApplication();

		return( application.getApplicationState());
	}
}
