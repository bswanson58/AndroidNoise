package com.SecretSquirrel.AndroidNoise.support;

import android.os.Bundle;

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.dto.Track;
import com.SecretSquirrel.AndroidNoise.events.EventPlayTrack;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;

import java.util.ArrayList;
import java.util.Random;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

// Created by BSwanson on 2/20/14.

public class RandomTrackSelector implements ShakeHandler.ShakeResponder, ServiceResultReceiver.Receiver {
	private final ServiceResultReceiver mServiceResultReceiver;
	private final INoiseData            mNoiseData;
	private final EventBus              mEventBus;

	@Inject
	public RandomTrackSelector( EventBus eventBus, INoiseData noiseData, ServiceResultReceiver receiver ) {
		mEventBus = eventBus;
		mNoiseData = noiseData;
		mServiceResultReceiver = receiver;
		mServiceResultReceiver.setReceiver( this );
	}

	@Override
	public void onShake() {
		mNoiseData.GetArtistList( mServiceResultReceiver );
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			int apiCode = resultData.getInt( NoiseRemoteApi.RemoteApiParameter );

			switch( apiCode ) {
				case NoiseRemoteApi.GetArtistList:
					selectArtist( resultData );
					break;

				case NoiseRemoteApi.GetAlbumList:
					selectAlbum( resultData );
					break;

				case NoiseRemoteApi.GetTrackList:
					selectTrack( resultData );
					break;
			}
		}
	}

	private void selectArtist( Bundle resultData ) {
		ArrayList<Artist> artistList = resultData.getParcelableArrayList( NoiseRemoteApi.ArtistList );

		if( artistList != null ) {
			Random  random = new Random();
			Artist  artist = artistList.get( random.nextInt( artistList.size()));

			mNoiseData.GetAlbumList( artist.getArtistId(), mServiceResultReceiver );
		}
	}

	private void selectAlbum( Bundle resultData ) {
		ArrayList<Album> albumList = resultData.getParcelableArrayList( NoiseRemoteApi.AlbumList );

		if( albumList != null ) {
			Random  random = new Random();
			Album   album = albumList.get( random.nextInt( albumList.size()));

			mNoiseData.GetTrackList( album.getAlbumId(), mServiceResultReceiver );
		}
	}

	private void selectTrack( Bundle resultData ) {
		ArrayList<Track> trackList = resultData.getParcelableArrayList( NoiseRemoteApi.TrackList );

		if( trackList != null ) {
			Random  random = new Random();
			Track   track = trackList.get( random.nextInt( trackList.size()));

			mEventBus.post( new EventPlayTrack( track ));
		}
	}
}
