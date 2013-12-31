package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 12/31/13.

import android.os.AsyncTask;
import android.util.Log;

import com.SecretSquirrel.AndroidNoise.events.EventServerQueueChanged;
import com.SecretSquirrel.AndroidNoise.nanoHttpd.NanoHTTPD;
import com.SecretSquirrel.AndroidNoise.support.Constants;

import de.greenrobot.event.EventBus;

public class ServerEventHost extends NanoHTTPD {
	private static final String     TAG = ServerEventHost.class.getName();

	public ServerEventHost( int port ) {
		super( port );
	}

	@Override
	public void start() {
		new AsyncTask<Object, Object, String>() {
			@Override
			protected String doInBackground( Object... params ) {
				startServer();

				return "Executed";
			}
		}.execute( null );

	}

	private void startServer() {
		try {
			super.start();
		}
		catch( Exception ex ) {
			if( Constants.LOG_ERROR ) {
				Log.e( TAG, "Starting ServerEventHost", ex );
			}
		}
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
