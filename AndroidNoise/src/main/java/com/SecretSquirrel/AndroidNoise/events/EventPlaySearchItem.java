package com.SecretSquirrel.AndroidNoise.events;

// Secret Squirrel Software - Created by bswanson on 1/24/14.

import com.SecretSquirrel.AndroidNoise.dto.SearchResultItem;

public class EventPlaySearchItem {
	private SearchResultItem    mSearchItem;

	public EventPlaySearchItem( SearchResultItem searchItem ) {
		mSearchItem = searchItem;
	}

	public SearchResultItem getSearchItem() {
		return( mSearchItem );
	}
}
