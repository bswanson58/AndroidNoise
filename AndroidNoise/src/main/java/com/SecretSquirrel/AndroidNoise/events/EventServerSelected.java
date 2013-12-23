package com.SecretSquirrel.AndroidNoise.events;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;

public class EventServerSelected {
	private ServerInformation   mServer;

	public EventServerSelected( ServerInformation server ) {
		mServer = server;
	}

	public ServerInformation getServer() {
		return mServer;
	}
}
