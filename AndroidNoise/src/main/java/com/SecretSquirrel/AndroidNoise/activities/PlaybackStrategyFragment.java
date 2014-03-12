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

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Strategy;
import com.SecretSquirrel.AndroidNoise.dto.StrategyInformation;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
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
	private ArrayList<Strategy> mPlayStrategies;
	private ArrayList<Strategy> mExhaustedStrategies;
	private StrategyAdapter     mPlayStrategyAdapter;
	private StrategyAdapter     mExhaustedStrategyAdapter;
	private int                 mCurrentPlayStrategy;
	private long                mCurrentPlayParameter;
	private int                 mCurrentExhaustedStrategy;
	private long                mCurrentExhaustedParameter;

	@Inject	EventBus            mEventBus;
	@Inject	INoiseQueue         mNoiseQueue;

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
					mCurrentPlayStrategy = mPlayStrategies.get( i ).getStrategyId();

					updateDisplay();
				}

				@Override
				public void onNothingSelected( AdapterView<?> adapterView ) { }
			} );

			mExhaustedStrategySelector.setAdapter( mExhaustedStrategyAdapter );
			mExhaustedStrategySelector.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected( AdapterView<?> adapterView, View view, int i, long l ) {
					mCurrentExhaustedStrategy = mExhaustedStrategies.get( i ).getStrategyId();

					updateDisplay();
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
	}

	private void updateDisplay() {
		int position = -1;

		for( Strategy strategy : mPlayStrategies ) {
			position++;

			if( strategy.getStrategyId() == mCurrentPlayStrategy ) {
				mPlayStrategySelector.setSelection( position );
				mPlayStrategyDescription.setText( strategy.getStrategyDescription());

				if( strategy.getRequiresParameter()) {
					mPlayParameterTitle.setText( strategy.getParameterTitle());
					mPlayParameterTitle.setVisibility( View.VISIBLE );
				}
				else {
					mPlayParameterTitle.setVisibility( View.INVISIBLE );
					mPlayParameterSelector.setVisibility( View.INVISIBLE );
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
					mExhaustedParameterTitle.setText( strategy.getParameterTitle());
					mExhaustedParameterTitle.setVisibility( View.VISIBLE );
				}
				else {
					mExhaustedParameterTitle.setVisibility( View.INVISIBLE );
					mExhaustedParameterSelector.setVisibility( View.INVISIBLE );
				}

				break;
			}
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
}
