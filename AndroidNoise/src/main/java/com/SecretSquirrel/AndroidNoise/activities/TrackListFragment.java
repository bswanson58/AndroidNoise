package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/18/13.

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Track;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TrackListFragment extends Fragment
							   implements ServiceResultReceiver.Receiver {
	private ListView                mTrackListView;
	private ArrayList<Track>        mTrackList;
	private TrackAdapter            mTrackListAdapter;
	private ServiceResultReceiver   mReceiver;
	private Album                   mCurrentAlbum;

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mTrackList = new ArrayList<Track>();

		mReceiver = new ServiceResultReceiver( new Handler());
		mReceiver.setReceiver( this );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_track_list, container, false );

		mTrackListView = (ListView)myView.findViewById( R.id.TrackListView );
		mTrackListAdapter = new TrackAdapter( getActivity(), mTrackList );
		mTrackListView.setAdapter( mTrackListAdapter );

		mCurrentAlbum = getApplicationState().getCurrentAlbum();
		if( mCurrentAlbum != null ) {
			getApplicationState().getDataClient().GetTrackList( mCurrentAlbum.AlbumId, mReceiver );
		}

		return( myView );
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

	public void setTrackList( ArrayList<Track> trackList ) {
		mTrackList.clear();
		mTrackList.addAll( trackList );

		Collections.sort( mTrackList, new Comparator<Track>() {
			public int compare( Track track1, Track track2 ) {
				return ( track1.TrackNumber - track2.TrackNumber );
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
			public Button   PlayButton;
			public TextView NameTextView;
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

				views = new ViewHolder();
				views.NameTextView = (TextView)retValue.findViewById( R.id.track_list_item_name );

				views.PlayButton = (Button) retValue.findViewById( R.id.play_button );
				views.PlayButton.setOnClickListener( new View.OnClickListener() {
					@Override
					public void onClick( View view ) {
						Track   track = (Track)view.getTag();

						if( track != null ) {
							//IViewListener listener = (IViewListener)getActivity();

							//listener.getQueueRequestListener().PlayTrack( track );
						}
					}
				} );

				retValue.setTag( views );
			}
			else {
				views = (ViewHolder)retValue.getTag();
			}

			if(( views != null ) &&
			   ( position < mTrackList.size())) {
				Track track = mTrackList.get( position );

				views.PlayButton.setTag( track );
				views.NameTextView.setText( track.Name );
			}

			return( retValue );
		}
	}
}
