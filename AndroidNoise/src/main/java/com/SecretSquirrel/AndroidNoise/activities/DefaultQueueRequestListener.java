package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Track;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.OnQueueRequestListener;

public class DefaultQueueRequestListener implements OnQueueRequestListener {
	private IApplicationState   mApplicationState;

	public DefaultQueueRequestListener( IApplicationState applicationState ) {
		mApplicationState = applicationState;
	}

	@Override
	public void PlayAlbum( Album album ) {
		if( album != null ) {
			mApplicationState.getQueueClient().EnqueueAlbum( album.AlbumId );
		}
	}

	@Override
	public void PlayTrack( Track track ) {
		if( track != null ) {
			mApplicationState.getQueueClient().EnqueueTrack( track.TrackId );
		}
	}
}
