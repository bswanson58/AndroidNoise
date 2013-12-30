package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/18/13.

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.AlbumInfo;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;
import com.SecretSquirrel.AndroidNoise.support.Constants;

public class AlbumInfoFragment extends Fragment
							   implements ServiceResultReceiver.Receiver {
	private static final String     TAG = AlbumInfoFragment.class.getName();
	private static final String     ALBUM_KEY = "AlbumInfoFragment_AlbumId";

	private ServiceResultReceiver   mServiceResultReceiver;
	private long                    mCurrentAlbum;
	private ImageView               mAlbumCover;

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

		mAlbumCover = (ImageView) myView.findViewById( R.id.album_cover_image );

		if( mCurrentAlbum != Constants.NULL_ID ) {
			if( getApplicationState().getIsConnected()) {
				getApplicationState().getDataClient().GetAlbumInfo( mCurrentAlbum, mServiceResultReceiver );
			}
		}
		else {
			if( Constants.LOG_ERROR ) {
				Log.e( TAG, "The current album could not be determined." );
			}
		}

		return( myView );
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			AlbumInfo albumInfo = resultData.getParcelable( NoiseRemoteApi.AlbumInfo );

			setAlbumInfo( albumInfo );
		}
	}

	private void setAlbumInfo( AlbumInfo albumInfo ) {
		mAlbumCover.setImageBitmap( albumInfo.getAlbumCover());
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putLong( ALBUM_KEY, mCurrentAlbum );
	}

	private IApplicationState getApplicationState() {
		NoiseRemoteApplication application = (NoiseRemoteApplication)getActivity().getApplication();

		return( application.getApplicationState());
	}
}
