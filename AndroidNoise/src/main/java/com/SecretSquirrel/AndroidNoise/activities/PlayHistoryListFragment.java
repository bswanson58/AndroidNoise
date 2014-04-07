package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by BSwanson on 4/7/14.

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.PlayHistory;
import com.SecretSquirrel.AndroidNoise.events.EventAlbumRequest;
import com.SecretSquirrel.AndroidNoise.events.EventPlayTrack;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class PlayHistoryListFragment extends Fragment
									 implements ServiceResultReceiver.Receiver {
	private final String            LIST_STATE      = "historyListState";
	private final String            HISTORY_LIST    = "historyList";

	private ArrayList<PlayHistory>  mHistoryList;
	private Parcelable              mListViewState;
	private HistoryListAdapter      mHistoryListAdapter;

	@Inject	INoiseData              mNoiseData;
	@Inject	EventBus                mEventBus;
	@Inject	ServiceResultReceiver   mServiceResultReceiver;

	@InjectView( R.id.ph_list_view )    ListView    mHistoryListView;

	public static PlayHistoryListFragment newInstance() {
		return( new PlayHistoryListFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		if( savedInstanceState != null ) {
			mHistoryList = savedInstanceState.getParcelableArrayList( HISTORY_LIST );
			mListViewState = savedInstanceState.getParcelable( LIST_STATE );
		}
		if( mHistoryList == null ) {
			mHistoryList = new ArrayList<PlayHistory>();
		}

		mHistoryListAdapter = new HistoryListAdapter( getActivity(), mHistoryList );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_history_list, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );

			mHistoryListView.setAdapter( mHistoryListAdapter );
			mHistoryListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick( AdapterView<?> adapterView, View view, int i, long l ) {
					PlayHistory history = mHistoryList.get( i );

					mEventBus.post( new EventAlbumRequest( history.getArtistId(), history.getAlbumId()));
				}
			} );

			if( mListViewState != null ) {
				mHistoryListView.onRestoreInstanceState( mListViewState );
			}
		}

		return( myView );
	}

	@Override
	public void onResume() {
		super.onResume();

		if( mHistoryList.size() == 0 ) {
			mServiceResultReceiver.setReceiver( this );

			mNoiseData.GetPlayHistory( mServiceResultReceiver );
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		mServiceResultReceiver.clearReceiver();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		ButterKnife.reset( this );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putParcelableArrayList( HISTORY_LIST, mHistoryList );

		if( mHistoryListView != null ) {
			mListViewState = mHistoryListView.onSaveInstanceState();
		}
		if( mListViewState != null ) {
			outState.putParcelable( LIST_STATE, mListViewState );
		}
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			ArrayList<PlayHistory>  historyList = resultData.getParcelableArrayList( NoiseRemoteApi.PlayHistoryList );

			setHistoryList( historyList );
		}
	}

	public void setHistoryList( ArrayList<PlayHistory> historyList ) {
		mHistoryList.clear();
		mHistoryList.addAll( historyList );

		mHistoryListAdapter.notifyDataSetChanged();
	}

	protected class HistoryListAdapter extends ArrayAdapter<PlayHistory> {
		private Context         mContext;
		private LayoutInflater  mLayoutInflater;

		protected class ViewHolder {
			public ViewHolder( View view ) {
				ButterKnife.inject( this, view );
			}

			@InjectView( R.id.play_button )	    Button      PlayButton;
			@InjectView( R.id.ph_track_name )   TextView    TrackNameView;
			@InjectView( R.id.ph_album_name )   TextView    AlbumNameView;

			@SuppressWarnings( "unused" )
			@OnClick( R.id.play_button )
			public void onClick( View view ) {
				PlayHistory history = (PlayHistory)view.getTag();

				if( history != null ) {
					mEventBus.post( new EventPlayTrack( history.getArtistId(), history.getTrackId(), history.getTrackName()));
				}
			}
		}

		public HistoryListAdapter( Context context, ArrayList<PlayHistory> favoritesList ) {
			super( context, R.layout.history_list_item, favoritesList );
			mContext = context;

			mLayoutInflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View        retValue = convertView;
			ViewHolder  views = null;

			if( convertView == null ) {
				retValue = mLayoutInflater.inflate( R.layout.history_list_item, parent, false );

				if( retValue != null ) {
					views = new ViewHolder( retValue );

					retValue.setTag( views );
				}
			}
			else {
				views = (ViewHolder)retValue.getTag();
			}

			if(( views != null ) &&
			   ( position < mHistoryList.size())) {
				PlayHistory    history = mHistoryList.get( position );

				views.PlayButton.setTag( history );
				views.TrackNameView.setText( history.getTrackName());
				views.AlbumNameView.setText( history.getAlbumName());
			}

			return( retValue );
		}
	}
}
