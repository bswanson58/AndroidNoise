package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 12/31/13.

import android.os.AsyncTask;

import com.SecretSquirrel.AndroidNoise.nanoHttpd.NanoHTTPD;

import java.util.ArrayList;

import timber.log.Timber;

public class ServerEventHost extends NanoHTTPD {

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
			Timber.e( ex, "Starting ServerEventHost" );
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
