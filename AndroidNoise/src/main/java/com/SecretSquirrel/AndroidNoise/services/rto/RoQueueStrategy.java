package com.SecretSquirrel.AndroidNoise.services.rto;

// Secret Squirrel Software - Created by BSwanson on 3/12/14.

public class RoQueueStrategy {
	public final int    ParameterTypeArtist = 1;
	public final int    ParameterTypeGenre = 2;

	public int      StrategyId;
	public String   StrategyName;
	public String   StrategyDescription;
	public boolean  RequiresParameter;
	public int      ParameterType;
	public String   ParameterTitle;
}
