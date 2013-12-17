package com.SecretSquirrel.AndroidNoise.interfaces;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

public interface INoiseQueue {
	public  void    EnqueueTrack( long trackId );
	public  void    EnqueueAlbum( long albumId );
}
