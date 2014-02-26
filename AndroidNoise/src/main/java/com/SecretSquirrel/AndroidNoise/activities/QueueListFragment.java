package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/30/13.

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.PlayQueueTrack;
import com.SecretSquirrel.AndroidNoise.events.EventAlbumNameRequest;
import com.SecretSquirrel.AndroidNoise.events.EventQueueUpdated;
import com.SecretSquirrel.AndroidNoise.interfaces.IQueueStatus;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class QueueListFragment extends Fragment  {
	private static final String         QUEUE_LIST  = "queueList";
	private static final String         LIST_STATE  = "queueListState";

	private ArrayList<PlayQueueTrack>   mQueueList;
	private ListView                    mQueueListView;
	private QueueAdapter                mQueueListAdapter;
	private Parcelable                  mQueueListState;

	@Inject EventBus                    mEventBus;
	@Inject	IQueueStatus                mQueueStatus;

	public static QueueListFragment newInstance() {
		return( new QueueListFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

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

					EventBus.getDefault().post( new EventAlbumNameRequest( track.getArtistName(), track.getAlbumName()));
				}
			} );

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

	@SuppressWarnings( "unused" )
	public void onEvent( EventQueueUpdated args ) {
		setQueueList( args.getQueueList());
	}

	private void updateQueueList() {
		setQueueList( mQueueStatus.getPlayQueueItems());
	}

	private void setQueueList( ArrayList<PlayQueueTrack> queueList ) {
		mQueueList.clear();
		mQueueList.addAll( queueList );
		mQueueListAdapter.notifyDataSetChanged();
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
