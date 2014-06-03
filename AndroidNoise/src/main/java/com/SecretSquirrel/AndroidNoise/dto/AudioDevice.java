package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by BSwanson on 6/3/14.

import android.os.Parcel;
import android.os.Parcelable;

import com.SecretSquirrel.AndroidNoise.services.rto.RoAudioDevice;

public class AudioDevice implements Parcelable {
	private int     mDeviceId;
	private String  mName;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<AudioDevice> CREATOR = new Parcelable.Creator<AudioDevice>() {
		public AudioDevice createFromParcel(Parcel parcel) {
			return new AudioDevice( parcel );
		}
		public AudioDevice[] newArray(int size) {
			return new AudioDevice[size];
		}
	};

	public AudioDevice( RoAudioDevice roDevice ) {
		mDeviceId = roDevice.DeviceId;
		mName = roDevice.Name;
	}

	public AudioDevice( Parcel parcel ) {
		mDeviceId = parcel.readInt();
		mName = parcel.readString();
	}

	public int getDeviceId() {
		return( mDeviceId );
	}

	public String getDeviceName() {
		return( mName );
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel( Parcel parcel, int i ) {
		parcel.writeInt( mDeviceId );
		parcel.writeString( mName );
	}
}
