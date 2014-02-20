package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 12/31/13.

import com.SecretSquirrel.AndroidNoise.dto.SearchResult;
import com.SecretSquirrel.AndroidNoise.events.EventActivityPausing;
import com.SecretSquirrel.AndroidNoise.events.EventActivityResuming;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseSearch;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.RemoteServerSearchApi;

import javax.inject.Inject;
import javax.inject.Provider;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.concurrency.Schedulers;
import rx.subscriptions.Subscriptions;

public class NoiseSearchClient implements INoiseSearch {
	private final EventBus                          mEventBus;
	private final Provider<RemoteServerSearchApi>   mServiceProvider;
	private RemoteServerSearchApi                   mService;

	@Inject
	public NoiseSearchClient( EventBus eventBus, Provider<RemoteServerSearchApi> serviceProvider ) {
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

	private RemoteServerSearchApi getService() {
		if( mService == null ) {
			mService = mServiceProvider.get();
		}

		return( mService );
	}

	@Override
	public Observable<SearchResult> Search( final String searchTerms ) {
		return( Observable.create( new Observable.OnSubscribeFunc<SearchResult>() {
			@Override
			public Subscription onSubscribe( Observer<? super SearchResult> observer ) {
				try {
					observer.onNext( new SearchResult( getService().Search( searchTerms )));
					observer.onCompleted();
				}
				catch( Exception ex ) {
					observer.onError( ex );
				}

				return( Subscriptions.empty());
			}
		} ).subscribeOn( Schedulers.threadPoolForIO()));
	}
}
