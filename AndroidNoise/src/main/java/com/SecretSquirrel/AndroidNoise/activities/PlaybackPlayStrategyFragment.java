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

public class PlaybackPlayStrategyFragment extends Fragment {
	private PlaybackStrategyAdapter     mPlayStrategyAdapter;
	private PlaybackParameterAdapter    mPlayParameterAdapter;
	private Subscription                mModelSubscription;

	@Inject	PlaybackStrategyModel   mPlaybackStrategy;

	@InjectView( R.id.ps_play_strategies )	        Spinner     mPlayStrategySelector;
	@InjectView( R.id.ps_play_description )	        TextView    mPlayStrategyDescription;
	@InjectView( R.id.ps_play_parameter_name )      TextView    mPlayParameterTitle;
	@InjectView( R.id.ps_play_strategy_parameters ) Spinner     mPlayParameterSelector;

	public static PlaybackPlayStrategyFragment newInstance() {
		return( new PlaybackPlayStrategyFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		mPlayStrategyAdapter = new PlaybackStrategyAdapter( getActivity(), mPlaybackStrategy.getPlayStrategies());
		mPlayParameterAdapter = new PlaybackParameterAdapter( getActivity(), mPlaybackStrategy.getPlayParameters());
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_play_strategy, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );

			mPlayStrategySelector.setAdapter( mPlayStrategyAdapter );
			mPlayStrategySelector.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected( AdapterView<?> adapterView, View view, int i, long l ) {
					Strategy    strategy = mPlaybackStrategy.getPlayStrategies().get( i );

					if(( strategy != null ) &&
					   ( strategy.getStrategyId() != mPlaybackStrategy.getCurrentPlayStrategy())) {
						mPlaybackStrategy.setCurrentPlayStrategy( strategy.getStrategyId());
					}
				}

				@Override
				public void onNothingSelected( AdapterView<?> adapterView ) { }
			} );

			mPlayParameterSelector.setAdapter( mPlayParameterAdapter );
			mPlayParameterSelector.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected( AdapterView<?> adapterView, View view, int i, long l ) {
					StrategyParameter   parameter = mPlaybackStrategy.getPlayParameters().get( i );

					if(( parameter != null ) &&
					   ( parameter.getParameterId() != mPlaybackStrategy.getCurrentPlayParameter())) {
						mPlaybackStrategy.setCurrentPlayParameter( parameter.getParameterId() );
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
		mPlayStrategyAdapter.notifyDataSetChanged();
		mPlayParameterAdapter.notifyDataSetChanged();

		int position = -1;

		for( Strategy strategy : mPlaybackStrategy.getPlayStrategies()) {
			position++;

			if( strategy.getStrategyId() == mPlaybackStrategy.getCurrentPlayStrategy()) {
				mPlayStrategySelector.setSelection( position );
				mPlayStrategyDescription.setText( strategy.getStrategyDescription());

				if( strategy.getRequiresParameter()) {
					mPlayParameterTitle.setText( strategy.getParameterTitle() );

					mPlayParameterAdapter.notifyDataSetChanged();

					mPlayParameterTitle.setVisibility( View.VISIBLE );
					mPlayParameterSelector.setVisibility( View.VISIBLE );
				}
				else {
					mPlayParameterTitle.setVisibility( View.INVISIBLE );
					mPlayParameterSelector.setVisibility( View.INVISIBLE );
				}

				break;
			}
		}
	}
}
