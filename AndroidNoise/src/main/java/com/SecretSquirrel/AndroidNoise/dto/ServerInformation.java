package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by bswanson on 12/9/13.

import android.os.Parcel;
import android.os.Parcelable;

import com.SecretSquirrel.AndroidNoise.services.rto.RoAudioDevice;
import com.SecretSquirrel.AndroidNoise.services.rto.RoServerInformation;
import com.SecretSquirrel.AndroidNoise.services.rto.ServiceInformation;

import java.util.ArrayList;

public class ServerInformation implements Parcelable {
	private ServiceInformation.ServiceState mServiceState;
	private String                          mServerAddress;
	private String                          mHostName;
	private String                          mServerName;
	private ServerVersion                   mServerVersion;
	private int                             mServerApiVersion;
	private String                          mLibraryName;
	private long                            mLibraryId;
	private int                             mLibraryCount;
	private ArrayList<AudioDevice>          mAudioDevices;
	private int                             mCurrentAudioDevice;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<ServerInformation> CREATOR = new Parcelable.Creator<ServerInformation>() {
		public ServerInformation createFromParcel( Parcel parcel ) {
			return new ServerInformation( parcel );
		}
		public ServerInformation[] newArray(int size) {
			return new ServerInformation[size];
		}
	};

	public ServerInformation( String serverAddress, RoServerInformation serverInformation ) {
		mServiceState = ServiceInformation.ServiceState.ServiceResolved;

		mServerAddress = serverAddress;
		mServerVersion = serverInformation.ServerVersion;
		mServerName = serverInformation.ServerName;
		mServerApiVersion = serverInformation.ApiVersion;
		mLibraryName = serverInformation.LibraryName;
		mLibraryId = serverInformation.LibraryId;
		mLibraryCount = serverInformation.LibraryCount;

		mAudioDevices = new ArrayList<AudioDevice>();
		if( serverInformation.AudioDevices != null ) {
			for( RoAudioDevice roDevice : serverInformation.AudioDevices ) {
				mAudioDevices.add( new AudioDevice( roDevice ));
			}
		}
		mCurrentAudioDevice = serverInformation.CurrentAudioDevice;

		mHostName = "";
	}

	public ServerInformation( Parcel parcel ) {
		mServiceState = (ServiceInformation.ServiceState)parcel.readSerializable();
		mServerAddress = parcel.readString();
		mHostName = parcel.readString();
		mServerName = parcel.readString();
		mServerApiVersion = parcel.readInt();
		mLibraryName = parcel.readString();
		mLibraryId = parcel.readLong();
		mLibraryCount = parcel.readInt();
		mCurrentAudioDevice = parcel.readInt();

		mServerVersion = parcel.readParcelable( ServerVersion.class.getClassLoader());
		mAudioDevices = parcel.readArrayList( AudioDevice.class.getClassLoader());
	}

	@Override
	public void writeToParcel( Parcel parcel, int i ) {
		parcel.writeSerializable( mServiceState );
		parcel.writeString( mServerAddress );
		parcel.writeString( mHostName );
		parcel.writeString( mServerName );
		parcel.writeInt( mServerApiVersion );
		parcel.writeString( mLibraryName );
		parcel.writeLong( mLibraryId );
		parcel.writeInt( mLibraryCount );
		parcel.writeInt( mCurrentAudioDevice );

		mServerVersion.writeToParcel( parcel, i );
		parcel.writeList( mAudioDevices );
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public void setServiceInformation( ServiceInformation serviceInformation ) {
		mServiceState = serviceInformation.getServiceState();
		mServerAddress = serviceInformation.getHostAddress();
		mHostName = serviceInformation.getHostName();
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

	public ArrayList<AudioDevice> getAudioDevices() {
		return( mAudioDevices );
	}

	public int getCurrentAudioDevice() {
		return( mCurrentAudioDevice );
	}

	public void updateLibrary( long libraryId, String libraryName ) {
		mLibraryId = libraryId;
		mLibraryName = libraryName;
	}

	public void updateAudioDevice( int deviceId ) {
		mCurrentAudioDevice = deviceId;
	}
}
