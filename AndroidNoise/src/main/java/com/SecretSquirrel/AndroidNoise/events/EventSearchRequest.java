package com.SecretSquirrel.AndroidNoise.events;

/**
 * Created by BSwanson on 12/30/13.
 */
public class EventSearchRequest {
	private String  mSearchTerms;

	public EventSearchRequest( String searchTerm ) {
		mSearchTerms = searchTerm;
	}

	public String getSearchTerm() {
		return( mSearchTerms );
	}
}
