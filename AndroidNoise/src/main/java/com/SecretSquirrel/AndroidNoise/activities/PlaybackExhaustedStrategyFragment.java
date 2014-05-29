package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by BSwanson on 3/14/14.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.adapters.PlaybackParameterAdapter;
import com.SecretSquirrel.AndroidNoise.adapters.PlaybackStrategyAdapter;
import com.SecretSquirrel.AndroidNoise.dto.Strategy;
import com.SecretSquirrel.AndroidNoise.dto.StrategyParameter;
import com.SecretSquirrel.AndroidNoise.models.PlaybackStrategyModel;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class PlaybackExhaustedStrategyFragment extends Fragment {
	private PlaybackStrategyAdapter     mExhaustedStrategyAdapter;
	private PlaybackParameterAdapter    mExhaustedParameterAdapter;
	private Subscription                mModelSubscription;

	@Inject	PlaybackStrategyModel       mPlaybackStrategy;

	@InjectView( R.id.ps_exhausted_strategies )	            Spinner     mExhaustedStrategySelector;
	@InjectView( R.id.ps_exhausted_description )	        TextView    mExhaustedStrategyDescription;
	@InjectView( R.id.ps_exhausted_parameter_name )         TextView    mExhaustedParameterTitle;
	@InjectView( R.id.ps_exhausted_strategy_parameters )    Spinner     mExhaustedParameterSelector;

	public static PlaybackExhaustedStrategyFragment newInstance() {
		return( new PlaybackExhaustedStrategyFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		mExhaustedStrategyAdapter = new PlaybackStrategyAdapter( getActivity(), mPlaybackStrategy.getExhaustedStrategies());
		mExhaustedParameterAdapter = new PlaybackParameterAdapter( getActivity(), mPlaybackStrategy.getExhaustedParameters());
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_exhausted_strategy, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );

			mExhaustedStrategySelector.setAdapter( mExhaustedStrategyAdapter );
			mExhaustedStrategySelector.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected( AdapterView<?> adapterView, View view, int i, long l ) {
					Strategy strategy = mPlaybackStrategy.getExhaustedStrategies().get( i );

					if(( strategy != null ) &&
					   ( strategy.getStrategyId() != mPlaybackStrategy.getCurrentExhaustedStrategy())) {
						mPlaybackStrategy.setCurrentExhaustedStrategy( strategy.getStrategyId());
					}
				}

				@Override
				public void onNothingSelected( AdapterView<?> adapterView ) { }
			} );

			mExhaustedParameterSelector.setAdapter( mExhaustedParameterAdapter );
			mExhaustedParameterSelector.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected( AdapterView<?> adapterView, View view, int i, long l ) {
					StrategyParameter parameter = mPlaybackStrategy.getExhaustedParameters().get( i );

					if(( parameter != null ) &
					   ( parameter.getParameterId() != mPlaybackStrategy.getCurrentExhaustedParameter())) {
						mPlaybackStrategy.setCurrentExhaustedParameter( parameter.getParameterId());
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

		mModelSubscription = mPlaybackStrategy.getStrategyChangedObservable()
				.observeOn( AndroidSchedulers.mainThread())
				.subscribe( new Action1<Boolean>() {
					            @Override
					            public void call( Boolean isInitialized ) {
						            if( isInitialized ) {
							            updateDisplay();
						            }
					            }
				            }, new Action1<Throwable>() {
					            @Override
					            public void call( Throwable throwable ) {
						            Timber.e( throwable, "An error was returned from the PlaybackStrategyModel." );
					            }
				            }
				);
	}

	@Override
	public void onPause() {
		super.onPause();

		if( mModelSubscription != null ) {
			mModelSubscription.unsubscribe();
			mModelSubscription = null;
		}
	}

	private void updateDisplay() {
		mExhaustedStrategyAdapter.notifyDataSetChanged();
		mExhaustedParameterAdapter.notifyDataSetChanged();

		int position = -1;

		for( Strategy strategy : mPlaybackStrategy.getExhaustedStrategies()) {
			position++;

			if( strategy.getStrategyId() == mPlaybackStrategy.getCurrentExhaustedStrategy()) {
				mExhaustedStrategySelector.setSelection( position );
				mExhaustedStrategyDescription.setText( strategy.getStrategyDescription());

				if( strategy.getRequiresParameter()) {
					mExhaustedParameterTitle.setText( strategy.getParameterTitle());

					mExhaustedParameterAdapter.notifyDataSetChanged();

					position = -1;
					for( StrategyParameter parameter : mPlaybackStrategy.getExhaustedParameters()) {
						position++;

						if( parameter.getParameterId() == mPlaybackStrategy.getCurrentExhaustedParameter()) {
							mExhaustedParameterSelector.setSelection( position );

							break;
						}
					}

					mExhaustedParameterTitle.setVisibility( View.VISIBLE );
					mExhaustedParameterSelector.setVisibility( View.VISIBLE );
				}
				else {
					mExhaustedParameterTitle.setVisibility( View.INVISIBLE );
					mExhaustedParameterSelector.setVisibility( View.INVISIBLE );
				}

				break;
			}
		}
	}
}
