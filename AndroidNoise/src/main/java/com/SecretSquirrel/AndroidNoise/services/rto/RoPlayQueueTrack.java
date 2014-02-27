package com.SecretSquirrel.AndroidNoise.services.rto;

// Created by BSwanson on 12/29/13.

public class RoPlayQueueTrack {
	public long         Id;
	public long		    TrackId;
	public String	    TrackName;
	public long		    AlbumId;
	public String	    AlbumName;
	public long		    ArtistId;
	public String	    ArtistName;
	public int          DurationMilliseconds;
	public boolean		IsPlaying;
	public boolean		HasPlayed;
	public boolean		IsFaulted;
	public boolean		IsStrategySourced;
}
