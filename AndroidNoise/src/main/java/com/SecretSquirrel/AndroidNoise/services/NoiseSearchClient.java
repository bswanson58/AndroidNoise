package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by BSwanson on 12/31/13.

import com.SecretSquirrel.AndroidNoise.dto.SearchResult;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseSearch;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.RemoteServerSearchApi;

import javax.inject.Inject;
import javax.inject.Provider;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class NoiseSearchClient implements INoiseSearch {
	private final Provider<RemoteServerSearchApi>   mServiceProvider;
	private RemoteServerSearchApi                   mService;

	@Inject
	public NoiseSearchClient( EventBus eventBus, Provider<RemoteServerSearchApi> serviceProvider ) {
		mServiceProvider = serviceProvider;

		eventBus.register( this );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventServerSelected args ) {
		mService = null;
	}

	private RemoteServerSearchApi getService() {
		if( mService == null ) {
			mService = mServiceProvider.get();
		}

		return( mService );
	}

	@Override
	public Observable<SearchResult> Search( final String searchTerms ) {
		return( Observable.create( new Observable.OnSubscribe<SearchResult>() {
			@Override
			public void call( Subscriber<? super SearchResult> subscriber ) {
				try {
					subscriber.onNext( new SearchResult( getService().Search( searchTerms )));
					subscriber.onCompleted();
				}
				catch( Exception ex ) {
					subscriber.onError( ex );
				}
			}
		} ).subscribeOn( Schedulers.io()));
	}
}
