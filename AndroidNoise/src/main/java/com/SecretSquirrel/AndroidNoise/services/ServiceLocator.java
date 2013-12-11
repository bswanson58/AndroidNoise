package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 12/9/13.

import android.app.IntentService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

public class ServiceLocator extends IntentService {
	private WifiManager                 wifi;
	private WifiManager.MulticastLock   lock;
	private JmDNS                       jmdns;

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

					locateServices2( servicesType, receiver );
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
		wifi = (WifiManager) getSystemService( android.content.Context.WIFI_SERVICE );
		lock = wifi.createMulticastLock( "lock" );
		lock.setReferenceCounted( true );
		lock.acquire();

		try {
			jmdns = JmDNS.create();

			jmdns.addServiceListener( ofType,
					new ServiceListener() {
						@Override
						public void serviceResolved( ServiceEvent ev ) {
							listService("\nService resolved: "
									+ ev.getInfo().getQualifiedName()
									+ "\nip: "
									+ ev.getInfo().getInet4Addresses()[0].getHostAddress() + "\nport: "
									+ ev.getInfo().getPort());

						}

						@Override
						public void serviceRemoved(ServiceEvent ev) {
							listService("\nService removed: " + ev.getName());
						}

						@Override
						public void serviceAdded(ServiceEvent event) {
							jmdns.requestServiceInfo(event.getType(), event.getName(), 1);
						}
					});
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	private void listService( final String msg ) {
	}
}
