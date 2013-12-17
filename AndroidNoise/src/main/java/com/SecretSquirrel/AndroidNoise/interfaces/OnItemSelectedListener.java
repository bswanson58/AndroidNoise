package com.SecretSquirrel.AndroidNoise.interfaces;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.dto.Track;

public interface OnItemSelectedListener {
	public  void    OnArtistSelected( Artist artist );
	public  void    OnAlbumSelected( Album album );
	public  void    OnTrackSelected( Track track );
}
