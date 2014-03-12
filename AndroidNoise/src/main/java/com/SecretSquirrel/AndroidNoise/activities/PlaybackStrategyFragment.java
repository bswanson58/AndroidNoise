package com.SecretSquirrel.AndroidNoise.activities;

// Created by BSwanson on 3/10/14.

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Strategy;
import com.SecretSquirrel.AndroidNoise.dto.StrategyInformation;
import com.SecretSquirrel.AndroidNoise.dto.StrategyParameter;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;
import com.SecretSquirrel.AndroidNoise.support.Constants;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import rx.android.observables.AndroidObservable;
import rx.util.functions.Action1;
import timber.log.Timber;

public class PlaybackStrategyFragment extends Fragment {
	private ArrayList<Strategy>             mPlayStrategies;
	private ArrayList<Strategy>             mExhaustedStrategies;
	private ArrayList<StrategyParameter>    mArtistParameters;
	private ArrayList<StrategyParameter>    mGenreParameters;
	private ArrayList<StrategyParameter>    mPlayParameters;
	private ArrayList<StrategyParameter>    mExhaustedParameters;
	private StrategyAdapter                 mPlayStrategyAdapter;
	private ParameterAdapter                mPlayParameterAdapter;
	private StrategyAdapter                 mExhaustedStrategyAdapter;
	private ParameterAdapter                mExhaustedParameterAdapter;
	private int                             mCurrentPlayStrategy;
	private long                            mCurrentPlayParameter;
	private int                             mCurrentExhaustedStrategy;
	private long                            mCurrentExhaustedParameter;

	@Inject	EventBus                        mEventBus;
	@Inject	INoiseQueue                     mNoiseQueue;

	@InjectView( R.id.ps_play_strategies )	                Spinner     mPlayStrategySelector;
	@InjectView( R.id.ps_play_description )	                TextView    mPlayStrategyDescription;
	@InjectView( R.id.ps_play_parameter_name )              TextView    mPlayParameterTitle;
	@InjectView( R.id.ps_play_strategy_parameters )         Spinner     mPlayParameterSelector;

	@InjectView( R.id.ps_exhausted_strategies )	            Spinner     mExhaustedStrategySelector;
	@InjectView( R.id.ps_exhausted_description )	        TextView    mExhaustedStrategyDescription;
	@InjectView( R.id.ps_exhausted_parameter_name )         TextView    mExhaustedParameterTitle;
	@InjectView( R.id.ps_exhausted_strategy_parameters )    Spinner     mExhaustedParameterSelector;

	public static PlaybackStrategyFragment newInstance() {
		return( new PlaybackStrategyFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		mPlayStrategies = new ArrayList<Strategy>();
		mPlayStrategyAdapter = new StrategyAdapter( getActivity(), mPlayStrategies );

		mExhaustedStrategies = new ArrayList<Strategy>();
		mExhaustedStrategyAdapter = new StrategyAdapter( getActivity(), mExhaustedStrategies );

		mArtistParameters = new ArrayList<StrategyParameter>();
		mGenreParameters = new ArrayList<StrategyParameter>();

		mPlayParameters = new ArrayList<StrategyParameter>();
		mPlayParameterAdapter = new ParameterAdapter( getActivity(), mPlayParameters );

		mExhaustedParameters = new ArrayList<StrategyParameter>();
		mExhaustedParameterAdapter = new ParameterAdapter( getActivity(), mExhaustedParameters );

		mCurrentPlayParameter = Constants.NULL_ID;
		mCurrentExhaustedParameter = Constants.NULL_ID;
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_playback_strategy, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );

			mPlayStrategySelector.setAdapter( mPlayStrategyAdapter );
			mPlayStrategySelector.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected( AdapterView<?> adapterView, View view, int i, long l ) {
					Strategy    strategy = mPlayStrategies.get( i );

					if(( strategy != null ) &&
					   ( strategy.getStrategyId() != mCurrentPlayStrategy )) {
						mCurrentPlayStrategy = strategy.getStrategyId();

						updateDisplay();
						setStrategyIfValid();
					}
				}

				@Override
				public void onNothingSelected( AdapterView<?> adapterView ) { }
			} );

			mPlayParameterSelector.setAdapter( mPlayParameterAdapter );
			mPlayParameterSelector.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected( AdapterView<?> adapterView, View view, int i, long l ) {
					StrategyParameter   parameter = mPlayParameters.get( i );

					if(( parameter != null ) &&
					   ( parameter.getParameterId() != mCurrentPlayParameter )) {
						mCurrentPlayParameter = parameter.getParameterId();

						setStrategyIfValid();
					}
				}

				@Override
				public void onNothingSelected( AdapterView<?> adapterView ) { }
			} );

			mExhaustedStrategySelector.setAdapter( mExhaustedStrategyAdapter );
			mExhaustedStrategySelector.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected( AdapterView<?> adapterView, View view, int i, long l ) {
					Strategy    strategy = mExhaustedStrategies.get( i );

					if(( strategy != null ) &&
					   ( strategy.getStrategyId() != mCurrentExhaustedStrategy )) {
						mCurrentExhaustedStrategy = strategy.getStrategyId();

						updateDisplay();
						setStrategyIfValid();
					}
				}

				@Override
				public void onNothingSelected( AdapterView<?> adapterView ) { }
			} );

			mExhaustedParameterSelector.setAdapter( mExhaustedParameterAdapter );
			mExhaustedParameterSelector.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected( AdapterView<?> adapterView, View view, int i, long l ) {
					StrategyParameter parameter = mExhaustedParameters.get( i );

					if( (parameter != null) && (parameter.getParameterId() != mCurrentExhaustedParameter) ) {
						mCurrentExhaustedParameter = parameter.getParameterId();

						setStrategyIfValid();
					}
				}

				@Override
				public void onNothingSelected( AdapterView<?> adapterView ) { }
			} );
		}

		return( myView );
	}

	@Override
	public void onResume() {
		super.onResume();

		getStrategyInformation();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private void getStrategyInformation() {
		AndroidObservable.fromFragment( this, mNoiseQueue.GetStrategyInformation())
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
		AndroidObservable.fromFragment( this, mNoiseQueue.SetStrategyInformation( mCurrentPlayStrategy, mCurrentPlayParameter, mCurrentExhaustedStrategy, mCurrentExhaustedParameter ))
				.subscribe( new Action1<BaseServerResult>() {
					            @Override
					            public void call( BaseServerResult result ) {
						            if(!result.Success ) {
							            Toast.makeText( getActivity(), "Setting the strategy failed.", Toast.LENGTH_LONG ).show();
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
		mPlayStrategies.addAll( strategyInformation.getPlayStrategies());
		mPlayStrategyAdapter.notifyDataSetChanged();

		mCurrentExhaustedStrategy = strategyInformation.getExhaustedStrategyId();
		mCurrentExhaustedParameter = strategyInformation.getExhaustedStrategyParameter();

		mExhaustedStrategies.clear();
		mExhaustedStrategies.addAll( strategyInformation.getExhaustedStrategies());
		mExhaustedStrategyAdapter.notifyDataSetChanged();

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
				mPlayStrategySelector.setSelection( position );
				mPlayStrategyDescription.setText( strategy.getStrategyDescription());

				if( strategy.getRequiresParameter()) {
					mPlayParameterTitle.setText( strategy.getParameterTitle() );

					updateParameterList( mPlayParameters, strategy.getParameterType());
					mPlayParameterAdapter.notifyDataSetChanged();

					mPlayParameterTitle.setVisibility( View.VISIBLE );
					mPlayParameterSelector.setVisibility( View.VISIBLE );
				}
				else {
					mPlayParameterTitle.setVisibility( View.INVISIBLE );
					mPlayParameterSelector.setVisibility( View.INVISIBLE );

					mCurrentPlayParameter = Constants.NULL_ID;
				}

				break;
			}
		}

		position = -1;
		for( Strategy strategy : mExhaustedStrategies ) {
			position++;

			if( strategy.getStrategyId() == mCurrentExhaustedStrategy ) {
				mExhaustedStrategySelector.setSelection( position );
				mExhaustedStrategyDescription.setText( strategy.getStrategyDescription());

				if( strategy.getRequiresParameter()) {
					mExhaustedParameterTitle.setText( strategy.getParameterTitle() );

					updateParameterList( mExhaustedParameters, strategy.getParameterType());
					mExhaustedParameterAdapter.notifyDataSetChanged();

					mExhaustedParameterTitle.setVisibility( View.VISIBLE );
					mExhaustedParameterSelector.setVisibility( View.VISIBLE );
				}
				else {
					mExhaustedParameterTitle.setVisibility( View.INVISIBLE );
					mExhaustedParameterSelector.setVisibility( View.INVISIBLE );

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


	protected class StrategyAdapter extends BaseAdapter implements SpinnerAdapter {
		private final LayoutInflater    mLayoutInflater;
		private final List<Strategy>    mStrategyList;

		public StrategyAdapter( Context context, List<Strategy> strategies ) {
			mStrategyList = strategies;

			mLayoutInflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		}

		@Override
		public int getCount() {
			return( mStrategyList.size());
		}

		@Override
		public Object getItem( int i ) {
			return( mStrategyList.get( i ));
		}

		@Override
		public long getItemId( int i ) {
			return( i );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View        retValue = convertView;

			if( convertView == null ) {
				retValue = mLayoutInflater.inflate( R.layout.simple_spinner_item, parent, false );
			}

			if( retValue != null ) {
				Strategy    strategy = (Strategy)getItem( position );
				TextView    textView = (TextView)retValue.findViewById( R.id.si_text );

				if( textView != null ) {
					textView.setText( strategy.getStrategyName());
				}
			}

			return( retValue );
		}
	}

	protected class ParameterAdapter extends BaseAdapter implements SpinnerAdapter {
		private final LayoutInflater            mLayoutInflater;
		private final List<StrategyParameter>   mParameterList;

		public ParameterAdapter( Context context, List<StrategyParameter> parameters ) {
			mParameterList = parameters;

			mLayoutInflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		}

		@Override
		public int getCount() {
			return( mParameterList.size());
		}

		@Override
		public Object getItem( int i ) {
			return( mParameterList.get( i ));
		}

		@Override
		public long getItemId( int i ) {
			return( i );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View        retValue = convertView;

			if( convertView == null ) {
				retValue = mLayoutInflater.inflate( R.layout.simple_spinner_item, parent, false );
			}

			if( retValue != null ) {
				StrategyParameter   parameter = (StrategyParameter)getItem( position );
				TextView            textView = (TextView)retValue.findViewById( R.id.si_text );

				if( textView != null ) {
					textView.setText( parameter.getParameterTitle() );
				}
			}

			return( retValue );
		}
	}
}
