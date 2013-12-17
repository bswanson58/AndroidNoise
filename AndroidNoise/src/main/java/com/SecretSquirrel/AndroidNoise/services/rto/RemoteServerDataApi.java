package com.SecretSquirrel.AndroidNoise.services.rto;

// Secret Squirrel Software - Created by bswanson on 12/11/13.

import retrofit.http.GET;
import retrofit.http.Path;

public interface RemoteServerDataApi {
	@GET( "/Data/artists" )
	RoArtistListResult  GetArtistList();

	@GET( "/Data/albums?artist={artistId}" )
	RoAlbumListResult   GetAlbumList( @Path("artistId") long forArtist );

	@GET( "/Data/tracks?album={albumId}" )
	RoTrackListResult   GetTrackList( @Path("albumId")  long forAlbum );
}
