package com.SecretSquirrel.AndroidNoise.services.rto;

// Secret Squirrel Software - Created by bswanson on 12/11/13.

import retrofit.http.GET;
import retrofit.http.Query;

public interface RemoteServerDataApi {
	@GET( "/Noise/Data/artists" )
	RoArtistListResult      GetArtistList();

	@GET( "/Noise/Data/artist" )
	RoArtistInfoResult      GetArtistInfo( @Query( "artist" ) long forArtist );

	@GET( "/Noise/Data/albums" )
	RoAlbumListResult       GetAlbumList( @Query("artist") long forArtist );

	@GET( "/Noise/Data/album" )
	RoAlbumInfoResult       GetAlbumInfo( @Query( "album" ) long forAlbum );

	@GET( "/Noise/Data/tracks" )
	RoTrackListResult       GetTrackList( @Query("album")  long forAlbum );

	@GET( "/Noise/Data/favorites" )
	RoFavoritesListResult GetFavoritesList();
}
