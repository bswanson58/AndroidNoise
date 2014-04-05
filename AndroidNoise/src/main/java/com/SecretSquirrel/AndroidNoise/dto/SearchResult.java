package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by BSwanson on 12/31/13.

import com.SecretSquirrel.AndroidNoise.services.rto.RoSearchResult;

import java.util.ArrayList;

public class SearchResult {
	private boolean                     mSuccess;
	private String                      mErrorMessage;
	private ArrayList<SearchResultItem> mResults;
	private ArrayList<Long>             mRandomTracks;

	public SearchResult( RoSearchResult roResult ) {
		mSuccess = roResult.Success;
		mErrorMessage = roResult.ErrorMessage;
		mResults = new ArrayList<SearchResultItem>();
		mRandomTracks = new ArrayList<Long>();

		for( int index = 0; index < roResult.Items.length; index++ ) {
			mResults.add( new SearchResultItem( roResult.Items[index]));
		}
		for( int index = 0; index < roResult.RandomTracks.length; index++ ) {
			mRandomTracks.add( roResult.RandomTracks[index]);
		}
	}

	public boolean getSuccess() {
		return( mSuccess );
	}

	public String getErrorMessage() {
		return( mErrorMessage );
	}

	public ArrayList<SearchResultItem> getResults() {
		return( mResults );
	}

	public ArrayList<Long> getRandomTracks() {
		return( mRandomTracks );
	}
}
