package com.SecretSquirrel.AndroidNoise.model;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import android.content.Context;
import android.widget.Toast;

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Favorite;
import com.SecretSquirrel.AndroidNoise.dto.QueuedAlbumResult;
import com.SecretSquirrel.AndroidNoise.dto.QueuedTrackResult;
import com.SecretSquirrel.AndroidNoise.dto.Track;
import com.SecretSquirrel.AndroidNoise.events.EventPlayAlbum;
import com.SecretSquirrel.AndroidNoise.events.EventPlayFavorite;
import com.SecretSquirrel.AndroidNoise.events.EventPlayTrack;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.support.Constants;

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

	@SuppressWarnings( "unused" )
	public void onEvent( EventPlayAlbum args ) {
		Album album = args.getAlbum();

		if( album != null ) {
			PlayAlbum( album );
		}
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventPlayTrack args ) {
		Track track = args.getTrack();

		if( track != null ) {
			PlayTrack( track );
		}
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventPlayFavorite args ) {
		Favorite    favorite = args.getFavorite();

		if( favorite != null ) {
			PlayFavorite( favorite );
		}
	}

	public void PlayAlbum( Album album ) {
		if( album != null ) {
			mApplicationState.getQueueClient().EnqueueAlbum( album, new Action1<QueuedAlbumResult>() {
				@Override
				public void call( QueuedAlbumResult queuedAlbumResult ) {
					if( queuedAlbumResult.Success ) {
						Toast.makeText( mContext, "Album '" + queuedAlbumResult.getAlbum().getName() + "' was queued!", Toast.LENGTH_SHORT ).show();
					}
					else {
						Toast.makeText( mContext, "An error occurred while queuing album '" + queuedAlbumResult.getAlbum().getName() + "'", Toast.LENGTH_LONG ).show();
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

	private void PlayFavorite( Favorite favorite ) {
		if( favorite != null ) {
			if( favorite.TrackId != Constants.NULL_ID ) {
				PlayTrack( new Track( favorite ));
			}
			else if( favorite.AlbumId != Constants.NULL_ID ) {
				PlayAlbum( new Album( favorite ));
			}
		}
	}
}
