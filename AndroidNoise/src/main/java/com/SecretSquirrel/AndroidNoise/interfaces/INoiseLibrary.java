package com.SecretSquirrel.AndroidNoise.interfaces;

// Created by BSwanson on 3/6/14.

import com.SecretSquirrel.AndroidNoise.dto.Library;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;

import rx.Observable;

public interface INoiseLibrary {
	public Observable<Library[]> getLibraries();

	public  Observable<BaseServerResult>    syncLibrary();
	public  Observable<BaseServerResult>    selectLibrary( Library library );

	public  Observable<BaseServerResult>    updateLibrary( Library library );
	public  Observable<Library>             createLibrary( Library library );

}
