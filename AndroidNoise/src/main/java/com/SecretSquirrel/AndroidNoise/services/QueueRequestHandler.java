package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by BSwanson on 12/23/13.

import android.os.Bundle;

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.dto.Favorite;
import com.SecretSquirrel.AndroidNoise.dto.QueuedAlbumResult;
import com.SecretSquirrel.AndroidNoise.dto.QueuedTrackResult;
import com.SecretSquirrel.AndroidNoise.dto.SearchResultItem;
import com.SecretSquirrel.AndroidNoise.dto.Track;
import com.SecretSquirrel.AndroidNoise.events.EventArtistPlayed;
import com.SecretSquirrel.AndroidNoise.events.EventPlayAlbum;
import com.SecretSquirrel.AndroidNoise.events.EventPlayFavorite;
import com.SecretSquirrel.AndroidNoise.events.EventPlaySearchItem;
import com.SecretSquirrel.AndroidNoise.events.EventPlayTrack;
import com.SecretSquirrel.AndroidNoise.events.EventPlayTrackList;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
import com.SecretSquirrel.AndroidNoise.interfaces.INotificationManager;
import com.SecretSquirrel.AndroidNoise.interfaces.IQueueRequestHandler;
import com.SecretSquirrel.AndroidNoise.support.Constants;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import rx.functions.Action1;

public class QueueRequestHandler implements IQueueRequestHandler {
	private final INoiseQueue           mNoiseQueue;
	private final INoiseData            mNoiseData;
	private final EventBus              mEventBus;
	private final INotificationManager  mNotificationManager;

	@Inject
	public QueueRequestHandler( EventBus eventBus, INotificationManager notificationManager,
	                            INoiseData noiseData, INoiseQueue noiseQueue ) {
		mNoiseData = noiseData;
		mNoiseQueue = noiseQueue;
		mEventBus = eventBus;
		mNotificationManager = notificationManager;

		mEventBus.register( this );
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
		playTrack( args.getArtistId(), args.getTrackId(), args.getTrackName());
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventPlayTrackList args ) {
		playTrackList( args.getTrackList());
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventPlayFavorite args ) {
		Favorite    favorite = args.getFavorite();

		if( favorite != null ) {
			PlayFavorite( favorite );
		}
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventPlaySearchItem args ) {
		SearchResultItem    searchItem = args.getSearchItem();

		if( searchItem != null ) {
			PlaySearchItem( searchItem );
		}
	}

	public void PlayAlbum( Album album ) {
		if( album != null ) {
			mNoiseQueue.EnqueueAlbum( album, new Action1<QueuedAlbumResult>() {
				@Override
				public void call( QueuedAlbumResult queuedAlbumResult ) {
					if( queuedAlbumResult.Success ) {
						mNotificationManager.NotifyItemQueued( queuedAlbumResult.getAlbum());
					}
					else {
						mNotificationManager.NotifyItemQueued( queuedAlbumResult.getAlbum(), queuedAlbumResult.ErrorMessage );
					}
				}
			} );

			notifyArtistPlayed( album.getArtistId());
		}
	}

	public void PlayTrack( Track track ) {
		if( track != null ) {
			mNoiseQueue.EnqueueTrack( track, new Action1<QueuedTrackResult>() {
				@Override
				public void call( QueuedTrackResult queuedTrackResult ) {
					if( queuedTrackResult.Success ) {
						mNotificationManager.NotifyItemQueued( queuedTrackResult.getTrack());
					}
					else {
						mNotificationManager.NotifyItemQueued( queuedTrackResult.getTrack(), queuedTrackResult.ErrorMessage );
					}
				}
			} );

			notifyArtistPlayed( track.getArtistId());
		}
	}

	private void playTrack( long artistId, long trackId, final String trackName ) {
		mNoiseQueue.EnqueueTrack( trackId, new Action1<QueuedTrackResult>() {
			@Override
			public void call( QueuedTrackResult queuedTrackResult ) {
				if( queuedTrackResult.Success ) {
					mNotificationManager.NotifyItemQueued( trackName );
				}
				else {
					mNotificationManager.NotifyItemQueued( trackName, queuedTrackResult.ErrorMessage );
				}
			}
		} );

		notifyArtistPlayed( artistId );
	}

	private void playTrackList( long[] trackList ) {
		mNoiseQueue.EnqueueTrackList( trackList, new Action1<QueuedTrackResult>() {
			@Override
			public void call( QueuedTrackResult result ) {
				if( result.Success ) {
					mNotificationManager.NotifyListQueued( result.getTrackCount());
				}
				else {
					mNotificationManager.NotifyListQueued( result.ErrorMessage );
				}
			}
		} );
	}

	private void PlayFavorite( Favorite favorite ) {
		if( favorite != null ) {
			if( favorite.getTrackId() != Constants.NULL_ID ) {
				PlayTrack( new Track( favorite ));
			}
			else if( favorite.getAlbumId() != Constants.NULL_ID ) {
				PlayAlbum( new Album( favorite ));
			}
		}
	}

	private void PlaySearchItem( SearchResultItem searchItem ) {
		if( searchItem != null ) {
			if( searchItem.getTrackId() != Constants.NULL_ID ) {
				PlayTrack( new Track( searchItem ));
			}
			else if( searchItem.getAlbumId() != Constants.NULL_ID ) {
				PlayAlbum( new Album( searchItem ));
			}
		}
	}

	private void notifyArtistPlayed( long artistId ) {
		ArtistResolver resolver = new ArtistResolver( mNoiseData );

		resolver.requestArtist( artistId, new ServiceResultReceiver.Receiver() {
			@Override
			public void onReceiveResult( int resultCode, Bundle resultData ) {
				Artist artist = resultData.getParcelable( NoiseRemoteApi.Artist );

				if( artist != null ) {
					mEventBus.post( new EventArtistPlayed( artist ));
				}
			}
		});
	}
}
