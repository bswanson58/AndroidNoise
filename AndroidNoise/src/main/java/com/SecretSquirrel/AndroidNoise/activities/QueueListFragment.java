package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/30/13.

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.PlayQueueTrack;
import com.SecretSquirrel.AndroidNoise.events.EventAlbumRequest;
import com.SecretSquirrel.AndroidNoise.events.EventQueueUpdated;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
import com.SecretSquirrel.AndroidNoise.interfaces.IQueueStatus;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;
import com.SecretSquirrel.AndroidNoise.support.NoiseUtils;
import com.SecretSquirrel.AndroidNoise.views.RevealingListView.DefaultRevealingListViewListener;
import com.SecretSquirrel.AndroidNoise.views.RevealingListView.RevealingListView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

public class QueueListFragment extends Fragment  {
	private static final String         TAG = QueueListFragment.class.getName();

	private static final String         QUEUE_LIST  = "queueList";
	private static final String         LIST_STATE  = "queueListState";

	private ArrayList<PlayQueueTrack>   mQueueList;
	private QueueAdapter                mQueueListAdapter;
	private Parcelable                  mQueueListState;

	@Inject EventBus                    mEventBus;
	@Inject	INoiseQueue                 mNoiseQueue;
	@Inject	IQueueStatus                mQueueStatus;

	@InjectView( R.id.ql_queue_list )	RevealingListView   mQueueListView;

	public static QueueListFragment newInstance() {
		return( new QueueListFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		setHasOptionsMenu( true );

		if( savedInstanceState != null ) {
			mQueueList = savedInstanceState.getParcelableArrayList( QUEUE_LIST );
			mQueueListState = savedInstanceState.getParcelable( LIST_STATE );
		}
		if( mQueueList == null ) {
			mQueueList = new ArrayList<PlayQueueTrack>();
		}

		mQueueListAdapter = new QueueAdapter( getActivity(), mQueueList );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_queue_list, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );

			mQueueListView.setAdapter( mQueueListAdapter );
			mQueueListView.setEmptyView( myView.findViewById( R.id.ql_empty_view ) );
			mQueueListView.setRevealingListViewListener( new DefaultRevealingListViewListener() {
				@Override
				public void onItemClicked( int position ) {
					PlayQueueTrack  track = mQueueList.get( position );

					if( track != null ) {
						mEventBus.post( new EventAlbumRequest( track.getArtistId(), track.getAlbumId()));
					}
				}

				@Override
				public void onRevealOpened( int position, int action ) {
					if( action == 101 ) {
						PlayQueueTrack  track = mQueueList.get( position );

						if( track != null ) {
							executeItemCommand( INoiseQueue.QueueItemCommand.Remove, track );
						}
					}
				}
			});

			if( mQueueListState != null ) {
				mQueueListView.onRestoreInstanceState( mQueueListState );
			}
		}

		return( myView );
	}

	@Override
	public void onResume() {
		super.onResume();

		updateQueueList();
		mEventBus.register( this );
	}

	@Override
	public void onPause() {
		super.onPause();

		mEventBus.unregister( this );
		ButterKnife.reset( this );
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		mQueueListView = null;
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putParcelableArrayList( QUEUE_LIST, mQueueList );

		if( mQueueListView != null ) {
			mQueueListState = mQueueListView.onSaveInstanceState();
		}
		if( mQueueListState != null ) {
			outState.putParcelable( LIST_STATE, mQueueListState );
		}
	}

	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		inflater.inflate( R.menu.queue_list, menu );

		super.onCreateOptionsMenu( menu, inflater );
	}

	@Override
	public void onPrepareOptionsMenu( Menu menu ) {
		MenuItem    item = menu.findItem( R.id.action_queue_clear_played );

		if( item != null ) {
			item.setEnabled( mQueueStatus.areTracksPlayed());
		}

		item = menu.findItem( R.id.action_queue_clear );
		if( item != null ) {
			item.setEnabled( mQueueStatus.areTracksQueued() );
		}

		item = menu.findItem( R.id.action_queue_start_play );
		if( item != null ) {
			item.setEnabled(!mQueueStatus.areTracksQueued());
		}

		super.onPrepareOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		boolean     retValue = true;

		switch( item.getItemId()) {
			case R.id.action_queue_start_play:
				executeCommand( INoiseQueue.QueueCommand.StartPlaying );
				break;

			case R.id.action_queue_clear:
				executeCommand( INoiseQueue.QueueCommand.Clear );
				break;

			case R.id.action_queue_clear_played:
				executeCommand( INoiseQueue.QueueCommand.ClearPlayed );
				break;

			default:
				retValue = super.onOptionsItemSelected( item );
				break;
		}

		return( retValue );
	}

	private void executeCommand( INoiseQueue.QueueCommand command ) {
		AndroidObservable.fromFragment( this, mNoiseQueue.ExecuteQueueCommand( command ))
				.subscribe( new Action1<BaseServerResult>() {
					@Override
					public void call( BaseServerResult serverResult ) {
						if( !serverResult.Success ) {
							Log.e( TAG, "The queue command was not executed: " + serverResult.ErrorMessage );
						}
					}
				} );
	}

	private void executeItemCommand( INoiseQueue.QueueItemCommand command, PlayQueueTrack track ) {
		AndroidObservable.fromFragment( this, mNoiseQueue.ExecuteQueueItemCommand( command, track.getId()))
				.subscribe( new Action1<BaseServerResult>() {
					@Override
					public void call( BaseServerResult serverResult ) {
						if( !serverResult.Success ) {
							Log.e( TAG, "The queue item command was not executed: " + serverResult.ErrorMessage );
						}
					}
				} );
	}

	private void executeTransportCommand( INoiseQueue.TransportCommand command ) {
		AndroidObservable.fromFragment( this, mNoiseQueue.ExecuteTransportCommand( command ))
				.subscribe( new Action1<BaseServerResult>() {
					@Override
					public void call( BaseServerResult serverResult ) {
						if(!serverResult.Success ) {
							Log.e( TAG, "The transport command was not executed: " + serverResult.ErrorMessage );
						}
					}
				} );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventQueueUpdated args ) {
		updateQueueList();
	}

	private void updateQueueList() {
		mQueueList.clear();
		mQueueList.addAll( mQueueStatus.getPlayQueueItems());
		mQueueListAdapter.notifyDataSetChanged();

		ActivityCompat.invalidateOptionsMenu( getActivity() );
	}

	protected class QueueAdapter extends ArrayAdapter<PlayQueueTrack> {
		private Context                     mContext;
		private LayoutInflater              mLayoutInflater;
		private ArrayList<PlayQueueTrack>   mQueueList;
		private int                         mWillPlayColor;
		private int                         mHasPlayedColor;
		private final String                mArtistAlbumFormat;

		@SuppressWarnings( "unused" )
		protected class ViewHolder {
			private PlayQueueTrack  mTrack;

			public ViewHolder( View view ) {
				ButterKnife.inject( this, view );
			}

			@InjectView( R.id.qli_now_playing )     View         NowPlaying;
			@InjectView( R.id.qli_item_name )       TextView     NameTextView;
			@InjectView( R.id.qli_album_name )      TextView     AlbumTextView;
			@InjectView( R.id.qli_play_duration )   TextView     PlayDuration;

			public void setTrack( PlayQueueTrack track ) {
				mTrack = track;
			}

			@OnClick( R.id.qli_play_continue )
			public void onContinuePlay( View view ) {
				executeItemCommand( INoiseQueue.QueueItemCommand.PlayNext, mTrack );
			}

			@OnClick( R.id.qli_replay_track )
			public void onReplayTrack( View view ) {
				if( mTrack.isPlaying()) {
					executeTransportCommand( INoiseQueue.TransportCommand.Repeat );
				}
				else {
					executeItemCommand( INoiseQueue.QueueItemCommand.Replay, mTrack );
				}
			}
		}

		public QueueAdapter( Context context, ArrayList<PlayQueueTrack> queueList ) {
			super( context, R.layout.queue_list_item, queueList );
			mContext = context;
			mQueueList = queueList;

			mWillPlayColor = getResources().getColor( R.color.TitleText );
			mHasPlayedColor = getResources().getColor( R.color.HasPlayed );
			mArtistAlbumFormat = getString( R.string.artist_album_format );

			mLayoutInflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View        retValue = convertView;
			ViewHolder  views = null;

			if( convertView == null ) {
				retValue = mLayoutInflater.inflate( R.layout.queue_list_item, parent, false );

				if( retValue != null ) {
					views = new ViewHolder( retValue );
					
					retValue.setTag( views );
				}
			}
			else {
				views = (ViewHolder)retValue.getTag();
			}

			if(( views != null ) &&
			   ( position < mQueueList.size())) {
				PlayQueueTrack  track = mQueueList.get( position );

				views.setTrack( track );
				views.NameTextView.setText( track.getTrackName());
				views.AlbumTextView.setText( String.format( mArtistAlbumFormat, track.getArtistName(), track.getAlbumName()));

				int typeStyle = track.getIsStrategySourced() ? Typeface.ITALIC : Typeface.NORMAL;

				views.NameTextView.setTypeface( null, typeStyle );
				views.AlbumTextView.setTypeface( null, typeStyle );
				views.PlayDuration.setText( NoiseUtils.formatTrackDuration( track.getDurationMilliseconds()));

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

				if( track.isPlaying()) {
					views.NowPlaying.setVisibility( View.VISIBLE );

					if( track.getIsStrategySourced()) {
						views.NameTextView.setTypeface( null, Typeface.BOLD_ITALIC );
					}
					else {
						views.NameTextView.setTypeface( null, Typeface.BOLD );
					}
				}
				else {
					views.NowPlaying.setVisibility( View.INVISIBLE );
				}
			}

			return( retValue );
		}
	}
}
