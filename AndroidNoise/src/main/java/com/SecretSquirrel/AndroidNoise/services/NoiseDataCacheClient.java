package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 1/13/14.

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;

import java.util.Hashtable;

import javax.inject.Inject;
import javax.inject.Named;

import de.greenrobot.event.EventBus;

public class NoiseDataCacheClient implements INoiseData {
	private static final int    cCacheSize = 25;

	private class DatedBundle {
		private Bundle  mBundle;
		private long    mLastAccess;

		public DatedBundle( Bundle bundle ) {
			mBundle = bundle;

			updateLastAccess();
		}

		public Bundle getBundle() {
			return( mBundle );
		}

		public long getLastAccess() {
			return( mLastAccess );
		}

		public void updateLastAccess() {
			mLastAccess = System.currentTimeMillis();
		}
	}

	private final INoiseData                mNoiseData;
	private Bundle                          mArtistList;
	private Hashtable<Long, DatedBundle>    mArtistInfo;
	private Hashtable<Long, DatedBundle>    mAlbumList;
	private Hashtable<Long, DatedBundle>    mAlbumInfo;
	private Hashtable<Long, DatedBundle>    mTrackList;
	private Hashtable<Long, DatedBundle>    mArtistTracks;
	private Bundle                          mFavoritesList;

	@Inject
	public NoiseDataCacheClient( EventBus eventBus,
	                             @Named( "NoiseDataClient" ) INoiseData downstreamClient ) {
		mNoiseData = downstreamClient;

		mArtistInfo = new Hashtable<Long, DatedBundle>();
		mAlbumList = new Hashtable<Long, DatedBundle>();
		mAlbumInfo = new Hashtable<Long, DatedBundle>();
		mTrackList = new Hashtable<Long, DatedBundle>();
		mArtistTracks = new Hashtable<Long, DatedBundle>();

		eventBus.register( this );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventServerSelected args ) {
		mArtistInfo.clear();
		mAlbumInfo.clear();
		mAlbumList.clear();
		mTrackList.clear();
		mArtistTracks.clear();

		mArtistList = null;
		mFavoritesList = null;
	}

	@Override
	public void GetArtistList( final ResultReceiver receiver ) {
		if( mArtistList != null ) {
			receiver.send( NoiseRemoteApi.RemoteResultSuccess, mArtistList );
		}
		else {
			mNoiseData.GetArtistList( new ResultReceiver( new Handler()) {
				@Override
				public void onReceiveResult( int resultCode, Bundle resultData ) {
					if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
						mArtistList = resultData;

						receiver.send( resultCode, resultData );
					}
					else {
						receiver.send( resultCode, null );
					}
				}
			});
		}
	}

	@Override
	public void GetArtistInfo( final long forArtist, final ResultReceiver receiver ) {
		if( mArtistInfo.containsKey( forArtist )) {
			DatedBundle bundle = mArtistInfo.get( forArtist );

			bundle.updateLastAccess();
			receiver.send( NoiseRemoteApi.RemoteResultSuccess, bundle.getBundle());
		}
		else {
			mNoiseData.GetArtistInfo( forArtist, new ResultReceiver( new Handler() ) {
				@Override
				public void onReceiveResult( int resultCode, Bundle resultData ) {
					if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
						mArtistInfo.put( forArtist, new DatedBundle( resultData ) );
						trimCache( mArtistInfo );

						receiver.send( resultCode, resultData );
					}
					else {
						receiver.send( resultCode, null );
					}
				}
			} );
		}
	}

	@Override
	public void GetArtistTracks( final long forArtist, final ResultReceiver receiver ) {
		if( mArtistTracks.containsKey( forArtist )) {
			DatedBundle bundle = mArtistTracks.get( forArtist );

			bundle.updateLastAccess();
			receiver.send( NoiseRemoteApi.RemoteResultSuccess, bundle.getBundle());
		}
		else {
			mNoiseData.GetArtistTracks( forArtist, new ResultReceiver( new Handler() ) {
				@Override
				public void onReceiveResult( int resultCode, Bundle resultData ) {
					if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
						mArtistTracks.put( forArtist, new DatedBundle( resultData ));
						trimCache( mArtistTracks );

						receiver.send( resultCode, resultData );
					}
					else {
						receiver.send( resultCode, null );
					}
				}
			} );
		}
	}

	@Override
	public void GetAlbumList( final long forArtist, final ResultReceiver receiver ) {
		if( mAlbumList.containsKey( forArtist )) {
			DatedBundle bundle = mAlbumList.get( forArtist );

			bundle.updateLastAccess();
			receiver.send( NoiseRemoteApi.RemoteResultSuccess, bundle.getBundle() );
		}
		else {
			mNoiseData.GetAlbumList( forArtist, new ResultReceiver( new Handler() ) {
				@Override
				public void onReceiveResult( int resultCode, Bundle resultData ) {
					if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
						mAlbumList.put( forArtist, new DatedBundle( resultData ) );
						trimCache( mAlbumList );

						receiver.send( resultCode, resultData );
					}
					else {
						receiver.send( resultCode, null );
					}
				}
			} );
		}
	}

	@Override
	public void GetAlbumInfo( final long forAlbum, final ResultReceiver receiver ) {
		if( mAlbumInfo.containsKey( forAlbum )) {
			DatedBundle bundle = mAlbumInfo.get( forAlbum );

			bundle.updateLastAccess();
			receiver.send( NoiseRemoteApi.RemoteResultSuccess, bundle.getBundle());
		}
		else {
			mNoiseData.GetAlbumInfo( forAlbum, new ResultReceiver( new Handler() ) {
				@Override
				public void onReceiveResult( int resultCode, Bundle resultData ) {
					if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
						mAlbumInfo.put( forAlbum, new DatedBundle( resultData ) );
						trimCache( mAlbumInfo );

						receiver.send( resultCode, resultData );
					}
					else {
						receiver.send( resultCode, null );
					}
				}
			} );
		}
	}

	@Override
	public void GetTrackList( final long forAlbum, final ResultReceiver receiver ) {
		if( mTrackList.containsKey( forAlbum )) {
			DatedBundle bundle = mTrackList.get( forAlbum );

			bundle.updateLastAccess();
			receiver.send( NoiseRemoteApi.RemoteResultSuccess, bundle.getBundle());
		}
		else {
			mNoiseData.GetTrackList( forAlbum, new ResultReceiver( new Handler() ) {
				@Override
				public void onReceiveResult( int resultCode, Bundle resultData ) {
					if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
						mTrackList.put( forAlbum, new DatedBundle( resultData ) );
						trimCache( mTrackList );

						receiver.send( resultCode, resultData );
					}
					else {
						receiver.send( resultCode, null );
					}
				}
			} );
		}
	}

	@Override
	public void GetFavoritesList( final ResultReceiver receiver ) {
		if( mFavoritesList != null ) {
			receiver.send( NoiseRemoteApi.RemoteResultSuccess, mFavoritesList );
		}
		else {
			mNoiseData.GetFavoritesList( new ResultReceiver( new Handler()) {
				@Override
				public void onReceiveResult( int resultCode, Bundle resultData ) {
					if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
						mFavoritesList = resultData;

						receiver.send( resultCode, resultData );
					}
					else {
						receiver.send( resultCode, null );
					}
				}
			});
		}
	}

	private void trimCache( Hashtable<Long, DatedBundle> cacheList ) {
		if( cacheList.size() > cCacheSize ) {
			long    oldestEntryKey = 0;
			long    lastAccess = System.currentTimeMillis();

			for( Long entry : cacheList.keySet()) {
				DatedBundle bundle = cacheList.get( entry );

				if( bundle.getLastAccess() < lastAccess ) {
					oldestEntryKey = entry;
					lastAccess = bundle.getLastAccess();
				}
			}

			if( oldestEntryKey > 0 ) {
				cacheList.remove( oldestEntryKey );
			}
		}
	}
}
