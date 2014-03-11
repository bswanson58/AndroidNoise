package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by Bswanson on 3/11/14.

import com.SecretSquirrel.AndroidNoise.services.rto.RoAudioState;

public class AudioState {
	private int     mVolumeLevel;

	public AudioState( RoAudioState roState ) {
		mVolumeLevel = roState.VolumeLevel;
	}

	public AudioState() {
		mVolumeLevel = -1;
	}

	public int getVolumeLevel() {
		return( mVolumeLevel );
	}

	public void setVolumeLevel( int level ) {
		mVolumeLevel = level;
	}

	public RoAudioState asRoAudioState() {
		RoAudioState    retValue = new RoAudioState();

		retValue.VolumeLevel = mVolumeLevel;

		return( retValue );
	}
}
