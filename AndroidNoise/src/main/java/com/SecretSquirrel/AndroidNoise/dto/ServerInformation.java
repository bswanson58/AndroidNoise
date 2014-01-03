package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 12/9/13.

import android.os.Parcel;
import android.os.Parcelable;

import com.SecretSquirrel.AndroidNoise.services.rto.ServiceInformation;

public class ServerInformation implements Parcelable {
	private String          mServerAddress;
	private String          mHostName;
	private String          mServerName;
	private ServerVersion   mServerVersion;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<ServerInformation> CREATOR = new Parcelable.Creator<ServerInformation>() {
		public ServerInformation createFromParcel(Parcel pc) {
			return new ServerInformation( pc );
		}
		public ServerInformation[] newArray(int size) {
			return new ServerInformation[size];
		}
	};

	public ServerInformation( String serverAddress, ServerVersion serverVersion ) {
		mServerAddress = serverAddress;
		mServerVersion = serverVersion;
	}

	public ServerInformation( Parcel parcel ) {
		mServerAddress = parcel.readString();
		mServerVersion = parcel.readParcelable( ServerVersion.class.getClassLoader());
	}

	public ServerInformation( ServiceInformation serviceInformation, ServerVersion version ) {
		if( serviceInformation != null ) {
			mServerAddress = serviceInformation.getHostAddress();
			mServerName = serviceInformation.getName();
			mHostName = serviceInformation.getHostName();
		}

		mServerVersion = version;
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel( Parcel parcel, int i ) {
		parcel.writeString( mServerAddress );
		parcel.writeParcelable( mServerVersion, i );
	}
}
