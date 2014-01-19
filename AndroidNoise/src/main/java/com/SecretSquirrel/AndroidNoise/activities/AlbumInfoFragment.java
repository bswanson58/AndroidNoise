package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/18/13.

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
import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.AlbumInfo;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;
import com.SecretSquirrel.AndroidNoise.support.Constants;

public class AlbumInfoFragment extends Fragment
							   implements ServiceResultReceiver.Receiver {
	private static final String     TAG             = AlbumInfoFragment.class.getName();
	private static final String     ARTIST_KEY      = "AlbumInfoFragment_Artist";
	private static final String     ALBUM_KEY       = "AlbumInfoFragment_Album";
	private static final String     ALBUM_INFO_KEY  = "AlbumInfoFragment_AlbumInfo";

	private ServiceResultReceiver   mServiceResultReceiver;
	private Artist                  mArtist;
	private Album                   mAlbum;
	private AlbumInfo               mAlbumInfo;
	private ImageView               mAlbumCover;
	private TextView                mArtistName;
	private TextView                mAlbumName;
	private TextView                mPublishedYear;
	private TextView                mPublishedYearHeader;
	private Bitmap                  mUnknownAlbum;

	public static AlbumInfoFragment newInstance( Artist artist, Album album ) {
		AlbumInfoFragment   fragment = new AlbumInfoFragment();
		Bundle              args = new Bundle();

		args.putParcelable( ARTIST_KEY, artist );
		args.putParcelable( ALBUM_KEY, album );
		fragment.setArguments( args );

		return( fragment );
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mServiceResultReceiver = new ServiceResultReceiver( new Handler());
		mServiceResultReceiver.setReceiver( this );

		mUnknownAlbum = BitmapFactory.decodeResource( getResources(), R.drawable.unknown_album );
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
			mArtist = savedInstanceState.getParcelable( ARTIST_KEY );
			mAlbum = savedInstanceState.getParcelable( ALBUM_KEY );
			mAlbumInfo = savedInstanceState.getParcelable( ALBUM_INFO_KEY );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				mArtist = args.getParcelable( ARTIST_KEY );
				mAlbum = args.getParcelable( ALBUM_KEY );
			}
		}

		if( myView != null ) {
			mArtistName = (TextView)myView.findViewById( R.id.ai_artist_name );
			mAlbumName = (TextView)myView.findViewById( R.id.ai_album_name );
			mAlbumCover = (ImageView) myView.findViewById( R.id.ai_album_cover_image );
			mPublishedYear = (TextView)myView.findViewById( R.id.ai_published );
			mPublishedYearHeader = (TextView)myView.findViewById( R.id.ai_published_header );
		}

		if( mAlbum != null ) {
			if( mAlbumInfo == null ) {
				if( getApplicationState().getIsConnected()) {
					getApplicationState().getDataClient().GetAlbumInfo( mAlbum.getAlbumId(), mServiceResultReceiver );
				}
			}
		}
		else {
			if( Constants.LOG_ERROR ) {
				Log.e( TAG, "The current album could not be determined." );
			}
		}

		updateDisplay( false );

		return( myView );
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

			if( mAlbum.getPublishedYear() > 0 ) {
				mPublishedYear.setText( String.format( "%4d", mAlbum.getPublishedYear()));
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

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putParcelable( ARTIST_KEY, mArtist );
		outState.putParcelable( ALBUM_KEY, mAlbum );
		if( mAlbumInfo != null ) {
			outState.putParcelable( ALBUM_INFO_KEY, mAlbumInfo );
		}
	}

	private IApplicationState getApplicationState() {
		NoiseRemoteApplication application = (NoiseRemoteApplication)getActivity().getApplication();

		return( application.getApplicationState());
	}
}
