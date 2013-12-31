package com.SecretSquirrel.AndroidNoise.support;

// Secret Squirrel Software - Created by bswanson on 12/31/13.

import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class NetworkUtility {
	private static final String     TAG = NetworkUtility.class.getName();

	public static String getLocalAddress() {
		String  retValue = "";

		try {
			for( Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
				NetworkInterface networkInterface = en.nextElement();

				for( Enumeration<InetAddress> internetAddress = networkInterface.getInetAddresses(); internetAddress.hasMoreElements(); ) {
					InetAddress inetAddress = internetAddress.nextElement();

					//if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress() ) {
					if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address( inetAddress.getHostAddress())) {
						retValue = inetAddress.getHostAddress();
					}
				}
			}
		} catch( SocketException ex ) {
			Log.d( TAG, ex.toString());
		}

		return( retValue );
	}

	/**
	 * Get IP address from first non-localhost interface
	 * @param useIPv4  true=return ipv4, false=return ipv6
	 * @return  address or empty string
	 */
	public static String getIPAddress(boolean useIPv4) {
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list( intf.getInetAddresses() );
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress().toUpperCase();
						boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
						if (useIPv4) {
							if (isIPv4)
								return sAddr;
						} else {
							if (!isIPv4) {
								int delim = sAddr.indexOf('%'); // drop ip6 port suffix
								return delim<0 ? sAddr : sAddr.substring(0, delim);
							}
						}
					}
				}
			}
		} catch (Exception ex) { } // for now eat exceptions
		return "";
	}}
