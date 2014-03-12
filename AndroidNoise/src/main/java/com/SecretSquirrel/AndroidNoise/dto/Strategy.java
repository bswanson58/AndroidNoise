package com.SecretSquirrel.AndroidNoise.dto;

// Secret Squirrel Software - Created by BSwanson on 3/12/14.

import com.SecretSquirrel.AndroidNoise.services.rto.RoQueueStrategy;

public class Strategy {
	private long        mStrategyId;
	private String      mStrategyName;
	private String      mStrategyDescription;
	private boolean     mRequiresParameter;
	private int         mParameterType;
	private String      mParameterTitle;

	public Strategy( RoQueueStrategy roStrategy ) {
		mStrategyId = roStrategy.StrategyId;
		mStrategyName = roStrategy.StrategyName;
		mStrategyDescription = roStrategy.StrategyDescription;
		mRequiresParameter = roStrategy.RequiresParameter;
		mParameterType = roStrategy.ParameterType;
		mParameterTitle = roStrategy.ParameterTitle;
	}

	public long getStrategyId() {
		return( mStrategyId );
	}

	public String getStrategyName() {
		return( mStrategyName );
	}

	public String getStrategyDescription() {
		return( mStrategyDescription );
	}

	public boolean getRequiresParameter() {
		return( mRequiresParameter );
	}

	public int getParameterType() {
		return( mParameterType );
	}

	public String getParameterTitle() {
		return( mParameterTitle );
	}
}
