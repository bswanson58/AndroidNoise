package com.SecretSquirrel.AndroidNoise.services;

// Created by BSwanson on 3/6/14.

import com.SecretSquirrel.AndroidNoise.dto.Library;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseLibrary;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;

import rx.Observable;

public class NoiseLibraryClient implements INoiseLibrary {
	@Override
	public Observable<Library[]> getLibraries() {
		return null;
	}

	@Override
	public Observable<BaseServerResult> syncLibrary() {
		return null;
	}

	@Override
	public Observable<BaseServerResult> selectLibrary( Library library ) {
		return null;
	}

	@Override
	public Observable<BaseServerResult> updateLibrary( Library library ) {
		return null;
	}

	@Override
	public Observable<Library> createLibrary( Library library ) {
		return null;
	}
}
