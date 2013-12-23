package com.SecretSquirrel.AndroidNoise.model;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import android.content.Context;
import android.widget.Toast;

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.QueuedAlbumResult;
import com.SecretSquirrel.AndroidNoise.dto.QueuedTrackResult;
import com.SecretSquirrel.AndroidNoise.dto.Track;
import com.SecretSquirrel.AndroidNoise.events.EventPlayAlbum;
import com.SecretSquirrel.AndroidNoise.events.EventPlayTrack;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;

import de.greenrobot.event.EventBus;
import rx.util.functions.Action1;

public class QueueRequestHandler {
	private Context mContext;
	private IApplicationState mApplicationState;

	public QueueRequestHandler( Context context, IApplicationState applicationState ) {
		mContext = context;
		mApplicationState = applicationState;

		EventBus.getDefault().register( this );
	}

	public void onEvent( EventPlayAlbum args ) {
		Album album = args.getAlbum();

		if( album != null ) {
			PlayAlbum( album );
		}
	}

	public void onEvent( EventPlayTrack args ) {
		Track track = args.getTrack();

		if( track != null ) {
			PlayTrack( track );
		}
	}

	public void PlayAlbum( Album album ) {
		if( album != null ) {
			mApplicationState.getQueueClient().EnqueueAlbum( album, new Action1<QueuedAlbumResult>() {
				@Override
				public void call( QueuedAlbumResult queuedAlbumResult ) {
					if( queuedAlbumResult.Success ) {
						Toast.makeText( mContext, "Album '" + queuedAlbumResult.getAlbum().Name + "' was queued!", Toast.LENGTH_SHORT ).show();
					}
					else {
						Toast.makeText( mContext, "An error occurred while queuing album '" + queuedAlbumResult.getAlbum().Name + "'", Toast.LENGTH_LONG ).show();
					}
				}
			} );
		}
	}

	public void PlayTrack( Track track ) {
		if( track != null ) {
			mApplicationState.getQueueClient().EnqueueTrack( track, new Action1<QueuedTrackResult>() {
				@Override
				public void call( QueuedTrackResult queuedTrackResult ) {
					if( queuedTrackResult.Success ) {
						Toast.makeText( mContext, "Track '" + queuedTrackResult.getTrack().Name + "' was queued!", Toast.LENGTH_SHORT ).show();
					}
					else {
						Toast.makeText( mContext, "An error occurred while queuing album '" + queuedTrackResult.getTrack().Name + "'", Toast.LENGTH_LONG ).show();
					}
				}
			} );
		}
	}
}
