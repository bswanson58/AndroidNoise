package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 12/6/13.

import android.support.v7.appcompat.R;

public class NoiseRemoteApi {
	// Identification of the server method to be called.
	public static final String  RemoteApiParameter          = "remoteApiParameter";

	// Server methods and return value keys.
	public static final int     GetServerVersion            = 1;
	public static final String  RemoteResultVersion         = "remoteResultVersion";

	public static final int     GetServerInformation        = 2;
	public static final String  ServerInformation           = "serverInformation";

	// Data methods and parameter/return keys.
	public static final int     GetArtistList               = 3;
	public static final String  ArtistList                  = "artistList";

	public static final int     GetAlbumList                = 4;
	public static final String  ArtistId                    = "artistId";
	public static final String  AlbumList                   = "albumList";

	public static final int     GetTrackList                = 5;
	public static final String  AlbumId                     = "albumId";
	public static final String  TrackList                   = "trackList";

	public static final int     GetFavoritesList            = 6;
	public static final String  FavoritesList               = "favoritesList";

	public static final int     GetArtistInfo               = 7;
	public static final String  ArtistInfo                  = "artistInfo";

	public static final int     GetAlbumInfo                = 8;
	public static final String  AlbumInfo                   = "albumInfo";

	public static final int     GetArtistTracks             = 9;
	public static final String  ArtistTrackList             = "artistTrackList";

	public static final int     GetPlayHistory              = 10;
	public static final String  PlayHistoryList             = "playHistoryList";

	public static final String  Artist                      = "artist";
	public static final String  Album                       = "album";

	// Locator methods and parameter/return value keys.
	public static final int     LocateServices              = 1;
	public static final String  LocateServicesType          = "locateServicesType";
	public static final String  LocateServicesList          = "locateServicesList";

	// Parameter keys to complete the remote api call.
	public static final String  RemoteServerAddress         = "remoteServerAddress";
	public static final String  RemoteCallReceiver          = "remoteCallReceiver";

	// Primary result codes and values common value codes.
	public static final int     RemoteResultSuccess         = 1;
	public static final int     RemoteResultError           = 2;
	public static final int     RemoteResultException       = 3;

	public static final String  RemoteResultErrorMessage    = "remoteErrorMessage";

}
