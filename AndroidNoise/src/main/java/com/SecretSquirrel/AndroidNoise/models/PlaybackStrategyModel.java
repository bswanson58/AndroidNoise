package com.SecretSquirrel.AndroidNoise.models;

// Created by BSwanson on 3/12/14.

import android.content.Context;

import com.SecretSquirrel.AndroidNoise.dto.Strategy;
import com.SecretSquirrel.AndroidNoise.dto.StrategyInformation;
import com.SecretSquirrel.AndroidNoise.dto.StrategyParameter;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;
import com.SecretSquirrel.AndroidNoise.support.Constants;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import rx.subjects.BehaviorSubject;
import rx.util.functions.Action1;
import timber.log.Timber;

public class PlaybackStrategyModel {
	private ArrayList<Strategy>             mPlayStrategies;
	private ArrayList<Strategy>             mExhaustedStrategies;
	private ArrayList<StrategyParameter>    mArtistParameters;
	private ArrayList<StrategyParameter>    mGenreParameters;
	private ArrayList<StrategyParameter>    mPlayParameters;
	private ArrayList<StrategyParameter>    mExhaustedParameters;
	private int                             mCurrentPlayStrategy;
	private long                            mCurrentPlayParameter;
	private int                             mCurrentExhaustedStrategy;
	private long                            mCurrentExhaustedParameter;
	private BehaviorSubject<Object>         mOnStrategyChanged;

	@Inject	EventBus                        mEventBus;
	@Inject	INoiseQueue                     mNoiseQueue;

	@Inject
	public PlaybackStrategyModel( Context context ) {
		IocUtility.inject( context, this );

		mPlayStrategies = new ArrayList<Strategy>();
		mExhaustedStrategies = new ArrayList<Strategy>();

		mArtistParameters = new ArrayList<StrategyParameter>();
		mGenreParameters = new ArrayList<StrategyParameter>();

		mPlayParameters = new ArrayList<StrategyParameter>();
		mExhaustedParameters = new ArrayList<StrategyParameter>();

		mCurrentPlayParameter = Constants.NULL_ID;
		mCurrentExhaustedParameter = Constants.NULL_ID;

		mOnStrategyChanged = BehaviorSubject.create( getSubject());
	}

	private Object getSubject() {
		return( this );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventServerSelected args ) {
		getStrategyInformation();
	}

	private void getStrategyInformation() {
		mNoiseQueue.GetStrategyInformation()
				.subscribe( new Action1<StrategyInformation>() {
					            @Override
					            public void call( StrategyInformation strategyInformation ) {
						            updateStrategyInformation( strategyInformation );

						            updateDisplay();
					            }
				            }, new Action1<Throwable>() {
					            @Override
					            public void call( Throwable throwable ) {
						            Timber.e( "The GetStrategyInformation call failed: " + throwable );
					            }
				            }
				);
	}

	private void setStrategyIfValid() {
		boolean update = true;

		for( Strategy strategy : mPlayStrategies ) {
			if( strategy.getStrategyId() == mCurrentPlayStrategy ) {
				if( strategy.getRequiresParameter()) {
					if( mCurrentPlayParameter == Constants.NULL_ID ) {
						update = false;
					}
				}

				break;
			}
		}

		for( Strategy strategy : mExhaustedStrategies ) {
			if( strategy.getStrategyId() == mCurrentExhaustedStrategy ) {
				if( strategy.getRequiresParameter()) {
					if( mCurrentExhaustedParameter == Constants.NULL_ID ) {
						update = false;
					}
				}

				break;
			}
		}

		if( update ) {
			setStrategyInformation();
		}
	}

	private void setStrategyInformation() {
		mNoiseQueue.SetStrategyInformation( mCurrentPlayStrategy, mCurrentPlayParameter,
											mCurrentExhaustedStrategy, mCurrentExhaustedParameter )
				.subscribe( new Action1<BaseServerResult>() {
					            @Override
					            public void call( BaseServerResult result ) {
						            if( !result.Success ) {
							            //Toast.makeText( getActivity(), "Setting the strategy failed.", Toast.LENGTH_LONG ).show();
						            }
					            }
				            }, new Action1<Throwable>() {
					            @Override
					            public void call( Throwable throwable ) {
						            Timber.e( "The GetStrategyInformation call failed: " + throwable );
					            }
				            }
				);
	}

	private void updateStrategyInformation( StrategyInformation strategyInformation ) {
		mCurrentPlayStrategy = strategyInformation.getPlayStrategyId();
		mCurrentPlayParameter = strategyInformation.getPlayStrategyParameter();

		mPlayStrategies.clear();
		mPlayStrategies.addAll( strategyInformation.getPlayStrategies() );

		mCurrentExhaustedStrategy = strategyInformation.getExhaustedStrategyId();
		mCurrentExhaustedParameter = strategyInformation.getExhaustedStrategyParameter();

		mExhaustedStrategies.clear();
		mExhaustedStrategies.addAll( strategyInformation.getExhaustedStrategies() );

		mArtistParameters.clear();
		mArtistParameters.addAll( strategyInformation.getArtistParameters());

		mGenreParameters.clear();
		mGenreParameters.addAll( strategyInformation.getGenreParameters());
	}

	private void updateDisplay() {
		int position = -1;

		for( Strategy strategy : mPlayStrategies ) {
			position++;

			if( strategy.getStrategyId() == mCurrentPlayStrategy ) {
				if( strategy.getRequiresParameter()) {
					updateParameterList( mPlayParameters, strategy.getParameterType() );
				}
				else {
					mCurrentPlayParameter = Constants.NULL_ID;
				}

				break;
			}
		}

		position = -1;
		for( Strategy strategy : mExhaustedStrategies ) {
			position++;

			if( strategy.getStrategyId() == mCurrentExhaustedStrategy ) {
				if( strategy.getRequiresParameter()) {
					updateParameterList( mExhaustedParameters, strategy.getParameterType() );
				}
				else {
					mCurrentExhaustedParameter = Constants.NULL_ID;
				}

				break;
			}
		}
	}

	private void updateParameterList( List<StrategyParameter> list, int parameterType ) {
		list.clear();

		switch( parameterType ) {
			case Strategy.ParameterTypeGenre:
				list.addAll( mGenreParameters );
				break;

			case Strategy.ParameterTypeArtist:
				list.addAll( mArtistParameters );
				break;
		}
	}
}
