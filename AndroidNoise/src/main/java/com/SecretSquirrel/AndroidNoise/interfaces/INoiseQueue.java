package com.SecretSquirrel.AndroidNoise.interfaces;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.QueuedAlbumResult;
import com.SecretSquirrel.AndroidNoise.dto.QueuedTrackResult;
import com.SecretSquirrel.AndroidNoise.dto.Track;

import rx.Observable;
import rx.util.functions.Action1;

public interface INoiseQueue {
	public  void                            EnqueueTrack( Track track, Action1<QueuedTrackResult> result );
	public  Observable<QueuedTrackResult>   EnqueueTrack( final Track track );

	public  void                            EnqueueAlbum( Album album, Action1<QueuedAlbumResult> result );
	public  Observable<QueuedAlbumResult>   EnqueueAlbum( final Album album );
}
