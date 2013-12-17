package com.SecretSquirrel.AndroidNoise.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.dto.Track;
import com.SecretSquirrel.AndroidNoise.services.rto.RemoteServerDataApi;
import com.SecretSquirrel.AndroidNoise.services.rto.RoAlbum;
import com.SecretSquirrel.AndroidNoise.services.rto.RoAlbumListResult;
import com.SecretSquirrel.AndroidNoise.services.rto.RoArtist;
import com.SecretSquirrel.AndroidNoise.services.rto.RoArtistListResult;
import com.SecretSquirrel.AndroidNoise.services.rto.RoTrack;
import com.SecretSquirrel.AndroidNoise.services.rto.RoTrackListResult;

import java.util.ArrayList;

import retrofit.RestAdapter;

// Secret Squirrel Software - Created by bswanson on 12/6/13.

public class NoiseDataService extends IntentService {
	public NoiseDataService() {
		super( "NoiseDataService" );
	}

	@Override
	protected void onHandleIntent( Intent intent ) {
		int             callId = intent.getIntExtra( NoiseRemoteApi.RemoteApiParameter, 0 );
		String          serverAddress = intent.getStringExtra( NoiseRemoteApi.RemoteServerAddress );
		ResultReceiver  receiver = intent.getParcelableExtra( NoiseRemoteApi.RemoteCallReceiver );

		if( receiver != null ) {
			if(!TextUtils.isEmpty( serverAddress )) {
				switch( callId ) {
					case NoiseRemoteApi.GetArtistList:
						getArtistList( serverAddress, receiver );
						break;

					case NoiseRemoteApi.GetAlbumList:
						long    forArtist = intent.getLongExtra( NoiseRemoteApi.ArtistId, 0 );

						getAlbumList( forArtist, serverAddress, receiver );
						break;

					case NoiseRemoteApi.GetTrackList:
						long    forAlbum = intent.getLongExtra( NoiseRemoteApi.AlbumId, 0 );

						getTrackList( forAlbum, serverAddress, receiver );
						break;
				}
			}
			else {
				Bundle resultData = new Bundle();

				resultData.putString( NoiseRemoteApi.RemoteResultErrorMessage, "Server address is not valid." );
				receiver.send( NoiseRemoteApi.RemoteResultError, resultData );
			}
		}
	}

	private void getArtistList( String serverAddress, ResultReceiver receiver ) {
		Bundle  resultData = new Bundle();
		int     resultCode = NoiseRemoteApi.RemoteResultError;

		try {
			RestAdapter         restAdapter = new RestAdapter.Builder().setServer( serverAddress ).build();
			RemoteServerDataApi service = restAdapter.create( RemoteServerDataApi.class );

			RoArtistListResult  result = service.GetArtistList();

			if( result.Success ) {
				ArrayList<Artist> artists = new ArrayList<Artist>();

				for( RoArtist roArtist : result.Artists ) {
					artists.add( new Artist( roArtist ));
				}

				resultCode = NoiseRemoteApi.RemoteResultSuccess;
				resultData.putParcelableArrayList( NoiseRemoteApi.ArtistList, artists );
			}
			else {
				resultData.putString( NoiseRemoteApi.RemoteResultErrorMessage, result.ErrorMessage );
			}
		}
		catch( Exception ex ) {
			resultData.putString( NoiseRemoteApi.RemoteResultErrorMessage, ex.getMessage());
			resultCode = NoiseRemoteApi.RemoteResultException;
		}

		receiver.send( resultCode, resultData );
	}

	private void getAlbumList( long forArtist, String serverAddress, ResultReceiver receiver ) {
		Bundle  resultData = new Bundle();
		int     resultCode = NoiseRemoteApi.RemoteResultError;

		try {
			RestAdapter         restAdapter = new RestAdapter.Builder().setServer( serverAddress ).build();
			RemoteServerDataApi service = restAdapter.create( RemoteServerDataApi.class );

			RoAlbumListResult result = service.GetAlbumList( forArtist );

			if( result.Success ) {
				ArrayList<Album> albums = new ArrayList<Album>();

				for( RoAlbum roAlbum : result.Albums ) {
					albums.add( new Album( roAlbum ));
				}

				resultCode = NoiseRemoteApi.RemoteResultSuccess;
				resultData.putParcelableArrayList( NoiseRemoteApi.AlbumList, albums );
			}
			else {
				resultData.putString( NoiseRemoteApi.RemoteResultErrorMessage, result.ErrorMessage );
			}
		}
		catch( Exception ex ) {
			resultData.putString( NoiseRemoteApi.RemoteResultErrorMessage, ex.getMessage());
			resultCode = NoiseRemoteApi.RemoteResultException;
		}

		receiver.send( resultCode, resultData );
	}

	private void getTrackList( long forAlbum, String serverAddress, ResultReceiver receiver ) {
		Bundle  resultData = new Bundle();
		int     resultCode = NoiseRemoteApi.RemoteResultError;

		try {
			RestAdapter         restAdapter = new RestAdapter.Builder().setServer( serverAddress ).build();
			RemoteServerDataApi service = restAdapter.create( RemoteServerDataApi.class );

			RoTrackListResult result = service.GetTrackList( forAlbum );

			if( result.Success ) {
				ArrayList<Track> tracks = new ArrayList<Track>();

				for( RoTrack roTrack : result.Tracks ) {
					tracks.add( new Track( roTrack ));
				}

				resultCode = NoiseRemoteApi.RemoteResultSuccess;
				resultData.putParcelableArrayList( NoiseRemoteApi.TrackList, tracks );
			}
			else {
				resultData.putString( NoiseRemoteApi.RemoteResultErrorMessage, result.ErrorMessage );
			}
		}
		catch( Exception ex ) {
			resultData.putString( NoiseRemoteApi.RemoteResultErrorMessage, ex.getMessage());
			resultCode = NoiseRemoteApi.RemoteResultException;
		}

		receiver.send( resultCode, resultData );
	}
}
