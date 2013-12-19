package com.SecretSquirrel.AndroidNoise.interfaces;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.QueuedAlbumResult;
import com.SecretSquirrel.AndroidNoise.dto.QueuedTrackResult;
import com.SecretSquirrel.AndroidNoise.dto.Track;

import rx.Observable;
import rx.Subscription;
import rx.util.functions.Action1;

public interface INoiseQueue {
	public  Subscription                    EnqueueTrack( Track track, Action1<QueuedTrackResult> result );
	public  Subscription                    EnqueueTrack( Track track, Action1<QueuedTrackResult> result, Action1<Throwable> errorAction );
	public  Observable<QueuedTrackResult>   EnqueueTrack( final Track track );

	public  Subscription                    EnqueueAlbum( Album album, Action1<QueuedAlbumResult> resultAction );
	public  Subscription                    EnqueueAlbum( Album album, Action1<QueuedAlbumResult> result, Action1<Throwable> errorAction );
	public  Observable<QueuedAlbumResult>   EnqueueAlbum( final Album album );
}
