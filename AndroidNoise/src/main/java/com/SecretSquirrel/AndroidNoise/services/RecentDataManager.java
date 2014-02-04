package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 2/4/14.

import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.events.EventArtistPlayed;
import com.SecretSquirrel.AndroidNoise.events.EventArtistViewed;
import com.SecretSquirrel.AndroidNoise.events.EventRecentDataUpdated;
import com.SecretSquirrel.AndroidNoise.interfaces.IRecentData;
import com.SecretSquirrel.AndroidNoise.support.Constants;

import java.util.List;

import de.greenrobot.event.EventBus;

public class RecentDataManager implements IRecentData {
	private RecentArtistList    mRecentlyPlayedList;
	private RecentArtistList    mRecentlyViewedList;

	public RecentDataManager() {
		mRecentlyPlayedList = new RecentArtistList( Constants.RECENT_LIST_SIZE );
		mRecentlyViewedList = new RecentArtistList( Constants.RECENT_LIST_SIZE );
	}

	@Override
	public void start() {
		EventBus.getDefault().register( this );
	}

	@Override
	public void persistData() {

	}

	@Override
	public void stop() {
		EventBus.getDefault().unregister( this );
	}

	@Override
	public List<Artist> getRecentlyPlayedArtists() {
		return( mRecentlyPlayedList );
	}

	@Override
	public List<Artist> getRecentlyViewedArtists() {
		return( mRecentlyViewedList );
	}

	@SuppressWarnings("unused")
	public void onEvent( EventArtistViewed args ) {
		mRecentlyViewedList.putMostRecentArtist( args.getArtist());

		EventBus.getDefault().post( new EventRecentDataUpdated());
	}

	@SuppressWarnings("unused")
	public void onEvent( EventArtistPlayed args ) {
		mRecentlyPlayedList.putMostRecentArtist( args.getArtist());

		EventBus.getDefault().post( new EventRecentDataUpdated());
	}
}
