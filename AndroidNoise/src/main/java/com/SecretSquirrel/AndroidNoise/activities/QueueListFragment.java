package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/30/13.

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.SecretSquirrel.AndroidNoise.services.EventHostService;
import com.SecretSquirrel.AndroidNoise.support.Constants;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.util.functions.Action1;

public class QueueListFragment extends Fragment  {
	private static final String TAG     = QueueListFragment.class.getName();

	private ArrayList<PlayQueueTrack>   mQueueList;
	private QueueAdapter                mQueueListAdapter;
	private Subscription                mQueueSubscription;
	private Messenger                   mMessenger;
	private Messenger                   mService;
	private boolean                     mIsBound;

	public static QueueListFragment newInstance() {
		return( new QueueListFragment());
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected( ComponentName className, IBinder service ) {
			mService = new Messenger( service );

			try {
				Message message = Message.obtain( null, EventHostService.SERVER_EVENT_REGISTER_CLIENT );

				if( message != null ) {
					message.replyTo = mMessenger;

					mService.send( message );
				}
			} catch( RemoteException ex ) {
				if( Constants.LOG_ERROR ) {
					Log.e( TAG, "Sending register client.", ex );
				}
			}
		}

		public void onServiceDisconnected( ComponentName className ) {
			// This is called when the connection with the service has been unexpectedly disconnected - process crashed.
			mService = null;
		}
	};

	private class IncomingHandler extends Handler {
		@Override
		public void handleMessage( Message message ) {
			switch( message.what ) {
				case EventHostService.SERVER_EVENT_QUEUE_CHANGED:
					requestQueueList();
					break;

				default:
					super.handleMessage( message );
			}
		}
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mQueueList = new ArrayList<PlayQueueTrack>();
		mQueueListAdapter = new QueueAdapter( getActivity(), mQueueList );
		mMessenger = new Messenger( new IncomingHandler());
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_queue_list, container, false );

		if( myView != null ) {
			ListView queueListView = (ListView) myView.findViewById( R.id.QueueListView );

			queueListView.setAdapter( mQueueListAdapter );
			queueListView.setEmptyView( myView.findViewById( R.id.ql_empty_view ));
		}

		if( getApplicationState().getIsConnected()) {
			bindToEventService();

			requestQueueList();
		}

		return( myView );
	}

	@Override
	public void onPause() {
		super.onPause();

		if( mQueueSubscription != null ) {
			mQueueSubscription.unsubscribe();
			mQueueSubscription = null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		unbindEventService();
	}

	private void bindToEventService() {
		getApplicationState().registerForEvents( mConnection );

		mIsBound = true;
	}

	private void unbindEventService() {
		if( mIsBound ) {
			if( mService != null ) {
				try {
					Message message = Message.obtain( null, EventHostService.SERVER_EVENT_UNREGISTER_CLIENT );

					if( message != null ) {
						message.replyTo = mMessenger;

						mService.send( message );
					}
				} catch( RemoteException ex ) {
					if( Constants.LOG_ERROR ) {
						Log.e( TAG, "unbindEventService", ex );
					}
				}
			}

			// Detach our existing connection.
			getApplicationState().unregisterFromEvents( mConnection );
			mIsBound = false;
		}
	}

	private void requestQueueList() {
		if( mQueueSubscription != null ) {
			mQueueSubscription.unsubscribe();
			mQueueSubscription = null;
		}

		mQueueSubscription = getApplicationState().getQueueClient()
				.GetQueuedTrackList( new Action1<PlayQueueListResult>() {
					                     @Override
					                     public void call( PlayQueueListResult playQueueListResult ) {
						                     setQueueList( playQueueListResult.getTracks() );
					                     }
				                     }, new Action1<Throwable>() {
					                     @Override
					                     public void call( Throwable throwable ) {
						                     if( Constants.LOG_ERROR ) {
							                     Log.e( TAG, "GetQueuedTrackList", throwable );
						                     }
					                     }
				                     }
				);
	}

	private void setQueueList( ArrayList<PlayQueueTrack> queueList ) {
		mQueueList.clear();
		mQueueList.addAll( queueList );
		mQueueListAdapter.notifyDataSetChanged();

		if( mQueueSubscription != null ) {
			mQueueSubscription.unsubscribe();
			mQueueSubscription = null;
		}
	}

	private IApplicationState getApplicationState() {
		IApplicationState       retValue = null;
		NoiseRemoteApplication  application = (NoiseRemoteApplication)getActivity().getApplication();

		if( application != null ) {
			retValue = application.getApplicationState();
		}

		return( retValue );
	}

	private class QueueAdapter extends ArrayAdapter<PlayQueueTrack> {
		private Context                     mContext;
		private LayoutInflater              mLayoutInflater;
		private ArrayList<PlayQueueTrack>   mQueueList;
		private int                         mWillPlayColor;
		private int                         mHasPlayedColor;

		private class ViewHolder {
			public View         NowPlaying;
			public TextView     NameTextView;
			public TextView     AlbumTextView;
			public TextView     PlayDuration;
		}

		public QueueAdapter( Context context, ArrayList<PlayQueueTrack> queueList ) {
			super( context, R.layout.queue_list_item, queueList );
			mContext = context;
			mQueueList = queueList;

			mWillPlayColor = getResources().getColor( R.color.TitleText );
			mHasPlayedColor = getResources().getColor( R.color.HasPlayed );

			mLayoutInflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View        retValue = convertView;
			ViewHolder  views = null;

			if( convertView == null ) {
				retValue = mLayoutInflater.inflate( R.layout.queue_list_item, parent, false );

				if( retValue != null ) {
					views = new ViewHolder();
					
					views.NowPlaying = retValue.findViewById( R.id.qli_now_playing );
					views.NameTextView = (TextView)retValue.findViewById( R.id.qli_item_name );
					views.AlbumTextView = (TextView)retValue.findViewById( R.id.qli_album_name );
					views.PlayDuration = (TextView)retValue.findViewById( R.id.qli_play_duration );

					retValue.setTag( views );
				}
			}
			else {
				views = (ViewHolder)retValue.getTag();
			}

			if(( views != null ) &&
			   ( position < mQueueList.size())) {
				PlayQueueTrack  track = mQueueList.get( position );

				if( track.isPlaying()) {
					views.NowPlaying.setVisibility( View.VISIBLE );
				}
				else {
					views.NowPlaying.setVisibility( View.INVISIBLE );
				}

				views.NameTextView.setText( track.getTrackName());
				views.AlbumTextView.setText( String.format( "(%s/%s)", track.getArtistName(), track.getAlbumName() ));

				int typeStyle = track.getIsStrategySourced() ? Typeface.ITALIC : Typeface.NORMAL;

				views.NameTextView.setTypeface( null, typeStyle );
				views.AlbumTextView.setTypeface( null, typeStyle );

				views.PlayDuration.setText(
						String.format( "%d:%02d",
								TimeUnit.MILLISECONDS.toMinutes( track.getDurationMilliseconds()),
								TimeUnit.MILLISECONDS.toSeconds( track.getDurationMilliseconds()) -
								TimeUnit.MINUTES.toSeconds( TimeUnit.MILLISECONDS.toMinutes( track.getDurationMilliseconds()))));

				if( track.getHasPlayed()) {
					views.NameTextView.setTextColor( mHasPlayedColor );
					views.AlbumTextView.setTextColor( mHasPlayedColor );
					views.PlayDuration.setTextColor( mHasPlayedColor );
				}
				else {
					views.NameTextView.setTextColor( mWillPlayColor );
					views.AlbumTextView.setTextColor( mWillPlayColor );
					views.PlayDuration.setTextColor( mWillPlayColor );
				}
			}

			return( retValue );
		}
	}
}
