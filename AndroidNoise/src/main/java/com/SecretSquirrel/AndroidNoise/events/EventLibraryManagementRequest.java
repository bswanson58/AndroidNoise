package com.SecretSquirrel.AndroidNoise.events;

// Created by BSwanson on 3/6/14.

import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;

public class EventLibraryManagementRequest {
	private final ServerInformation mServerInformation;

	public EventLibraryManagementRequest( ServerInformation serverInformation ) {
		mServerInformation = serverInformation;
	}

	public EventLibraryManagementRequest() {
		mServerInformation = null;
	}

	public ServerInformation getServerInformation() {
		return( mServerInformation );
	}

	public boolean getCloseRequest() {
		return( mServerInformation == null );
	}
}
