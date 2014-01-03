package com.SecretSquirrel.AndroidNoise.services.rto;

// Secret Squirrel Software - Created by bswanson on 1/3/14.

import javax.jmdns.ServiceInfo;

public class ServiceInformation {
	public enum ServiceState {
		ServiceAdded,
		ServiceResolved,
		ServiceDeleted
	}

	private ServiceInfo     mServiceInfo;
	private ServiceState    mServiceState;

	public ServiceInformation( ServiceState state, ServiceInfo serviceInfo ) {
		mServiceState = state;
		mServiceInfo = serviceInfo;
	}

	public ServiceState getServiceState() {
		return( mServiceState );
	}

	public String getName() {
		String  retValue = "";

		if( mServiceInfo !=  null ) {
			retValue = mServiceInfo.getName();
		}

		return( retValue );
	}

	public String getHostName() {
		String  retValue = "";

		if( mServiceInfo != null ) {
			String  server = mServiceInfo.getServer();
			String  domain = mServiceInfo.getDomain();

			if( server.contains( domain )) {
				server = server.replace( domain, "" );
			}
			if( server.startsWith( "." )) {
				server = server.substring( 1 );
			}
			while( server.endsWith( "." )) {
				server = server.substring( 0, server.length() - 1 );
			}

			retValue = server;
		}
		return( retValue );
	}

	public String getHostAddress() {
		String  retValue = "";

		if( mServiceInfo != null ) {
			retValue = mServiceInfo.getURLs()[0];
		}

		return( retValue );
	}
}
