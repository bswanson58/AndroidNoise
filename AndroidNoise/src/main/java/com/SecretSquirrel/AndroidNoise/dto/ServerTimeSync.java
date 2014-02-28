package com.SecretSquirrel.AndroidNoise.dto;

// Created by BSwanson on 2/27/14.

import com.SecretSquirrel.AndroidNoise.services.rto.RoTimeSync;

public class ServerTimeSync {
	private long    mClientSentTime;
	private long    mServerReceivedTime;
	private long    mClientReceivedTime;

	public ServerTimeSync( RoTimeSync roTime ) {
		mClientSentTime = roTime.ClientTimeSent;
		mServerReceivedTime = roTime.ServerTimeReceived;

		mClientReceivedTime = System.currentTimeMillis();
	}

	public long getSendTime() {
		return( mServerReceivedTime - mClientSentTime );
	}

	public long getReceiveTime() {
		return( mClientReceivedTime - mServerReceivedTime );
	}

	public long getTimeDifference() {
		long    difference = ( Math.abs( getSendTime()) + Math.abs( getReceiveTime())) / 2;

		if( getSendTime() < 0 ) {
			difference = 0 - difference;
		}

		return( difference );
	}
}
