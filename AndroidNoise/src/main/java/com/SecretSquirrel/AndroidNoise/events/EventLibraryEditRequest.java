package com.SecretSquirrel.AndroidNoise.events;

// Secret Squirrel Software - Created by Bswanson on 5/16/14.

import com.SecretSquirrel.AndroidNoise.dto.Library;
import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;

import retrofit.Server;

public class EventLibraryEditRequest {
	private final ServerInformation mServerInformation;
	private final Library           mLibrary;
	private final int               mAction;

	public EventLibraryEditRequest( ServerInformation serverInformation, Library library, int action ) {
		mServerInformation = serverInformation;
		mLibrary = library;
		mAction = action;
	}

	public ServerInformation getServerInformation() {
		return( mServerInformation );
	}

	public Library getLibrary() {
		return( mLibrary );
	}

	public int getAction() {
		return( mAction );
	}
}
