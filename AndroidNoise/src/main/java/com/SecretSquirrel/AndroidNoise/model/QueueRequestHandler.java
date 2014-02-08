package com.SecretSquirrel.AndroidNoise.model;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

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
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
import com.SecretSquirrel.AndroidNoise.services.ArtistResolver;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;
import com.SecretSquirrel.AndroidNoise.support.Constants;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import rx.util.functions.Action1;

public class QueueRequestHandler {
	private final Context           mContext;
	private final INoiseQueue       mNoiseQueue;
	private final INoiseData        mNoiseData;
	private final EventBus          mEventBus;

	@Inject
	public QueueRequestHandler( Context context, EventBus eventBus,
	                            INoiseData noiseData, INoiseQueue noiseQueue ) {
		mNoiseData = noiseData;
		mNoiseQueue = noiseQueue;
		mContext = context;
		mEventBus = eventBus;

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
						Toast.makeText( mContext, "Album '" + queuedAlbumResult.getAlbum().getName() + "' was queued!", Toast.LENGTH_SHORT ).show();
					}
					else {
						Toast.makeText( mContext, "An error occurred while queuing album '" + queuedAlbumResult.getAlbum().getName() + "'", Toast.LENGTH_LONG ).show();
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
						Toast.makeText( mContext, "Track '" + queuedTrackResult.getTrack().getName() + "' was queued!", Toast.LENGTH_SHORT ).show();
					}
					else {
						Toast.makeText( mContext, "An error occurred while queuing album '" + queuedTrackResult.getTrack().getName() + "'", Toast.LENGTH_LONG ).show();
					}
				}
			} );

			notifyArtistPlayed( track.getArtistId());
		}
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
