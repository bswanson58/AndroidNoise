package com.SecretSquirrel.AndroidNoise.services;

// Created by BSwanson on 3/6/14.

import com.SecretSquirrel.AndroidNoise.dto.Library;
import com.SecretSquirrel.AndroidNoise.events.EventActivityPausing;
import com.SecretSquirrel.AndroidNoise.events.EventActivityResuming;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseLibrary;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.RemoteServerLibraryApi;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;
import com.SecretSquirrel.AndroidNoise.services.rto.RoLibraryListResult;

import javax.inject.Inject;
import javax.inject.Provider;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class NoiseLibraryClient implements INoiseLibrary {
	private final EventBus                          mEventBus;
	private final Provider<RemoteServerLibraryApi>  mServiceProvider;
	private RemoteServerLibraryApi                  mService;

	@Inject
	public NoiseLibraryClient( EventBus eventBus, Provider<RemoteServerLibraryApi> serviceProvider ) {
		mEventBus = eventBus;
		mServiceProvider = serviceProvider;

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

	private RemoteServerLibraryApi getService() {
		if( mService == null ) {
			mService = mServiceProvider.get();
		}

		return( mService );
	}
	@Override
	public Observable<Library[]> getLibraries() {
		return( Observable.create( new Observable.OnSubscribe<Library[]>() {
			@Override
			public void call( Subscriber<? super Library[]> subscriber ) {
				try {
					RoLibraryListResult result = getService().getLibraries();

					if( result.Success ) {
						Library[]   libraries = new Library[result.Libraries.length];

						for( int index = 0; index < libraries.length; index++ ) {
							libraries[index] = new Library( result.Libraries[index]);
						}

						subscriber.onNext( libraries );
						subscriber.onCompleted();
					}
					else {
						subscriber.onError( new Exception( String.format( "getLibraries failed: %s", result.ErrorMessage )));
					}
				}
				catch( Exception ex ) {
					subscriber.onError( ex );
				}
			}
		} ).subscribeOn( Schedulers.io() ));
	}

	@Override
	public Observable<BaseServerResult> syncLibrary() {
		return( Observable.create( new Observable.OnSubscribe<BaseServerResult>() {
			@Override
			public void call( Subscriber<? super BaseServerResult> subscriber ) {
				try {
					subscriber.onNext( getService().syncLibrary() );
					subscriber.onCompleted();
				}
				catch( Exception ex ) {
					subscriber.onError( ex );
				}
			}
		} ).subscribeOn( Schedulers.io()));
	}

	@Override
	public Observable<BaseServerResult> selectLibrary( final Library library ) {
		return( Observable.create( new Observable.OnSubscribe<BaseServerResult>() {
			@Override
			public void call( Subscriber<? super BaseServerResult> subscriber ) {
				try {
					subscriber.onNext( getService().selectLibrary( library.getLibraryId() ) );
					subscriber.onCompleted();
				}
				catch( Exception ex ) {
					subscriber.onError( ex );
				}
			}
		} ).subscribeOn( Schedulers.io()));
	}

	@Override
	public Observable<BaseServerResult> updateLibrary( final Library library ) {
		return( Observable.create( new Observable.OnSubscribe<BaseServerResult>() {
			@Override
			public void call( Subscriber<? super BaseServerResult> subscriber ) {
				try {
					subscriber.onNext( getService().updateLibrary( library.asRoLibrary() ) );
					subscriber.onCompleted();
				}
				catch( Exception ex ) {
					subscriber.onError( ex );
				}
			}
		} ).subscribeOn( Schedulers.io()));
	}

	@Override
	public Observable<Library> createLibrary( final Library library ) {
		return( Observable.create( new Observable.OnSubscribe<Library>() {
			@Override
			public void call( Subscriber<? super Library> subscriber ) {
				try {
					RoLibraryListResult result = getService().createLibrary( library.asRoLibrary());

					if( result.Success ) {
						subscriber.onNext( new Library( result.Libraries[0]));
						subscriber.onCompleted();
					}
					else {
						subscriber.onError( new Exception( String.format( "createLibrary failed: %s", result.ErrorMessage )));
					}
				}
				catch( Exception ex ) {
					subscriber.onError( ex );
				}
			}
		} ).subscribeOn( Schedulers.io()));
	}
}
