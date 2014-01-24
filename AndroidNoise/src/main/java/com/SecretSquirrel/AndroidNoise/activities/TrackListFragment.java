package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/18/13.

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Track;
import com.SecretSquirrel.AndroidNoise.events.EventPlayTrack;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;
import com.SecretSquirrel.AndroidNoise.support.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

public class TrackListFragment extends Fragment
							   implements ServiceResultReceiver.Receiver {
	private static final String     TAG = TrackListFragment.class.getName();
	private static final String     ALBUM_KEY = "TrackListFragment_AlbumId";

	private ArrayList<Track>        mTrackList;
	private TrackAdapter            mTrackListAdapter;
	private ServiceResultReceiver   mReceiver;
	private long                    mCurrentAlbum;

	public static TrackListFragment newInstance( long albumId ) {
		TrackListFragment   fragment = new TrackListFragment();
		Bundle              args = new Bundle();

		args.putLong( ALBUM_KEY, albumId );
		fragment.setArguments( args );

		return( fragment );
	}

	public TrackListFragment() {
		mCurrentAlbum = Constants.NULL_ID;
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mTrackList = new ArrayList<Track>();
		mTrackListAdapter = new TrackAdapter( getActivity(), mTrackList );

		mReceiver = new ServiceResultReceiver( new Handler());
		mReceiver.setReceiver( this );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_track_list, container, false );

		if( myView != null ) {
			ListView    trackListView = (ListView)myView.findViewById( R.id.TrackListView );

			trackListView.setAdapter( mTrackListAdapter );
		}

		if( savedInstanceState != null ) {
			mCurrentAlbum = savedInstanceState.getLong( ALBUM_KEY, Constants.NULL_ID );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				mCurrentAlbum = args.getLong( ALBUM_KEY, Constants.NULL_ID );
			}
		}

		if( mCurrentAlbum != Constants.NULL_ID ) {
			getApplicationState().getDataClient().GetTrackList( mCurrentAlbum, mReceiver );
		}
		else {
			if( Constants.LOG_ERROR ) {
				Log.e( TAG, "The current album could not be determined. " );
			}
		}

		return( myView );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putLong( ALBUM_KEY, mCurrentAlbum );
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		if( mReceiver != null ) {
			mReceiver.clearReceiver();
		}
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			int callCode = resultData.getInt( NoiseRemoteApi.RemoteApiParameter );

			switch( callCode ) {
				case NoiseRemoteApi.GetTrackList:
					ArrayList<Track>    trackList = resultData.getParcelableArrayList( NoiseRemoteApi.TrackList );

					setTrackList( trackList );
					break;
			}
		}
	}

	private void setTrackList( ArrayList<Track> trackList ) {
		mTrackList.clear();
		mTrackList.addAll( trackList );

		Collections.sort( mTrackList, new Comparator<Track>() {
			public int compare( Track track1, Track track2 ) {
				return ( track1.getTrackNumber() - track2.getTrackNumber());
			}
		} );

		mTrackListAdapter.notifyDataSetChanged();
	}

	private IApplicationState getApplicationState() {
		NoiseRemoteApplication application = (NoiseRemoteApplication)getActivity().getApplication();

		return( application.getApplicationState());
	}

	private class TrackAdapter extends ArrayAdapter<Track> {
		private Context             mContext;
		private LayoutInflater      mLayoutInflater;
		private ArrayList<Track>    mTrackList;

		private class ViewHolder {
			public Button       PlayButton;
			public TextView     TrackNumberTextView;
			public TextView     NameTextView;
			public TextView     DurationTextView;
		}

		public TrackAdapter( Context context, ArrayList<Track> trackList ) {
			super( context, R.layout.track_list_item, trackList );
			mContext = context;
			mTrackList = trackList;

			mLayoutInflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View        retValue = convertView;
			ViewHolder  views = null;

			if( convertView == null ) {
				retValue = mLayoutInflater.inflate( R.layout.track_list_item, parent, false );

				if( retValue != null ) {
					views = new ViewHolder();
					views.TrackNumberTextView = (TextView)retValue.findViewById( R.id.tli_track_number );
					views.NameTextView = (TextView)retValue.findViewById( R.id.tli_name );
					views.DurationTextView = (TextView)retValue.findViewById( R.id.tli_duration );

					views.PlayButton = (Button) retValue.findViewById( R.id.play_button );
					views.PlayButton.setOnClickListener( new View.OnClickListener() {
						@Override
						public void onClick( View view ) {
							Track   track = (Track)view.getTag();

							if( track != null ) {
								EventBus.getDefault().post( new EventPlayTrack( track ));
							}
						}
					} );

					retValue.setTag( views );
				}
			}
			else {
				views = (ViewHolder)retValue.getTag();
			}

			if(( views != null ) &&
			   ( position < mTrackList.size())) {
				Track track = mTrackList.get( position );

				views.TrackNumberTextView.setText( String.format( "%d", track.getTrackNumber()));
				views.PlayButton.setTag( track );
				views.NameTextView.setText( track.getName() );
				views.DurationTextView.setText(
						String.format( "%d:%02d",
								TimeUnit.MILLISECONDS.toMinutes( track.getDurationMilliseconds()),
								TimeUnit.MILLISECONDS.toSeconds( track.getDurationMilliseconds()) -
								TimeUnit.MINUTES.toSeconds( TimeUnit.MILLISECONDS.toMinutes( track.getDurationMilliseconds()))));
			}

			return( retValue );
		}
	}
}
