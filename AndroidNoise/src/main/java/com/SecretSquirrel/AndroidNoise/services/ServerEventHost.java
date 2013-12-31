package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 12/31/13.

import com.SecretSquirrel.AndroidNoise.events.EventServerQueueChanged;
import com.SecretSquirrel.AndroidNoise.nanoHttpd.NanoHTTPD;

import de.greenrobot.event.EventBus;

public class ServerEventHost extends NanoHTTPD {

	public ServerEventHost( int port ) {
		super( port );
	}

	@Override
	public Response serve( IHTTPSession session ) {
		String  uri = session.getUri();

		if( uri.contains( "/eventInQueue" )) {
			EventBus.getDefault().post( new EventServerQueueChanged());
		}

		return( new Response( "Queue event received." ));
	}
}
