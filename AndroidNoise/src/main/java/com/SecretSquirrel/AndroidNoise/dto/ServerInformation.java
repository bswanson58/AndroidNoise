package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 12/9/13.

import com.SecretSquirrel.AndroidNoise.services.rto.ServiceInformation;
import com.SecretSquirrel.AndroidNoise.support.Constants;

public class ServerInformation {
	private ServiceInformation.ServiceState mServiceState;
	private String                          mServerAddress;
	private String                          mHostName;
	private String                          mServerName;
	private ServerVersion                   mServerVersion;
	private int                             mServerApiVersion;
	private String                          mLibraryName;
	private long                            mLibraryId;

	public ServerInformation( ServiceInformation serviceInformation, ServerVersion version ) {
		mServerApiVersion = 1;

		mLibraryId = Constants.NULL_ID;
		mLibraryName = "";

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

	public int getServerApiVersion() {
		return( mServerApiVersion );
	}

	public long getLibraryId() {
		return( mLibraryId );
	}

	public String getLibraryName() {
		return( mLibraryName );
	}
}
