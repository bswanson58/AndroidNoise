package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 12/30/13.

import com.SecretSquirrel.AndroidNoise.services.rto.RoPlayQueueListResult;
import com.SecretSquirrel.AndroidNoise.services.rto.RoPlayQueueTrack;

import java.util.ArrayList;

public class PlayQueueListResult {
	private boolean                     mSuccess;
	private String                      mErrorMessage;
	private ArrayList<PlayQueueTrack>   mTracks;

	public PlayQueueListResult( RoPlayQueueListResult roResult ) {
		mTracks = new ArrayList<PlayQueueTrack>();
		mSuccess = roResult.Success;
		mErrorMessage = roResult.ErrorMessage;

		if( roResult.Success ) {
			for( RoPlayQueueTrack roTrack : roResult.Tracks ) {
				mTracks.add( new PlayQueueTrack( roTrack ));
			}
		}
	}

	public boolean getSuccess() {
		return( mSuccess );
	}

	public String getErrorMessage() {
		return( mErrorMessage );
	}

	public ArrayList<PlayQueueTrack> getTracks() {
		return( mTracks );
	}
}
