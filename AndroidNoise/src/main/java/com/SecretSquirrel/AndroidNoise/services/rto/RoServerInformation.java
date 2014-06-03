package com.SecretSquirrel.AndroidNoise.services.rto;

// Secret Squirrel Software - Created by bswanson on 12/9/13.

import com.SecretSquirrel.AndroidNoise.dto.ServerVersion;

public class RoServerInformation {
	public ServerVersion    ServerVersion;
	public int              ApiVersion;
	public String           ServerName;
	public long             LibraryId;
	public String           LibraryName;
	public int              LibraryCount;
	public RoAudioDevice[]  AudioDevices;
	public int              CurrentAudioDevice;
}
