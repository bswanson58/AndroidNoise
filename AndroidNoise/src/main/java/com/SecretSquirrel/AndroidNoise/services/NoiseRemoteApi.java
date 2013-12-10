package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 12/6/13.

import android.support.v7.appcompat.R;

public class NoiseRemoteApi {
	// Identification of the server method to be called.
	public static final String  RemoteApiParameter          = "remoteApiParameter";

	// Server methods and return value keys.
	public static final int     GetServerVersion            = 1;
	public static final String  RemoteResultVersion         = "remoteResultVersion";

	// Locator methods and parameter/return value keys.
	public static final int     LocateServices              = 1;
	public static final String  LocateServicesType          = "locateServicesType";
	public static final String  LocateServicesList          = "locateServicesList";

	// Parameter keys to complete the remote api call.
	public static final String  RemoteServerAddress         = "remoteServerAddress";
	public static final String  RemoteCallReceiver          = "remoteCallReceiver";

	// Primary result codes and values common value codes.
	public static final int     RemoteResultSuccess         = 1;
	public static final int     RemoteResultError           = 2;
	public static final int     RemoteResultException       = 3;

	public static final String  RemoteResultErrorMessage    = "remoteErrorMessage";

}
