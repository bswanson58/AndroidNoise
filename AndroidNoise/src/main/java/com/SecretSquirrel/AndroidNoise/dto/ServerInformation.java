package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 12/9/13.

import com.SecretSquirrel.AndroidNoise.services.rto.ServiceInformation;

public class ServerInformation {
	private ServiceInformation.ServiceState mServiceState;
	private String                          mServerAddress;
	private String                          mHostName;
	private String                          mServerName;
	private ServerVersion                   mServerVersion;

	public ServerInformation( ServiceInformation serviceInformation, ServerVersion version ) {
		if( serviceInformation != null ) {
			mServiceState = serviceInformation.getServiceState();
			mServerAddress = serviceInformation.getHostAddress();
			mServerName = serviceInformation.getName();
			mHostName = serviceInformation.getHostName();
		}

		mServerVersion = version;
	}

	public ServiceInformation.ServiceState getServiceState() {
		return( mServiceState );
	}

	public String getHostName() {
		return( mHostName );
	}

	public String getServerAddress() {
		return( mServerAddress );
	}

	public String getServerName() {
		return( mServerName );
	}

	public ServerVersion getServerVersion() {
		return( mServerVersion );
	}
}
