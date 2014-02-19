package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import android.util.Log;

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.PlayQueueListResult;
import com.SecretSquirrel.AndroidNoise.dto.QueuedAlbumResult;
import com.SecretSquirrel.AndroidNoise.dto.QueuedTrackResult;
import com.SecretSquirrel.AndroidNoise.dto.Track;
import com.SecretSquirrel.AndroidNoise.events.EventActivityPausing;
import com.SecretSquirrel.AndroidNoise.events.EventActivityResuming;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.RemoteServerQueueApi;
import com.SecretSquirrel.AndroidNoise.support.Constants;

import java.util.EnumMap;

import javax.inject.Inject;

import dagger.Lazy;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.concurrency.AndroidSchedulers;
import rx.concurrency.Schedulers;
import rx.subscriptions.Subscriptions;
import rx.util.functions.Action1;

public class NoiseQueueClient implements INoiseQueue {
	private static final String                         TAG = NoiseQueueClient.class.getName();

	private final EventBus                              mEventBus;
	private final Lazy<RemoteServerQueueApi>            mServiceProvider;
	public  final EnumMap<TransportCommand, Integer>    mTransportCommands;
	private RemoteServerQueueApi                        mService;

	@Inject
	public NoiseQueueClient( EventBus eventBus, Lazy<RemoteServerQueueApi> queueApi ) {
		mEventBus = eventBus;
		mServiceProvider = queueApi;

		mTransportCommands = new EnumMap<TransportCommand, Integer>( TransportCommand.class );

		mTransportCommands.put( TransportCommand.Play, 1 );
		mTransportCommands.put( TransportCommand.Stop, 2 );
		mTransportCommands.put( TransportCommand.Pause, 3 );
		mTransportCommands.put( TransportCommand.PlayNext, 4 );
		mTransportCommands.put( TransportCommand.PlayPrevious, 5 );
		mTransportCommands.put( TransportCommand.Repeat, 6 );

		mEventBus.register( this );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventServerSelected args ) {
		mService = null;
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventActivityPausing args ) {
		mEventBus.unregister( this );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventActivityResuming args ) {
		mEventBus.register( this );
	}

	private RemoteServerQueueApi getService() {
		if( mService == null ) {
			mService = mServiceProvider.get();
		}

		return( mService );
	}

	@Override
	public Subscription EnqueueTrack( Track track, Action1<QueuedTrackResult> resultAction ) {
		return( EnqueueTrack( track )
				.observeOn( AndroidSchedulers.mainThread())
				.subscribe( resultAction, new Action1<Throwable>() {
					@Override
					public void call( Throwable throwable ) {
					if( Constants.LOG_ERROR ) {
						Log.w( TAG, "Default EnqueueTrack exception handler", throwable );
					}
					}
				} ));
	}

	@Override
	public Subscription EnqueueTrack( Track track, Action1<QueuedTrackResult> resultAction, Action1<Throwable> errorAction  ) {
		return( EnqueueTrack( track )
				.observeOn( AndroidSchedulers.mainThread())
				.subscribe( resultAction, errorAction ));
	}

	@Override
	public Observable<QueuedTrackResult> EnqueueTrack( final Track track ) {
		return Observable.create( new Observable.OnSubscribeFunc<QueuedTrackResult>() {
			@Override
			public Subscription onSubscribe( Observer<? super QueuedTrackResult> observer ) {
				try {
					observer.onNext( new QueuedTrackResult( track, getService().EnqueueTrack( track.getTrackId())));
					observer.onCompleted();
				} catch( Exception e ) {
					observer.onError( e );
				}

				return Subscriptions.empty();
			}
		}).subscribeOn( Schedulers.threadPoolForIO());
	}

	@Override
	public Subscription EnqueueAlbum( Album album, Action1<QueuedAlbumResult> resultAction ) {
		return( EnqueueAlbum( album )
				.observeOn( AndroidSchedulers.mainThread())
				.subscribe( resultAction, new Action1<Throwable>() {
					@Override
					public void call( Throwable throwable ) {
					if( Constants.LOG_ERROR ) {
						Log.w( TAG, "Default EnqueueAlbum exception handler", throwable );
					}
					}
				} ));
	}

	@Override
	public Subscription EnqueueAlbum( Album album, Action1<QueuedAlbumResult> resultAction, Action1<Throwable> errorAction ) {
		return( EnqueueAlbum( album )
				.observeOn( AndroidSchedulers.mainThread())
				.subscribe( resultAction, errorAction ));
	}

	@Override
	public Observable<QueuedAlbumResult> EnqueueAlbum( final Album album ) {
		return Observable.create( new Observable.OnSubscribeFunc<QueuedAlbumResult>() {
			@Override
			public Subscription onSubscribe( Observer<? super QueuedAlbumResult> observer) {
				try {
					observer.onNext( new QueuedAlbumResult( album, getService().EnqueueAlbum( album.getAlbumId())));
					observer.onCompleted();
				} catch( Exception e ) {
					observer.onError( e );
				}

				return Subscriptions.empty();
			}
		}).subscribeOn( Schedulers.threadPoolForIO());
	}

	@Override
	public Subscription GetQueuedTrackList( Action1<PlayQueueListResult> resultAction, Action1<Throwable> errorAction ) {
		return( GetQueuedTrackList()
					.observeOn( AndroidSchedulers.mainThread())
					.subscribe( resultAction, errorAction ));
	}

	@Override
	public Observable<PlayQueueListResult> GetQueuedTrackList() {
		return( Observable.create( new Observable.OnSubscribeFunc<PlayQueueListResult>() {
			@Override
			public Subscription onSubscribe( Observer<? super PlayQueueListResult> observer ) {
				try {
					observer.onNext( new PlayQueueListResult( getService().GetQueuedTrackList()));
					observer.onCompleted();
				}
				catch( Exception ex ) {
					observer.onError( ex );
				}

				return( Subscriptions.empty());
			}
		} )).subscribeOn( Schedulers.threadPoolForIO());
	}

	@Override
	public Observable<BaseServerResult> ExecuteTransportCommand( final TransportCommand command ) {
		return( Observable.create( new Observable.OnSubscribeFunc<BaseServerResult>() {
			@Override
			public Subscription onSubscribe( Observer<? super BaseServerResult> observer ) {
				try {
					observer.onNext( new BaseServerResult( getService().ExecuteTransportCommand( mTransportCommands.get( command ))));
					observer.onCompleted();
				}
				catch( Exception ex ) {
					observer.onError( ex );
				}

				return( Subscriptions.empty());
			}
		} )).subscribeOn( Schedulers.threadPoolForIO());
	}
}
