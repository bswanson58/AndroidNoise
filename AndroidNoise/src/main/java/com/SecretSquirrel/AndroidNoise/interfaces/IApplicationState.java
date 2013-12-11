package com.SecretSquirrel.AndroidNoise.interfaces;

// Secret Squirrel Software - Created by bswanson on 12/11/13.

import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;

public interface IApplicationState {
	public boolean  getIsConnected();
	public void     LocateServers( final ServiceResultReceiver receiver );
	public void     SelectServer( ServerInformation server );

	public INoiseData getDataClient();
}
