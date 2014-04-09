package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by BSwanson on 12/18/13.

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Track;
import com.SecretSquirrel.AndroidNoise.events.EventPlayTrack;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;
import com.SecretSquirrel.AndroidNoise.support.ChainedComparator;
import com.SecretSquirrel.AndroidNoise.support.Constants;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;
import com.SecretSquirrel.AndroidNoise.support.NoiseUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class TrackListFragment extends Fragment
							   implements ServiceResultReceiver.Receiver {
	private static final String     ALBUM_KEY   = "trackListAlbumId";
	private static final String     TRACK_LIST  = "trackList";
	private static final String     LIST_STATE  = "trackListState";

	private ArrayList<Track>        mTrackList;
	private ListView                mTrackListView;
	private Parcelable              mTrackListState;
	private TrackAdapter            mTrackListAdapter;
	private long                    mCurrentAlbum;

	@Inject	INoiseData              mNoiseData;
	@Inject ServiceResultReceiver   mReceiver;

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

		IocUtility.inject( this );

		if( savedInstanceState != null ) {
			mTrackList = savedInstanceState.getParcelableArrayList( TRACK_LIST );
			mTrackListState = savedInstanceState.getParcelable( LIST_STATE );
			mCurrentAlbum = savedInstanceState.getLong( ALBUM_KEY, Constants.NULL_ID );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				mCurrentAlbum = args.getLong( ALBUM_KEY, Constants.NULL_ID );
			}
		}

		if( mTrackList == null ) {
			mTrackList = new ArrayList<Track>();
		}

		mTrackListAdapter = new TrackAdapter( getActivity(), mTrackList );

		if( mCurrentAlbum == Constants.NULL_ID ) {
			Timber.e( "The current album could not be determined. " );
		}
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_track_list, container, false );

		if( myView != null ) {
			mTrackListView = (ListView)myView.findViewById( R.id.TrackListView );

			mTrackListView.setAdapter( mTrackListAdapter );

			if( mTrackListState != null ) {
				mTrackListView.onRestoreInstanceState( mTrackListState );
			}
		}

		return( myView );
	}

	@Override
	public void onResume() {
		super.onResume();

		if( mCurrentAlbum != Constants.NULL_ID ) {
			if( mTrackList.size() == 0 ) {
				mReceiver.setReceiver( this );

				mNoiseData.GetTrackList( mCurrentAlbum, mReceiver );
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		mReceiver.clearReceiver();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		mTrackListView = null;
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putLong( ALBUM_KEY, mCurrentAlbum );
		outState.putParcelableArrayList( TRACK_LIST, mTrackList );
		if( mTrackListView != null ) {
			mTrackListState = mTrackListView.onSaveInstanceState();
		}
		if( mTrackListState != null ) {
			outState.putParcelable( LIST_STATE, mTrackListState );
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

		Comparator<Track> compareVolume = new Comparator<Track>() {
			@Override
			public int compare( Track o1, Track o2) {
				return( o1.getVolumeName().compareToIgnoreCase( o2.getVolumeName()));
			}
		};

		Comparator<Track> compareTrack = new Comparator<Track>() {
			@Override
			public int compare( Track o1, Track o2 ) {
				return( o1.getTrackNumber() - o2.getTrackNumber());
			}
		};

		final ChainedComparator<Track> comparator = new ChainedComparator<Track>( compareVolume, compareTrack );

		Collections.sort( mTrackList, new Comparator<Track>() {
			public int compare( Track track1, Track track2 ) {
				return ( comparator.compare( track1, track2 ));
			}
		} );

		mTrackListAdapter.notifyDataSetChanged();
	}

	protected class TrackAdapter extends ArrayAdapter<Track> {
		private final Context           mContext;
		private final LayoutInflater    mLayoutInflater;
		private final ArrayList<Track>  mTrackList;
		private final String            mTrackNumberFormat;
		private final String            mVolumeNameFormat;

		protected class ViewHolder {
			public ViewHolder( View view ) {
				ButterKnife.inject( this, view );
			}

			@InjectView( R.id.play_button )	        Button      PlayButton;
			@InjectView( R.id.tli_track_number )    TextView    TrackNumberTextView;
			@InjectView( R.id.tli_name )            TextView    NameTextView;
			@InjectView( R.id.tli_duration )        TextView    DurationTextView;
			@InjectView( R.id.tli_volume_name )     TextView    VolumeNameView;
			@InjectView( R.id.tli_favorite )		CheckBox    FavoriteView;
			@InjectView( R.id.tli_rating )			RatingBar   RatingView;

			@SuppressWarnings( "unused" )
			@OnClick( R.id.play_button )
			public void onClick( View view ) {
				Track   track = (Track)view.getTag();

				if( track != null ) {
					EventBus.getDefault().post( new EventPlayTrack( track ));
				}
			}
		}

		public TrackAdapter( Context context, ArrayList<Track> trackList ) {
			super( context, R.layout.track_list_item, trackList );
			mContext = context;
			mTrackList = trackList;

			mTrackNumberFormat = getString( R.string.track_number_format );
			mVolumeNameFormat = getString( R.string.volume_name_format );

			mLayoutInflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View        retValue = convertView;
			ViewHolder  views = null;

			if( convertView == null ) {
				retValue = mLayoutInflater.inflate( R.layout.track_list_item, parent, false );

				if( retValue != null ) {
					views = new ViewHolder( retValue );

					retValue.setTag( views );
				}
			}
			else {
				views = (ViewHolder)retValue.getTag();
			}

			if(( views != null ) &&
			   ( position < mTrackList.size())) {
				Track track = mTrackList.get( position );

				views.TrackNumberTextView.setText( String.format( mTrackNumberFormat, track.getTrackNumber()));
				views.PlayButton.setTag( track );
				views.NameTextView.setText( track.getTrackName());

				if( TextUtils.isEmpty( track.getVolumeName())) {
					views.VolumeNameView.setText( "" );
				}
				else {
					views.VolumeNameView.setText( String.format( mVolumeNameFormat, track.getVolumeName()));
				}

				views.FavoriteView.setChecked( track.getIsFavorite());
				views.RatingView.setRating( track.getRating());
				views.DurationTextView.setText( NoiseUtils.formatTrackDuration( track.getDurationMilliseconds()));
			}

			return( retValue );
		}
	}
}
