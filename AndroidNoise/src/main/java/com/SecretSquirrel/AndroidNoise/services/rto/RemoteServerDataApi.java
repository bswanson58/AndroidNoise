package com.SecretSquirrel.AndroidNoise.services.rto;

// Secret Squirrel Software - Created by bswanson on 12/11/13.

import retrofit.http.GET;
import retrofit.http.Query;

public interface RemoteServerDataApi {
	@GET( "/Data/artists" )
	RoArtistListResult      GetArtistList();

	@GET( "/Data/albums" )
	RoAlbumListResult       GetAlbumList( @Query("artist") long forArtist );

	@GET( "/Data/tracks" )
	RoTrackListResult       GetTrackList( @Query("album")  long forAlbum );

	@GET( "/Data/favorites" )
	RoFavoritesListResult GetFavoritesList();
}
