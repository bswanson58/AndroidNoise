package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.QueuedAlbumResult;
import com.SecretSquirrel.AndroidNoise.dto.QueuedTrackResult;
import com.SecretSquirrel.AndroidNoise.dto.Track;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
import com.SecretSquirrel.AndroidNoise.services.rto.RemoteServerQueueApi;

import retrofit.RestAdapter;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.concurrency.AndroidSchedulers;
import rx.concurrency.Schedulers;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Action1;

public class NoiseQueueClient implements INoiseQueue {
	private String                  mServerAddress;
	private RemoteServerQueueApi    mService;

	public NoiseQueueClient( String serverAddress ) {
		mServerAddress = serverAddress;
	}

	@Override
	public void EnqueueTrack( Track track, Action1<QueuedTrackResult> resultAction ) {
		EnqueueTrack( track )
				.observeOn( AndroidSchedulers.mainThread())
				.subscribe( resultAction );
	}

	@Override
	public Observable<QueuedTrackResult> EnqueueTrack( final Track track ) {
		return Observable.create(new Observable.OnSubscribeFunc<QueuedTrackResult>() {
			@Override
			public Subscription onSubscribe( Observer<? super QueuedTrackResult> observer ) {
				try {
					observer.onNext( new QueuedTrackResult( track, getService().EnqueueTrack( track.TrackId )));
					observer.onCompleted();
				} catch( Exception e ) {
					observer.onError(e);
				}

				return Subscriptions.empty();
			}
		}).subscribeOn( Schedulers.threadPoolForIO());
	}

	@Override
	public void EnqueueAlbum( Album album, Action1<QueuedAlbumResult> resultAction ) {
		EnqueueAlbum( album )
				.observeOn( AndroidSchedulers.mainThread())
				.subscribe( resultAction );
	}

	@Override
	public Observable<QueuedAlbumResult> EnqueueAlbum( final Album album ) {
		return Observable.create(new Observable.OnSubscribeFunc<QueuedAlbumResult>() {
			@Override
			public Subscription onSubscribe( Observer<? super QueuedAlbumResult> observer) {
				try {
					observer.onNext( new QueuedAlbumResult( album, getService().EnqueueAlbum( album.AlbumId )));
					observer.onCompleted();
				} catch( Exception e ) {
					observer.onError(e);
				}

				return Subscriptions.empty();
			}
		}).subscribeOn( Schedulers.threadPoolForIO());
	}

	private RemoteServerQueueApi getService() {
		if( mService == null ) {
			mService = buildService( mServerAddress );
		}

		return( mService );
	}

	private RemoteServerQueueApi buildService( String serverAddress ) {
		RestAdapter restAdapter = new RestAdapter.Builder()
				.setServer( serverAddress )
				.build();

		return( restAdapter.create( RemoteServerQueueApi.class ));
	}
}
