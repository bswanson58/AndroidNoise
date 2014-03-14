package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by BSwanson on 3/14/14.

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
import com.SecretSquirrel.AndroidNoise.dto.StrategyParameter;
import com.SecretSquirrel.AndroidNoise.models.PlaybackStrategyModel;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.functions.Action1;
import timber.log.Timber;

public class PlaybackPlayStrategyFragment extends Fragment {
	private StrategyAdapter         mPlayStrategyAdapter;
	private ParameterAdapter        mPlayParameterAdapter;
	private Subscription            mModelSubscription;

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

		mPlayStrategyAdapter = new StrategyAdapter( getActivity(), mPlaybackStrategy.getPlayStrategies());
		mPlayParameterAdapter = new ParameterAdapter( getActivity(), mPlaybackStrategy.getPlayParameters());
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
						mPlaybackStrategy.setCurrentPlayParameter( parameter.getParameterId());
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
