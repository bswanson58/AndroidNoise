package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by BSwanson on 3/12/14.

import com.SecretSquirrel.AndroidNoise.services.rto.RoQueueStrategy;
import com.SecretSquirrel.AndroidNoise.services.rto.RoStrategyInformation;
import com.SecretSquirrel.AndroidNoise.services.rto.RoStrategyParameter;

import java.util.ArrayList;
import java.util.List;

public class StrategyInformation {
	private int                             mPlayStrategyId;
	private long                            mPlayStrategyParameter;
	private int                             mExhaustedStrategyId;
	private long                            mExhaustedStrategyParameter;
	private ArrayList<Strategy>             mPlayStrategies;
	private ArrayList<Strategy>             mExhaustedStrategies;
	private ArrayList<StrategyParameter>    mGenreParameters;

	public StrategyInformation( RoStrategyInformation roStrategyInformation ) {
		mPlayStrategyId = roStrategyInformation.PlayStrategy;
		mPlayStrategyParameter = roStrategyInformation.PlayStrategyParameter;
		mExhaustedStrategyId = roStrategyInformation.ExhaustedStrategy;
		mExhaustedStrategyParameter = roStrategyInformation.ExhaustedStrategyParameter;

		mPlayStrategies = new ArrayList<Strategy>();
		for( RoQueueStrategy  roStrategy : roStrategyInformation.PlayStrategies ) {
			mPlayStrategies.add( new Strategy( roStrategy ));
		}

		mExhaustedStrategies = new ArrayList<Strategy>();
		for( RoQueueStrategy roStrategy : roStrategyInformation.ExhaustedStrategies ) {
			mExhaustedStrategies.add( new Strategy( roStrategy ));
		}

		mGenreParameters = new ArrayList<StrategyParameter>();
		for( RoStrategyParameter roParameter : roStrategyInformation.GenreParameters ) {
			mGenreParameters.add( new StrategyParameter( roParameter ));
		}
	}

	public int getPlayStrategyId() {
		return( mPlayStrategyId );
	}

	public long getPlayStrategyParameter() {
		return( mPlayStrategyParameter );
	}

	public int getExhaustedStrategyId() {
		return( mExhaustedStrategyId );
	}

	public long getExhaustedStrategyParameter() {
		return( mExhaustedStrategyParameter );
	}

	public List<Strategy> getPlayStrategies() {
		return( mPlayStrategies );
	}

	public List<Strategy> getExhaustedStrategies() {
		return( mExhaustedStrategies );
	}

	public List<StrategyParameter> getGenreParameters() {
		return( mGenreParameters );
	}
}
