package com.SecretSquirrel.AndroidNoise.models;

// Created by BSwanson on 3/12/14.

import com.SecretSquirrel.AndroidNoise.dto.Strategy;
import com.SecretSquirrel.AndroidNoise.dto.StrategyInformation;
import com.SecretSquirrel.AndroidNoise.dto.StrategyParameter;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;
import com.SecretSquirrel.AndroidNoise.support.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class PlaybackStrategyModel {
	private final EventBus                      mEventBus;
	private final INoiseQueue                   mNoiseQueue;
	private final ArrayList<Strategy>           mPlayStrategies;
	private final ArrayList<Strategy>           mExhaustedStrategies;
	private final ArrayList<StrategyParameter>  mArtistParameters;
	private final ArrayList<StrategyParameter>  mGenreParameters;
	private final ArrayList<StrategyParameter>  mPlayParameters;
	private final ArrayList<StrategyParameter>  mExhaustedParameters;
	private final BehaviorSubject<Boolean>      mOnStrategyChanged;
	private boolean                             mInitialized;
	private int                                 mCurrentPlayStrategy;
	private long                                mCurrentPlayParameter;
	private int                                 mCurrentExhaustedStrategy;
	private long                                mCurrentExhaustedParameter;

	@Inject
	public PlaybackStrategyModel( EventBus eventBus, IApplicationState applicationState, INoiseQueue noiseQueue ) {
		mEventBus = eventBus;
		mNoiseQueue = noiseQueue;

		mPlayStrategies = new ArrayList<Strategy>();
		mExhaustedStrategies = new ArrayList<Strategy>();

		mArtistParameters = new ArrayList<StrategyParameter>();
		mGenreParameters = new ArrayList<StrategyParameter>();

		mPlayParameters = new ArrayList<StrategyParameter>();
		mExhaustedParameters = new ArrayList<StrategyParameter>();

		mCurrentPlayParameter = Constants.NULL_ID;
		mCurrentExhaustedParameter = Constants.NULL_ID;

		mOnStrategyChanged = BehaviorSubject.create( getSubject());

		mEventBus.register( this );

		if( applicationState.getIsConnected()) {
			getStrategyInformation();
		}
	}

	public Observable<Boolean> getStrategyChangedObservable() {
		return( mOnStrategyChanged.asObservable());
	}

	public int getCurrentPlayStrategy() {
		return( mCurrentPlayStrategy );
	}

	public void setCurrentPlayStrategy( int playStrategy ) {
		mCurrentPlayStrategy = playStrategy;

		setStrategyIfValid();
		notifySubscribers();
	}

	public List<Strategy> getPlayStrategies() {
		return( mPlayStrategies );
	}

	public long getCurrentPlayParameter() {
		return( mCurrentPlayParameter );
	}

	public void setCurrentPlayParameter( long parameter ) {
		mCurrentPlayParameter = parameter;

		setStrategyIfValid();
		notifySubscribers();
	}

	public List<StrategyParameter> getPlayParameters() {
		return( mPlayParameters );
	}

	public int getCurrentExhaustedStrategy() {
		return( mCurrentExhaustedStrategy );
	}

	public void setCurrentExhaustedStrategy( int playStrategy ) {
		mCurrentExhaustedStrategy = playStrategy;

		setStrategyIfValid();
		notifySubscribers();
	}

	public List<Strategy> getExhaustedStrategies() {
		return( mExhaustedStrategies );
	}

	public long getCurrentExhaustedParameter() {
		return( mCurrentExhaustedParameter );
	}

	public void setCurrentExhaustedParameter( long parameter ) {
		mCurrentExhaustedParameter = parameter;

		setStrategyIfValid();
		notifySubscribers();
	}

	public List<StrategyParameter> getExhaustedParameters() {
		return( mExhaustedParameters );
	}

	private Boolean getSubject() {
		return( mInitialized );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventServerSelected args ) {
		mInitialized = false;

		getStrategyInformation();
	}

	private void getStrategyInformation() {
		mNoiseQueue.GetStrategyInformation()
				.subscribe( new Action1<StrategyInformation>() {
					            @Override
					            public void call( StrategyInformation strategyInformation ) {
						            updateStrategyInformation( strategyInformation );
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
						            Timber.e( "Setting the queue strategy failed." );
					            }
				            }
			            }, new Action1<Throwable>() {
				            @Override
				            public void call( Throwable throwable ) {
					            Timber.e( "The SetStrategyInformation call failed: " + throwable );
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

		updateStrategyParameters();

		mInitialized = true;
		notifySubscribers();
	}

	private void updateStrategyParameters() {
		for( Strategy strategy : mPlayStrategies ) {
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

		for( Strategy strategy : mExhaustedStrategies ) {
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

	private void notifySubscribers() {
		mOnStrategyChanged.onNext( getSubject());
	}
}
