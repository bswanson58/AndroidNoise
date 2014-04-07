package com.SecretSquirrel.AndroidNoise.interfaces;

// Secret Squirrel Software - Created by BSwanson on 12/11/13.

import android.os.ResultReceiver;

public interface INoiseData {
	public  void    GetArtistList( ResultReceiver receiver );
	public  void    GetArtistInfo( long forArtist, ResultReceiver receiver );
	public  void    GetAlbumList( long forArtist, ResultReceiver receiver );
	public  void    GetAlbumInfo( long forAlbum, ResultReceiver receiver );
	public  void    GetTrackList( long forAlbum, ResultReceiver receiver );
	public  void    GetFavoritesList( ResultReceiver receiver );
	public  void    GetArtistTracks( long forArtist, ResultReceiver receiver );
	public  void    GetPlayHistory( ResultReceiver receiver );
}
