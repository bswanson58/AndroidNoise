package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 12/31/13.

import com.SecretSquirrel.AndroidNoise.nanoHttpd.NanoHTTPD;

public class ServerEventHost extends NanoHTTPD {

	public ServerEventHost( int port ) {
		super( port );
	}

	@Override
	public Response serve( IHTTPSession session ) {
		return super.serve( session );
	}
}
