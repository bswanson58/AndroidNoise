package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 12/31/13.

import com.SecretSquirrel.AndroidNoise.dto.SearchResult;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseSearch;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.RemoteServerSearchApi;

import javax.inject.Inject;

import retrofit.RestAdapter;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.concurrency.Schedulers;
import rx.subscriptions.Subscriptions;

public class NoiseSearchClient implements INoiseSearch {
	private final RemoteServerSearchApi mService;

	@Inject
	public NoiseSearchClient( RemoteServerSearchApi service ) {
		mService = service;
	}

	@Override
	public Observable<SearchResult> Search( final String searchTerms ) {
		return( Observable.create( new Observable.OnSubscribeFunc<SearchResult>() {
			@Override
			public Subscription onSubscribe( Observer<? super SearchResult> observer ) {
				try {
					observer.onNext( new SearchResult( mService.Search( searchTerms )));
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
