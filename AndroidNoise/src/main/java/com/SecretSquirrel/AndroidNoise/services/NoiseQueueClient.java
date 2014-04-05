package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by BSwanson on 12/17/13.

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.PlayQueueListResult;
import com.SecretSquirrel.AndroidNoise.dto.QueuedAlbumResult;
import com.SecretSquirrel.AndroidNoise.dto.QueuedTrackResult;
import com.SecretSquirrel.AndroidNoise.dto.StrategyInformation;
import com.SecretSquirrel.AndroidNoise.dto.Track;
import com.SecretSquirrel.AndroidNoise.events.EventActivityPausing;
import com.SecretSquirrel.AndroidNoise.events.EventActivityResuming;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.RemoteServerQueueApi;
import com.SecretSquirrel.AndroidNoise.services.rto.StrategyInformationResult;

import java.util.EnumMap;

import javax.inject.Inject;
import javax.inject.Provider;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class NoiseQueueClient implements INoiseQueue {
	private final EventBus                              mEventBus;
	private final Provider<RemoteServerQueueApi>        mServiceProvider;
	public  final EnumMap<TransportCommand, Integer>    mTransportCommands;
	public  final EnumMap<QueueCommand, Integer>        mQueueCommands;
	public  final EnumMap<QueueItemCommand, Integer>    mQueueItemCommands;
	private RemoteServerQueueApi                        mService;

	@Inject
	public NoiseQueueClient( EventBus eventBus, Provider<RemoteServerQueueApi> queueApi ) {
		mEventBus = eventBus;
		mServiceProvider = queueApi;

		mTransportCommands = new EnumMap<TransportCommand, Integer>( TransportCommand.class );
		mTransportCommands.put( TransportCommand.Play, 1 );
		mTransportCommands.put( TransportCommand.Stop, 2 );
		mTransportCommands.put( TransportCommand.Pause, 3 );
		mTransportCommands.put( TransportCommand.PlayNext, 4 );
		mTransportCommands.put( TransportCommand.PlayPrevious, 5 );
		mTransportCommands.put( TransportCommand.Repeat, 6 );

		mQueueCommands = new EnumMap<QueueCommand, Integer>( QueueCommand.class );
		mQueueCommands.put( QueueCommand.StartPlaying, 1 );
		mQueueCommands.put( QueueCommand.Clear, 2 );
		mQueueCommands.put( QueueCommand.ClearPlayed, 3 );

		mQueueItemCommands = new EnumMap<QueueItemCommand, Integer>( QueueItemCommand.class );
		mQueueItemCommands.put( QueueItemCommand.Remove, 1 );
		mQueueItemCommands.put( QueueItemCommand.PlayNext, 2 );
		mQueueItemCommands.put( QueueItemCommand.Replay, 3 );

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
		if(!mEventBus.isRegistered( this )) {
			mEventBus.register( this );
		}
	}

	private RemoteServerQueueApi getService() {
		if( mService == null ) {
			mService = mServiceProvider.get();
		}

		return( mService );
	}

	@Override
	public Subscription EnqueueTrack( long trackId, Action1<QueuedTrackResult> resultAction ) {
		return( EnqueueTrack( trackId )
				.observeOn( AndroidSchedulers.mainThread())
				.subscribe( resultAction, new Action1<Throwable>() {
					@Override
					public void call( Throwable throwable ) {
						Timber.e( throwable, "Default EnqueueTrack( id ) exception handler" );
					}
				} ));
	}

	@Override
	public Observable<QueuedTrackResult> EnqueueTrack( final long trackId ) {
		return Observable.create( new Observable.OnSubscribe<QueuedTrackResult>() {
			@Override
			public void call( Subscriber<? super QueuedTrackResult> subscriber ) {
				try {
					subscriber.onNext( new QueuedTrackResult( trackId, getService().EnqueueTrack( trackId ) ) );
					subscriber.onCompleted();
				} catch( Exception e ) {
					subscriber.onError( e );
				}
			}
		}).subscribeOn( Schedulers.io());
	}

	@Override
	public Subscription EnqueueTrackList( long[] trackList, Action1<QueuedTrackResult> resultAction ) {
		return( EnqueueTrackList( trackList )
				.observeOn( AndroidSchedulers.mainThread())
				.subscribe( resultAction, new Action1<Throwable>() {
					@Override
					public void call( Throwable throwable ) {
						Timber.e( throwable, "Default EnqueueTrackList( long[] ) exception handler" );
					}
				} ));
	}

	@Override
	public Observable<QueuedTrackResult> EnqueueTrackList( final long[] trackList ) {
		return Observable.create( new Observable.OnSubscribe<QueuedTrackResult>() {
			@Override
			public void call( Subscriber<? super QueuedTrackResult> subscriber ) {
				try {
					subscriber.onNext( new QueuedTrackResult( trackList, getService().EnqueueTrackList( trackList )));
					subscriber.onCompleted();
				} catch( Exception e ) {
					subscriber.onError( e );
				}
			}
		}).subscribeOn( Schedulers.io());
	}




	@Override
	public Subscription EnqueueTrack( Track track, Action1<QueuedTrackResult> resultAction ) {
		return( EnqueueTrack( track )
				.observeOn( AndroidSchedulers.mainThread())
				.subscribe( resultAction, new Action1<Throwable>() {
					@Override
					public void call( Throwable throwable ) {
						Timber.e( throwable, "Default EnqueueTrack exception handler" );
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
		return Observable.create( new Observable.OnSubscribe<QueuedTrackResult>() {
			@Override
			public void call( Subscriber<? super QueuedTrackResult> subscriber ) {
				try {
					subscriber.onNext( new QueuedTrackResult( track, getService().EnqueueTrack( track.getTrackId() ) ) );
					subscriber.onCompleted();
				} catch( Exception e ) {
					subscriber.onError( e );
				}
			}
		}).subscribeOn( Schedulers.io());
	}

	@Override
	public Subscription EnqueueAlbum( Album album, Action1<QueuedAlbumResult> resultAction ) {
		return( EnqueueAlbum( album )
				.observeOn( AndroidSchedulers.mainThread())
				.subscribe( resultAction, new Action1<Throwable>() {
					@Override
					public void call( Throwable throwable ) {
						Timber.e( throwable, "Default EnqueueAlbum exception handler" );
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
		return Observable.create( new Observable.OnSubscribe<QueuedAlbumResult>() {
			@Override
			public void call( Subscriber<? super QueuedAlbumResult> subscriber ) {
				try {
					subscriber.onNext( new QueuedAlbumResult( album, getService().EnqueueAlbum( album.getAlbumId() ) ) );
					subscriber.onCompleted();
				} catch( Exception e ) {
					subscriber.onError( e );
				}
			}
		}).subscribeOn( Schedulers.io());
	}

	@Override
	public Subscription GetQueuedTrackList( Action1<PlayQueueListResult> resultAction, Action1<Throwable> errorAction ) {
		return( GetQueuedTrackList()
					.observeOn( AndroidSchedulers.mainThread())
					.subscribe( resultAction, errorAction ));
	}

	@Override
	public Observable<PlayQueueListResult> GetQueuedTrackList() {
		return( Observable.create( new Observable.OnSubscribe<PlayQueueListResult>() {
			@Override
			public void call( Subscriber<? super PlayQueueListResult> subscriber ) {
				try {
					subscriber.onNext( new PlayQueueListResult( getService().GetQueuedTrackList() ) );
					subscriber.onCompleted();
				}
				catch( Exception ex ) {
					subscriber.onError( ex );
				}
			}
		} )).subscribeOn( Schedulers.io());
	}

	@Override
	public Observable<BaseServerResult> ExecuteTransportCommand( final TransportCommand command ) {
		return( Observable.create( new Observable.OnSubscribe<BaseServerResult>() {
			@Override
			public void call( Subscriber<? super BaseServerResult> subscriber ) {
				try {
					subscriber.onNext( new BaseServerResult( getService().ExecuteTransportCommand( mTransportCommands.get( command ) ) ) );
					subscriber.onCompleted();
				}
				catch( Exception ex ) {
					subscriber.onError( ex );
				}
			}
		} )).subscribeOn( Schedulers.io());
	}

	@Override
	public Observable<BaseServerResult> ExecuteQueueCommand( final QueueCommand command ) {
		return( Observable.create( new Observable.OnSubscribe<BaseServerResult>() {
			@Override
			public void call( Subscriber<? super BaseServerResult> subscriber ) {
				try {
					subscriber.onNext( new BaseServerResult( getService().ExecuteQueueCommand( mQueueCommands.get( command ) ) ) );
					subscriber.onCompleted();
				}
				catch( Exception ex ) {
					subscriber.onError( ex );
				}
			}
		} )).subscribeOn( Schedulers.io());
	}

	@Override
	public Observable<BaseServerResult> ExecuteQueueItemCommand( final QueueItemCommand command, final long itemId ) {
		return( Observable.create( new Observable.OnSubscribe<BaseServerResult>() {
			@Override
			public void call( Subscriber<? super BaseServerResult> subscriber ) {
				try {
					subscriber.onNext( new BaseServerResult( getService().ExecuteQueueItemCommand( mQueueItemCommands.get( command ), itemId )));
					subscriber.onCompleted();
				}
				catch( Exception ex ) {
					subscriber.onError( ex );
				}
			}
		} )).subscribeOn( Schedulers.io());
	}

	@Override
	public Observable<StrategyInformation> GetStrategyInformation() {
		return( Observable.create( new Observable.OnSubscribe<StrategyInformation>() {
			@Override
			public void call( Subscriber<? super StrategyInformation> subscriber ) {
				try {
					StrategyInformationResult   result = getService().GetQueueStrategyInformation();

					if( result.Success ) {
						subscriber.onNext( new StrategyInformation( result.StrategyInformation ) );
						subscriber.onCompleted();
					}
					else {
						subscriber.onError( new Throwable( result.ErrorMessage ) );
					}
				}
				catch( Exception ex ) {
					subscriber.onError( ex );
				}
			}
		} )).subscribeOn( Schedulers.io());
	}

	@Override
	public Observable<BaseServerResult> SetStrategyInformation( final int playStrategyId, final long playStrategyParameter,
	                                                            final int exhaustedStrategyId, final long exhaustedStrategyParameter ) {
		return( Observable.create( new Observable.OnSubscribe<BaseServerResult>() {
			@Override
			public void call( Subscriber<? super BaseServerResult> subscriber ) {
				try {
					subscriber.onNext( new BaseServerResult( getService()
							.SetQueueStrategies( playStrategyId, playStrategyParameter,
												 exhaustedStrategyId, exhaustedStrategyParameter )));
					subscriber.onCompleted();
				}
				catch( Exception ex ) {
					subscriber.onError( ex );
				}
			}
		} )).subscribeOn( Schedulers.io());
	}
}
