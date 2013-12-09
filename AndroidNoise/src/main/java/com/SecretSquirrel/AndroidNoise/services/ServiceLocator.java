package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 12/9/13.

import android.os.ResultReceiver;

import java.util.ArrayList;
import java.util.List;

public class ServiceLocator {
	public List<String> LocateServices( String ofType, ResultReceiver receiver ) {
		List<String>    retValue = new ArrayList<String>();

		retValue.add( "http://10.1.1.139:88/Noise" );

		return( retValue );
	}
}
