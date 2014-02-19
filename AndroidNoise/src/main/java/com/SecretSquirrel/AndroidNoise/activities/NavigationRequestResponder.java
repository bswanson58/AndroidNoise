package com.SecretSquirrel.AndroidNoise.activities;

import android.os.Bundle;

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.dto.LibraryFocusArgs;
import com.SecretSquirrel.AndroidNoise.events.EventAlbumNameRequest;
import com.SecretSquirrel.AndroidNoise.events.EventAlbumRequest;
import com.SecretSquirrel.AndroidNoise.events.EventArtistListRequest;
import com.SecretSquirrel.AndroidNoise.events.EventArtistRequest;
import com.SecretSquirrel.AndroidNoise.events.EventNavigationUpEnable;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.services.ArtistAlbumResolver;
import com.SecretSquirrel.AndroidNoise.services.ArtistResolver;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

// Created by BSwanson on 2/8/14.

public class NavigationRequestResponder {
	private final EventBus              mEventBus;
	private final INoiseData            mNoiseData;
	private NavigationRequestListener   mRequestListener;

	public interface NavigationRequestListener {
		void    navigateRequest( int id, LibraryFocusArgs args );
		void    enableActionUp();
	}

	@Inject
	public NavigationRequestResponder( EventBus eventBus, INoiseData noiseData ) {
		mEventBus = eventBus;
		mNoiseData = noiseData;
	}

	public void setListener( NavigationRequestListener listener ) {
		mRequestListener = listener;

		if( mRequestListener != null ) {
			mEventBus.register( this );
		}
		else {
			mEventBus.unregister( this );
		}
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventNavigationUpEnable args ) {
		if( mRequestListener != null ) {
			mRequestListener.enableActionUp();
		}
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventServerSelected args ) {
		notifyListener( ShellActivity.LIBRARY_ITEM_ID, null );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventArtistListRequest args ) {
		notifyListener( ShellActivity.LIBRARY_ITEM_ID, null );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventArtistRequest args ) {
		ArtistResolver resolver = new ArtistResolver( mNoiseData );

		resolver.requestArtist( args.getArtistId(), new ServiceResultReceiver.Receiver() {
			@Override
			public void onReceiveResult( int resultCode, Bundle resultData ) {
				Artist artist = resultData.getParcelable( NoiseRemoteApi.Artist );

				notifyListener( ShellActivity.LIBRARY_ITEM_ID, new LibraryFocusArgs( artist ));
			}
		} );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventAlbumRequest args ) {
		ArtistAlbumResolver resolver = new ArtistAlbumResolver( mNoiseData );

		resolver.requestArtistAlbum( args.getArtistId(), args.getAlbumId(), new ServiceResultReceiver.Receiver() {
			@Override
			public void onReceiveResult( int resultCode, Bundle resultData ) {
				if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
					requestLibraryFocus( resultData );
				}
			}
		});
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventAlbumNameRequest args ) {
		ArtistAlbumResolver resolver = new ArtistAlbumResolver( mNoiseData );

		resolver.requestArtistAlbum( args.getArtistName(), args.getAlbumName(), new ServiceResultReceiver.Receiver() {
			@Override
			public void onReceiveResult( int resultCode, Bundle resultData ) {
				if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
					requestLibraryFocus( resultData );
				}
			}
		} );
	}

	private void requestLibraryFocus( Bundle args ) {
		Artist  artist = args.getParcelable( NoiseRemoteApi.Artist );
		Album album = args.getParcelable( NoiseRemoteApi.Album );

		notifyListener( ShellActivity.LIBRARY_ITEM_ID, new LibraryFocusArgs( artist, album ) );
	}

	private void notifyListener( int id, LibraryFocusArgs args ) {
		if( mRequestListener != null ) {
			mRequestListener.navigateRequest( id, args );
		}
	}
}
