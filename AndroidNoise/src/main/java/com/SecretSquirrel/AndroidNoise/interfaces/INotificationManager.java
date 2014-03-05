package com.SecretSquirrel.AndroidNoise.interfaces;

// Secret Squirrel Software - Created by bswanson on 2/20/14.

import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Track;

public interface INotificationManager {
	void    NotifyItemQueued( String itemName );
	void    NotifyItemQueued( String itemName, String errorMessage );
	void    NotifyItemQueued( Track track );
	void    NotifyItemQueued( Track track, String errorMessage );
	void    NotifyItemQueued( Album album );
	void    NotifyItemQueued( Album album, String errorMessage );
}
