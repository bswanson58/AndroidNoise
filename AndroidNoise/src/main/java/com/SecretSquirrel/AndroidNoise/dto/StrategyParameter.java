package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by BSwanson on 3/12/14.

import com.SecretSquirrel.AndroidNoise.services.rto.RoStrategyParameter;

public class StrategyParameter {
	private int     mStrategyType;
	private long    mParameterId;
	private String  mParameterTitle;

	public StrategyParameter( RoStrategyParameter roStrategyParameter ) {
		mStrategyType = roStrategyParameter.Type;
		mParameterId = roStrategyParameter.Id;
		mParameterTitle = roStrategyParameter.Title;
	}

	public int getStrategyType() {
		return( mStrategyType );
	}

	public long getParameterId() {
		return( mParameterId );
	}

	public String getParameterTitle() {
		return( mParameterTitle );
	}
}
