package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 12/31/13.

import android.os.AsyncTask;
import android.util.Log;

import com.SecretSquirrel.AndroidNoise.nanoHttpd.NanoHTTPD;
import com.SecretSquirrel.AndroidNoise.support.Constants;

import java.util.ArrayList;

public class ServerEventHost extends NanoHTTPD {
	private static final String     TAG = ServerEventHost.class.getName();

	public interface UriResponder {
		boolean     shouldServe( IHTTPSession session );

		Response    serve( IHTTPSession session );
	}

	private ArrayList<UriResponder> mResponders;

	public ServerEventHost( int port ) {
		super( port );

		mResponders = new ArrayList<UriResponder>();
	}

	public void AddResponder( UriResponder responder ) {
		mResponders.add( responder );
	}

	@Override
	public void start() {
		new AsyncTask<Object, Object, String>() {
			@Override
			protected String doInBackground( Object... params ) {
				startServer();

				return "Executed";
			}
		}.execute();

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
		Response    retValue = new Response( "Unhandled request: " + session.getUri());

		for( UriResponder responder : mResponders ) {
			if( responder.shouldServe( session )) {
				retValue = responder.serve( session );

				break;
			}
		}

		return( retValue );
	}
}
