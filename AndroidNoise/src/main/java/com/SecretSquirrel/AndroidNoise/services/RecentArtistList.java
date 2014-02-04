package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 2/4/14.

import com.SecretSquirrel.AndroidNoise.dto.Artist;

import java.util.ArrayList;

public class RecentArtistList extends ArrayList<Artist> {
	private int     mMaxListSize;

	public RecentArtistList( int maxListSize ) {
		if( maxListSize > 1 ) {
			mMaxListSize = maxListSize;
		}
		else {
			mMaxListSize = 5;
		}
	}

	public void putMostRecentArtist( Artist artist ) {
		for( Artist listArtist : this ) {
			if( listArtist.getArtistId() == artist.getArtistId()) {
				remove( listArtist );

				break;
			}
		}

		add( 0, artist );

		while( size() > mMaxListSize ) {
			remove( size() - 1 );
		}
	}
}
