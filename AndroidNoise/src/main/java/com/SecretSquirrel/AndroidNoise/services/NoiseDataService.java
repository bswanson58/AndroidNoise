package com.SecretSquirrel.AndroidNoise.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.AlbumInfo;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.dto.ArtistInfo;
import com.SecretSquirrel.AndroidNoise.dto.Favorite;
import com.SecretSquirrel.AndroidNoise.dto.Track;
import com.SecretSquirrel.AndroidNoise.services.noiseApi.RemoteServerDataApi;
import com.SecretSquirrel.AndroidNoise.services.rto.RoAlbum;
import com.SecretSquirrel.AndroidNoise.services.rto.RoAlbumInfoResult;
import com.SecretSquirrel.AndroidNoise.services.rto.RoAlbumListResult;
import com.SecretSquirrel.AndroidNoise.services.rto.RoArtist;
import com.SecretSquirrel.AndroidNoise.services.rto.RoArtistInfoResult;
import com.SecretSquirrel.AndroidNoise.services.rto.RoArtistListResult;
import com.SecretSquirrel.AndroidNoise.services.rto.RoArtistTracksResult;
import com.SecretSquirrel.AndroidNoise.services.rto.RoFavorite;
import com.SecretSquirrel.AndroidNoise.services.rto.RoFavoritesListResult;
import com.SecretSquirrel.AndroidNoise.services.rto.RoTrack;
import com.SecretSquirrel.AndroidNoise.services.rto.RoTrackListResult;
import com.SecretSquirrel.AndroidNoise.support.Constants;

import java.util.ArrayList;

import retrofit.RestAdapter;

// Secret Squirrel Software - Created by bswanson on 12/6/13.

public class NoiseDataService extends IntentService {
	private static final String     TAG = NoiseDataService.class.getName();

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
						long    forArtist = intent.getLongExtra( NoiseRemoteApi.ArtistId, Constants.NULL_ID );

						getAlbumList( forArtist, serverAddress, receiver );
						break;

					case NoiseRemoteApi.GetTrackList:
						long    forAlbum = intent.getLongExtra( NoiseRemoteApi.AlbumId, Constants.NULL_ID );

						getTrackList( forAlbum, serverAddress, receiver );
						break;

					case NoiseRemoteApi.GetFavoritesList:
						getFavoritesList( serverAddress, receiver );
						break;

					case NoiseRemoteApi.GetArtistInfo:
						forArtist = intent.getLongExtra( NoiseRemoteApi.ArtistId, Constants.NULL_ID );

						getArtistInfo( forArtist, serverAddress, receiver );
						break;

					case NoiseRemoteApi.GetAlbumInfo:
						forAlbum = intent.getLongExtra( NoiseRemoteApi.AlbumId, Constants.NULL_ID );

						getAlbumInfo( forAlbum, serverAddress, receiver );
						break;

					case NoiseRemoteApi.GetArtistTracks:
						forArtist = intent.getLongExtra( NoiseRemoteApi.ArtistId, Constants.NULL_ID );

						getArtistTracks( forArtist, serverAddress, receiver );
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
		Bundle  resultData = buildResultBundle( NoiseRemoteApi.GetArtistList );
		int     resultCode = NoiseRemoteApi.RemoteResultError;

		try {
			RemoteServerDataApi service = buildDataService( serverAddress );
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

			if( Constants.LOG_ERROR ) {
				Log.w( TAG, "getArtistList", ex );
			}
		}

		receiver.send( resultCode, resultData );
	}

	private void getAlbumList( long forArtist, String serverAddress, ResultReceiver receiver ) {
		Bundle  resultData = buildResultBundle( NoiseRemoteApi.GetAlbumList );
		int     resultCode = NoiseRemoteApi.RemoteResultError;

		try {
			RemoteServerDataApi service = buildDataService( serverAddress );
			RoAlbumListResult   result = service.GetAlbumList( forArtist );

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

			if( Constants.LOG_ERROR ) {
				Log.w( TAG, "getAlbumList", ex );
			}
		}

		receiver.send( resultCode, resultData );
	}

	private void getTrackList( long forAlbum, String serverAddress, ResultReceiver receiver ) {
		Bundle  resultData = buildResultBundle( NoiseRemoteApi.GetTrackList );
		int     resultCode = NoiseRemoteApi.RemoteResultError;

		try {
			RemoteServerDataApi service = buildDataService( serverAddress );
			RoTrackListResult   result = service.GetTrackList( forAlbum );

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

			if( Constants.LOG_ERROR ) {
				Log.w( TAG, "getTrackList", ex );
			}
		}

		receiver.send( resultCode, resultData );
	}

	private void getFavoritesList( String serverAddress, ResultReceiver receiver ) {
		Bundle  resultData = buildResultBundle( NoiseRemoteApi.GetFavoritesList );
		int     resultCode = NoiseRemoteApi.RemoteResultError;

		try {
			RemoteServerDataApi     service = buildDataService( serverAddress );
			RoFavoritesListResult   result = service.GetFavoritesList();

			if( result.Success ) {
				ArrayList<Favorite> favorites = new ArrayList<Favorite>();

				for( RoFavorite roFavorite : result.Favorites ) {
					favorites.add( new Favorite( roFavorite ) );
				}

				resultCode = NoiseRemoteApi.RemoteResultSuccess;
				resultData.putParcelableArrayList( NoiseRemoteApi.FavoritesList, favorites );
			}
			else {
				resultData.putString( NoiseRemoteApi.RemoteResultErrorMessage, result.ErrorMessage );
			}
		}
		catch( Exception ex ) {
			resultData.putString( NoiseRemoteApi.RemoteResultErrorMessage, ex.getMessage());
			resultCode = NoiseRemoteApi.RemoteResultException;

			if( Constants.LOG_ERROR ) {
				Log.w( TAG, "getFavoritesList", ex );
			}
		}

		receiver.send( resultCode, resultData );
	}

	private void getArtistInfo( long forArtist, String serverAddress, ResultReceiver receiver ) {
		Bundle  resultData = buildResultBundle( NoiseRemoteApi.GetArtistInfo );
		int     resultCode = NoiseRemoteApi.RemoteResultError;

		try {
			RemoteServerDataApi     service = buildDataService( serverAddress );
			RoArtistInfoResult      result = service.GetArtistInfo( forArtist );

			if( result.Success ) {
				resultCode = NoiseRemoteApi.RemoteResultSuccess;
				resultData.putParcelable( NoiseRemoteApi.ArtistInfo, new ArtistInfo( result.ArtistInfo ) );
			}
			else {
				resultData.putString( NoiseRemoteApi.RemoteResultErrorMessage, result.ErrorMessage );
			}
		}
		catch( Exception ex ) {
			resultData.putString( NoiseRemoteApi.RemoteResultErrorMessage, ex.getMessage());
			resultCode = NoiseRemoteApi.RemoteResultException;

			if( Constants.LOG_ERROR ) {
				Log.w( TAG, "getArtistInfo", ex );
			}
		}

		receiver.send( resultCode, resultData );
	}

	private void getAlbumInfo( long forAlbum, String serverAddress, ResultReceiver receiver ) {
		Bundle  resultData = buildResultBundle( NoiseRemoteApi.GetAlbumInfo );
		int     resultCode = NoiseRemoteApi.RemoteResultError;

		try {
			RemoteServerDataApi     service = buildDataService( serverAddress );
			RoAlbumInfoResult       result = service.GetAlbumInfo( forAlbum );

			if( result.Success ) {
				resultCode = NoiseRemoteApi.RemoteResultSuccess;
				resultData.putParcelable( NoiseRemoteApi.AlbumInfo, new AlbumInfo( result.AlbumInfo ));
			}
			else {
				resultData.putString( NoiseRemoteApi.RemoteResultErrorMessage, result.ErrorMessage );
			}
		}
		catch( Exception ex ) {
			resultData.putString( NoiseRemoteApi.RemoteResultErrorMessage, ex.getMessage());
			resultCode = NoiseRemoteApi.RemoteResultException;

			if( Constants.LOG_ERROR ) {
				Log.w( TAG, "getAlbumInfo", ex );
			}
		}

		receiver.send( resultCode, resultData );
	}

	private void getArtistTracks( long forArtist, String serverAddress, ResultReceiver receiver ) {
		Bundle  resultData = buildResultBundle( NoiseRemoteApi.GetArtistTracks );
		int     resultCode = NoiseRemoteApi.RemoteResultError;

		try {
			RemoteServerDataApi     service = buildDataService( serverAddress );
			RoArtistTracksResult    result = service.GetArtistTracks( forArtist );

			if( result.Success ) {
				resultCode = NoiseRemoteApi.RemoteResultSuccess;
//				resultData.putParcelable( NoiseRemoteApi.ArtistTrackList, result.Tracks );
			}
			else {
				resultData.putString( NoiseRemoteApi.RemoteResultErrorMessage, result.ErrorMessage );
			}
		}
		catch( Exception ex ) {
			resultData.putString( NoiseRemoteApi.RemoteResultErrorMessage, ex.getMessage());
			resultCode = NoiseRemoteApi.RemoteResultException;

			if( Constants.LOG_ERROR ) {
				Log.w( TAG, "getArtistTracks", ex );
			}
		}

		receiver.send( resultCode, resultData );
	}

	private Bundle buildResultBundle( int apiCode ) {
		Bundle  retValue = new Bundle();

		retValue.putInt( NoiseRemoteApi.RemoteApiParameter, apiCode );

		return( retValue );
	}

	private RemoteServerDataApi buildDataService( String serverAddress ) {
		RestAdapter restAdapter = new RestAdapter.Builder().setServer( serverAddress ).build();

		return( restAdapter.create( RemoteServerDataApi.class ));
	}
}
