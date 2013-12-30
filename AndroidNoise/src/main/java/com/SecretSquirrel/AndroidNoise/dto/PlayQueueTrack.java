package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 12/30/13.

import com.SecretSquirrel.AndroidNoise.services.rto.RoPlayQueueTrack;

public class PlayQueueTrack {
	private long        mTrackId;
	private String	    mTrackName;
	private String	    mAlbumName;
	private String	    mArtistName;
	private int         mDurationMilliseconds;
	private boolean		mIsPlaying;
	private boolean		mHasPlayed;
	private boolean		mIsFaulted;
	private boolean		mIsStrategySourced;

	public PlayQueueTrack( RoPlayQueueTrack roTrack ) {
		mTrackId = roTrack.TrackId;
		mTrackName = roTrack.TrackName;
		mAlbumName = roTrack.AlbumName;
		mArtistName = roTrack.ArtistName;
		mDurationMilliseconds = roTrack.DurationMilliseconds;
		mIsPlaying = roTrack.IsPlaying;
		mHasPlayed = roTrack.HasPlayed;
		mIsFaulted = roTrack.IsFaulted;
		mIsStrategySourced = roTrack.IsStrategySourced;
	}

	public String getTrackName() {
		return( mTrackName );
	}

	public String getAlbumName() {
		return( mAlbumName );
	}

	public String getArtistName() {
		return( mArtistName );
	}

	public int getDurationMilliseconds() {
		return( mDurationMilliseconds );
	}

	public boolean isPlaying() {
		return( mIsPlaying );
	}

	public boolean isHasPlayed() {
		return( mHasPlayed );
	}

	public boolean isFaulted() {
		return( mIsFaulted );
	}

	public boolean isStrategySourced() {
		return( mIsStrategySourced );
	}

	public long getTrackId() {
		return( mTrackId );
	}
}
