package com.SecretSquirrel.AndroidNoise.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.SecretSquirrel.AndroidNoise.services.rto.RoServerVersion;

// Secret Squirrel Software - Created by bswanson on 12/6/13.

public class ServerVersion implements Parcelable {
	public  long    Major;
	public  long    Minor;
	public  long    Build;
	public  long    Revision;

	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<ServerVersion> CREATOR = new Parcelable.Creator<ServerVersion>() {
		public ServerVersion createFromParcel(Parcel pc) {
			return new ServerVersion(pc);
		}
		public ServerVersion[] newArray(int size) {
			return new ServerVersion[size];
		}
	};

	public ServerVersion( RoServerVersion fromVersion ) {
		Major = fromVersion.Major;
		Minor = fromVersion.Minor;
		Build = fromVersion.Build;
		Revision = fromVersion.Revision;
	}

	public ServerVersion( Parcel parcel ) {
		Major = parcel.readLong();
		Minor = parcel.readLong();
		Build = parcel.readLong();
		Revision = parcel.readLong();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel( Parcel parcel, int i ) {
		parcel.writeLong( Major );
		parcel.writeLong( Minor );
		parcel.writeLong( Build );
		parcel.writeLong( Revision );
	}
}
