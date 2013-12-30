package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/30/13.

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.PlayQueueListResult;
import com.SecretSquirrel.AndroidNoise.dto.PlayQueueTrack;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;

import java.util.ArrayList;

import rx.Subscription;
import rx.util.functions.Action1;

public class QueueListFragment extends Fragment  {
	private ListView                    mQueueListView;
	private ArrayList<PlayQueueTrack>   mQueueList;
	private QueueAdapter                mQueueListAdapter;
	private Subscription                mQueueSubscription;

	public static QueueListFragment newInstance() {
		return( new QueueListFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mQueueList = new ArrayList<PlayQueueTrack>();
		mQueueListAdapter = new QueueAdapter( getActivity(), mQueueList );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_queue_list, container, false );

		mQueueListView = (ListView) myView.findViewById( R.id.QueueListView );
		mQueueListView.setAdapter( mQueueListAdapter );

		if( getApplicationState().getIsConnected()) {
			mQueueSubscription = getApplicationState().getQueueClient().GetQueuedTrackList( new Action1<PlayQueueListResult>() {
				@Override
				public void call( PlayQueueListResult playQueueListResult ) {
					setQueueList( playQueueListResult.getTracks());
				}
			}, new Action1<Throwable>() {
               @Override
               public void call( Throwable throwable ) {

               }
           } );
		}

		return( myView );
	}

	@Override
	public void onPause() {
		super.onPause();

		if( mQueueSubscription != null ) {
			mQueueSubscription.unsubscribe();
		}
	}

	public void setQueueList( ArrayList<PlayQueueTrack> queueList ) {
		mQueueList.clear();
		mQueueList.addAll( queueList );
		mQueueListAdapter.notifyDataSetChanged();
	}

	private IApplicationState getApplicationState() {
		NoiseRemoteApplication application = (NoiseRemoteApplication)getActivity().getApplication();

		return( application.getApplicationState());
	}

	private class QueueAdapter extends ArrayAdapter<PlayQueueTrack> {
		private Context                     mContext;
		private LayoutInflater              mLayoutInflater;
		private ArrayList<PlayQueueTrack>   mQueueList;

		private class ViewHolder {
			public TextView NameTextView;
		}

		public QueueAdapter( Context context, ArrayList<PlayQueueTrack> queueList ) {
			super( context, R.layout.queue_list_item, queueList );
			mContext = context;
			mQueueList = queueList;

			mLayoutInflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View        retValue = convertView;
			ViewHolder  views;

			if( convertView == null ) {
				retValue = mLayoutInflater.inflate( R.layout.queue_list_item, parent, false );

				views = new ViewHolder();
				views.NameTextView = (TextView)retValue.findViewById( R.id.queue_list_item_name );

				retValue.setTag( views );
			}
			else {
				views = (ViewHolder)retValue.getTag();
			}

			if(( views != null ) &&
			   ( position < mQueueList.size())) {
				PlayQueueTrack  track = mQueueList.get( position );

				views.NameTextView.setText( track.getTrackName() + " (" + track.getArtistName() + "/" + track.getAlbumName() + ")" );
			}

			return( retValue );
		}
	}
}
