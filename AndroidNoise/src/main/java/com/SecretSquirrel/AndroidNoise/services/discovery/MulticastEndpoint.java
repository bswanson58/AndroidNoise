package com.SecretSquirrel.AndroidNoise.services.discovery;

// Secret Squirrel Software - Created by BSwanson on 7/18/14.

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import timber.log.Timber;

public class MulticastEndpoint {
	private Context         mContext;
	private int             mMulticastPort;
	private String          mMulticastAddress;

	public MulticastEndpoint( Context context, String multicastAddress, int multicastPort ) throws IllegalArgumentException {
		if(context == null || multicastPort <= 1024 || multicastPort > 49151 ) {
			throw new IllegalArgumentException();
		}

		mContext = context.getApplicationContext();
		mMulticastAddress = multicastAddress;
		mMulticastPort = multicastPort;
	}

	public boolean sendMessage( String message ) throws IllegalArgumentException {
		if( message == null || message.length() == 0 ) {
			throw new IllegalArgumentException();
		}

		// Check for WiFi connectivity
		ConnectivityManager connManager = (ConnectivityManager)mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
		NetworkInfo         mWifi = connManager.getNetworkInfo( ConnectivityManager.TYPE_WIFI );

		if(( mWifi == null ) ||
		   (!mWifi.isConnected())) {
			Timber.d( "You must to be in a WiFi network in order to send UDP multicast packets. Aborting." );

			return( false );
		}

		MulticastSocket socket;
		try {
			socket = new MulticastSocket( mMulticastPort );

			socket.setTimeToLive( 5 );
		}
		catch( Exception ex ) {
			Timber.e( ex, "Multicast socket could not be created." );

			return( false );
		}

		DatagramPacket  packet;
		byte            data[];

		try {
			data = message.getBytes( "UTF-8" );
		}
		catch( UnsupportedEncodingException ex ) {
			Timber.e( ex, "Could not encode multicast message." );

			return( false );
		}

		try {
			packet = new DatagramPacket( data, data.length, InetAddress.getByName( mMulticastAddress ), mMulticastPort );
		} catch( UnknownHostException ex ) {
			Timber.e( ex, "IP Address " + mMulticastAddress + " is not a valid IP.");

			return( false );
		}

		try {
			socket.send( packet );

			socket.close();
		} catch( IOException ex ) {
			Timber.e( ex, "There was an error sending the UDP multicast.");

			return( false );
		}

		return( true );
	}
}
