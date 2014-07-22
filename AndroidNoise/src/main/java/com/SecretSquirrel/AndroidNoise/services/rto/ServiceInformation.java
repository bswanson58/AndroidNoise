package com.SecretSquirrel.AndroidNoise.services.rto;

// Secret Squirrel Software - Created by BSwanson on 1/3/14.

public class ServiceInformation {
	public enum ServiceState {
		ServiceAdded,
		ServiceResolved,
		ServiceDeleted
	}

	private ServiceState    mServiceState;
	private String          mServiceName;
	private String          mServiceAddress;

	public ServiceInformation( ServiceState state, String serviceName, String serviceAddress ) {
		mServiceState = state;
		mServiceName = serviceName;
		mServiceAddress = serviceAddress;
	}

	public ServiceState getServiceState() {
		return( mServiceState );
	}

	public String getName() {
		return( mServiceName );
	}

	public String getHostAddress() {
		return( mServiceAddress );
	}
}
