package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 12/31/13.

import com.SecretSquirrel.AndroidNoise.dto.SearchResult;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseSearch;
import com.SecretSquirrel.AndroidNoise.services.rto.RemoteServerSearchApi;

import retrofit.RestAdapter;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.concurrency.Schedulers;
import rx.subscriptions.Subscriptions;

public class NoiseSearchClient implements INoiseSearch {
	private final String            mServerAddress;
	private RemoteServerSearchApi mService;

	public NoiseSearchClient( String serverAddress ) {
		mServerAddress = serverAddress;
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

	private RemoteServerSearchApi getService() {
		if( mService == null ) {
			mService = buildService( mServerAddress );
		}

		return( mService );
	}

	private RemoteServerSearchApi buildService( String serverAddress ) {
		RestAdapter restAdapter = new RestAdapter.Builder()
				.setServer( serverAddress )
				.build();

		return( restAdapter.create( RemoteServerSearchApi.class ));
	}
}
