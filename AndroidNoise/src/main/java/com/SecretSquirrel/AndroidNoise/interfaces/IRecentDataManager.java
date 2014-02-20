package com.SecretSquirrel.AndroidNoise.interfaces;

// Secret Squirrel Software - Created by bswanson on 2/20/14.

public interface IRecentDataManager {
	void            start();
	void            persistData();
	void            stop();

	IRecentData     getRecentData();
}
