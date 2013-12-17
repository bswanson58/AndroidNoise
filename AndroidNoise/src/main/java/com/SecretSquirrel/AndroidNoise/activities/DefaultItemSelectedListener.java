package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.dto.Track;
import com.SecretSquirrel.AndroidNoise.interfaces.OnItemSelectedListener;

public class DefaultItemSelectedListener implements OnItemSelectedListener {
	@Override
	public void OnArtistSelected( Artist artist ) {	}

	@Override
	public void OnAlbumSelected( Album album ) { }

	@Override
	public void OnTrackSelected( Track track ) { }
}
