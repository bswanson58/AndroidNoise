package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/30/13.

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.PlayQueueTrack;
import com.SecretSquirrel.AndroidNoise.events.EventAlbumRequest;
import com.SecretSquirrel.AndroidNoise.events.EventQueueUpdated;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseQueue;
import com.SecretSquirrel.AndroidNoise.interfaces.IQueueStatus;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;

public class QueueListFragment extends Fragment  {
	private static final String         TAG = QueueListFragment.class.getName();

	private static final String         QUEUE_LIST  = "queueList";
	private static final String         LIST_STATE  = "queueListState";

	private ArrayList<PlayQueueTrack>   mQueueList;
	private ListView                    mQueueListView;
	private QueueAdapter                mQueueListAdapter;
	private Parcelable                  mQueueListState;

	@Inject EventBus                    mEventBus;
	@Inject	INoiseQueue                 mNoiseQueue;
	@Inject	IQueueStatus                mQueueStatus;

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
			mQueueListView = (ListView) myView.findViewById( R.id.QueueListView );

			mQueueListView.setAdapter( mQueueListAdapter );
			mQueueListView.setEmptyView( myView.findViewById( R.id.ql_empty_view ) );
			mQueueListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick( AdapterView<?> adapterView, View view, int i, long l ) {
					PlayQueueTrack  track = mQueueList.get( i );

					mEventBus.post( new EventAlbumRequest( track.getArtistId(), track.getAlbumId()));
				}
			} );

			registerForContextMenu( mQueueListView );

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

	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo ) {
		super.onCreateContextMenu( menu, v, menuInfo );

		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate( R.menu.queue_list_item, menu );

		menu.setHeaderTitle( getString( R.string.menu_title_queue_list_item ));

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		if( info != null ) {
			PlayQueueTrack  track = mQueueListAdapter.getItem( info.position );
			MenuItem        item = menu.findItem( R.id.action_queue_item_replay );

			if(( item != null ) &&
			   ( track != null )) {
				item.setEnabled( track.getHasPlayed());
			}
		}
	}

	@Override
	public boolean onContextItemSelected( MenuItem item ) {
		boolean retValue = true;
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

		if( info != null ) {
			PlayQueueTrack track = mQueueListAdapter.getItem( info.position );

			if( track != null ) {
				switch( item.getItemId()) {
					case R.id.action_queue_item_remove:
						executeItemCommand( INoiseQueue.QueueItemCommand.Remove, track );
						break;

					case R.id.action_queue_item_playNext:
						executeItemCommand( INoiseQueue.QueueItemCommand.PlayNext, track );
						break;

					case R.id.action_queue_item_replay:
						executeItemCommand( INoiseQueue.QueueItemCommand.Replay, track );
						break;
					
					default:
						retValue = super.onContextItemSelected( item );
				}
			}
		}

		return( retValue );
	}

	private void executeItemCommand( INoiseQueue.QueueItemCommand command, PlayQueueTrack track ) {
		AndroidObservable.fromFragment( this, mNoiseQueue.ExecuteQueueItemCommand( command, track.getId()))
				.subscribe( new Action1<BaseServerResult>() {
					@Override
					public void call( BaseServerResult serverResult ) {
						if( !serverResult.Success ) {
							Log.e( TAG, "The queue command was not executed: " + serverResult.ErrorMessage );
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

		ActivityCompat.invalidateOptionsMenu( getActivity());
	}

	protected class QueueAdapter extends ArrayAdapter<PlayQueueTrack> {
		private Context                     mContext;
		private LayoutInflater              mLayoutInflater;
		private ArrayList<PlayQueueTrack>   mQueueList;
		private int                         mWillPlayColor;
		private int                         mHasPlayedColor;

		protected class ViewHolder {
			public ViewHolder( View view ) {
				ButterKnife.inject( this, view );
			}

			@InjectView( R.id.qli_now_playing )     View         NowPlaying;
			@InjectView( R.id.qli_item_name )       TextView     NameTextView;
			@InjectView( R.id.qli_album_name )      TextView     AlbumTextView;
			@InjectView( R.id.qli_play_duration )   TextView     PlayDuration;
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
