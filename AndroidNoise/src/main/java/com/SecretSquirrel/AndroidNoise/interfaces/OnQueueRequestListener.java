package com.SecretSquirrel.AndroidNoise.interfaces;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Track;

public interface OnQueueRequestListener {
	public  void    PlayAlbum( Album album );
	public  void    PlayTrack( Track track );
}
