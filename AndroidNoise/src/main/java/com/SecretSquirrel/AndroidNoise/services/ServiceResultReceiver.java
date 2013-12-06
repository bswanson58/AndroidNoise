package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 12/6/13.

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class ServiceResultReceiver extends ResultReceiver {
	public interface Receiver {
		public void onReceiveResult( int resultCode, Bundle resultData );
	}

	private Receiver mReceiver;

	public ServiceResultReceiver( Handler handler ) {
		super( handler );
	}

	public void setReceiver( Receiver receiver ) {
		mReceiver = receiver;
	}

	public void clearReceiver() {
		mReceiver = null;
	}

	@Override
	protected void onReceiveResult( int resultCode, Bundle resultData ) {
		if( mReceiver != null ) {
			mReceiver.onReceiveResult( resultCode, resultData );
		}
	}
}
