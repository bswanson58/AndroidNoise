package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.ArtistInfo;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;
import com.SecretSquirrel.AndroidNoise.support.Constants;

public class ArtistInfoFragment extends Fragment
								implements ServiceResultReceiver.Receiver {
	private static final String     TAG = ArtistInfoFragment.class.getName();
	private static final String     ARTIST_KEY   = "ArtistInfoFragment_ArtistId";

	private ServiceResultReceiver   mServiceResultReceiver;
	private long                    mCurrentArtist;
	private ImageView               mArtistImage;

	public static ArtistInfoFragment newInstance( long artistId ) {
		ArtistInfoFragment  fragment = new ArtistInfoFragment();
		Bundle              args = new Bundle();

		args.putLong( ARTIST_KEY, artistId );
		fragment.setArguments( args );

		return( fragment );
	}

	public ArtistInfoFragment() {
		mCurrentArtist = Constants.NULL_ID;
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mServiceResultReceiver = new ServiceResultReceiver( new Handler());
		mServiceResultReceiver.setReceiver( this );
	}

	@Override
	public void onPause() {
		super.onPause();

		mServiceResultReceiver.clearReceiver();
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View myView = inflater.inflate( R.layout.fragment_artist_info, container, false );

		if( savedInstanceState != null ) {
			mCurrentArtist = savedInstanceState.getLong( ARTIST_KEY, Constants.NULL_ID );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				mCurrentArtist = args.getLong( ARTIST_KEY, Constants.NULL_ID );
			}
		}

		mArtistImage = (ImageView)myView.findViewById( R.id.artistImage );

		if( mCurrentArtist != Constants.NULL_ID ) {
			if( getApplicationState().getIsConnected()) {
				getApplicationState().getDataClient().GetArtistInfo( mCurrentArtist, mServiceResultReceiver );
			}
		}
		else {
			if( Constants.LOG_ERROR ) {
				Log.e( TAG, "The current artist could not be determined." );
			}
		}

		return( myView );
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			ArtistInfo  artistInfo = resultData.getParcelable( NoiseRemoteApi.ArtistInfo );

			setArtistInfo( artistInfo );
		}
	}

	private void setArtistInfo( ArtistInfo artistInfo ) {
		mArtistImage.setImageBitmap( artistInfo.getArtistImage());
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putLong( ARTIST_KEY, mCurrentArtist );
	}

	private IApplicationState getApplicationState() {
		NoiseRemoteApplication application = (NoiseRemoteApplication)getActivity().getApplication();

		return( application.getApplicationState());
	}
}
