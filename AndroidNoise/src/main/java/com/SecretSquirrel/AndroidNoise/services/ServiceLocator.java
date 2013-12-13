package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 12/9/13.

import android.app.IntentService;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

public class ServiceLocator extends IntentService {
	private static final String TAG = ServiceLocator.class.getName();

	private WifiManager.MulticastLock   mWiFiLock;
	private JmDNS                       mDiscoveryService;
	private ServiceListener             mServiceListener;

	public ServiceLocator() {
		super( "ServiceLocator" );
	}

	@Override
	protected void onHandleIntent( Intent intent ) {
		int             callId = intent.getIntExtra( NoiseRemoteApi.RemoteApiParameter, 0 );
		ResultReceiver  receiver = intent.getParcelableExtra( NoiseRemoteApi.RemoteCallReceiver );

		if( receiver != null ) {
			switch( callId ) {
				case NoiseRemoteApi.LocateServices:
					String  servicesType  = intent.getStringExtra( NoiseRemoteApi.LocateServicesType );

					locateServices( servicesType, receiver );
					break;
			}
		}
	}

	private void locateServices( String ofType, ResultReceiver receiver ) {
		Bundle              resultData = new Bundle();
		int                 resultCode = NoiseRemoteApi.RemoteResultError;
		String[]            serverList = new String[1];

		serverList[0] = "http://10.1.1.139:88/Noise";

		resultData.putStringArray( NoiseRemoteApi.LocateServicesList, serverList );
		resultCode = NoiseRemoteApi.RemoteResultSuccess;

		receiver.send( resultCode, resultData );
	}

	private void locateServices2( String ofType, ResultReceiver receiver ) {
		WifiManager wifi = (WifiManager) getSystemService( android.content.Context.WIFI_SERVICE );

		mWiFiLock = wifi.createMulticastLock( "NoiseServiceDiscoveryLock" );
		mWiFiLock.setReferenceCounted( true );
		mWiFiLock.acquire();

		try {
			mDiscoveryService = JmDNS.create( getDeviceIpAddress( wifi ), "NoiseRemote" );

			javax.jmdns.ServiceInfo[] services = mDiscoveryService.list( ofType );

			mDiscoveryService.close();
		} catch( IOException e ) {
			e.printStackTrace();
		}
		finally {
			if( mWiFiLock != null ) {
				mWiFiLock.release();
			}
		}
	}

	/**
	 * Gets the current Android device IP address or return 10.0.0.2 which is localhost on Android.
	 * <p>
	 * @return the InetAddress of this Android device
	 */
	private InetAddress getDeviceIpAddress(WifiManager wifi) {
		InetAddress result = null;
		try {
			// default to Android localhost
			result = InetAddress.getByName("10.0.0.2");

			// figure out our wifi address, otherwise bail
			WifiInfo wifiinfo = wifi.getConnectionInfo();
			int intaddr = wifiinfo.getIpAddress();
			byte[] byteaddr = new byte[] { (byte) (intaddr & 0xff), (byte) (intaddr >> 8 & 0xff), (byte) (intaddr >> 16 & 0xff), (byte) (intaddr >> 24 & 0xff) };
			result = InetAddress.getByAddress(byteaddr);
		} catch (UnknownHostException ex) {
			Log.w( "ServiceLocator", String.format("getDeviceIpAddress Error: %s", ex.getMessage()));
		}

		return result;
	}

	/*
	private final String                    SERVICE_TYPE = "_Noise._Tcp.local.";
	private final String                    mServiceName = "Noise.Desktop";
	private NsdManager                      mNsdManager;
	private NsdManager.DiscoveryListener    mDiscoveryListener;
	private NsdManager.ResolveListener      mResolveListener;
	private NsdServiceInfo                  mService;

	private void locateServices3() {
		initializeDiscoveryListener();

		mNsdManager.discoverServices( SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener );
	}

	public void initializeDiscoveryListener() {

		// Instantiate a new DiscoveryListener
		mDiscoveryListener = new NsdManager.DiscoveryListener() {

			//  Called as soon as service discovery begins.
			@Override
			public void onDiscoveryStarted(String regType) {
				Log.d(TAG, "Service discovery started");
			}

			@Override
			public void onServiceFound(NsdServiceInfo service) {
				// A service was found!  Do something with it.
				Log.d(TAG, "Service discovery success" + service);
				if (!service.getServiceType().equals(SERVICE_TYPE)) {
					// Service type is the string containing the protocol and
					// transport layer for this service.
					Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
				} else if (service.getServiceName().equals(mServiceName)) {
					// The name of the service tells the user what they'd be
					// connecting to. It could be "Bob's Chat App".
					Log.d(TAG, "Same machine: " + mServiceName);
				} else if (service.getServiceName().contains("NsdChat")){
					mNsdManager.resolveService(service, mResolveListener);
				}
			}

			@Override
			public void onServiceLost(NsdServiceInfo service) {
				// When the network service is no longer available.
				// Internal bookkeeping code goes here.
				Log.e(TAG, "service lost" + service);
			}

			@Override
			public void onDiscoveryStopped(String serviceType) {
				Log.i(TAG, "Discovery stopped: " + serviceType);
			}

			@Override
			public void onStartDiscoveryFailed(String serviceType, int errorCode) {
				Log.e(TAG, "Discovery failed: Error code:" + errorCode);
				mNsdManager.stopServiceDiscovery(this);
			}

			@Override
			public void onStopDiscoveryFailed(String serviceType, int errorCode) {
				Log.e(TAG, "Discovery failed: Error code:" + errorCode);
				mNsdManager.stopServiceDiscovery(this);
			}
		};

		mResolveListener = new NsdManager.ResolveListener() {

			@Override
			public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
				// Called when the resolve fails.  Use the error code to debug.
				Log.e(TAG, "Resolve failed" + errorCode);
			}

			@Override
			public void onServiceResolved(NsdServiceInfo serviceInfo) {
				Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

				if (serviceInfo.getServiceName().equals(mServiceName)) {
					Log.d(TAG, "Same IP.");
					return;
				}

				mService = serviceInfo;
				int port = mService.getPort();
				InetAddress host = mService.getHost();
			}
		};
	} */
}
