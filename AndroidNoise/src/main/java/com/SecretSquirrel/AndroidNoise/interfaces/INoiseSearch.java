package com.SecretSquirrel.AndroidNoise.interfaces;

// Secret Squirrel Software - Created by bswanson on 12/31/13.

import com.SecretSquirrel.AndroidNoise.dto.SearchResult;

import rx.Observable;

public interface INoiseSearch {
	Observable<SearchResult>    Search( String searchTerms );
}
