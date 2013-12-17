package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import android.content.Context;

import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;

public class NoiseQueueClient implements INoiseQueue {
	private Context     mContext;
	private String      mServerAddress;

	public NoiseQueueClient( Context context, String serverAddress ) {
		mContext = context;
		mServerAddress = serverAddress;
	}

	@Override
	public void EnqueueTrack( long trackId ) {

	}

	@Override
	public void EnqueueAlbum( long albumId ) {

	}
}
