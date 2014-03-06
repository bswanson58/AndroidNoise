package com.SecretSquirrel.AndroidNoise.activities;// Created by BSwanson on 3/5/14.

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.dto.ArtistTrack;
import com.SecretSquirrel.AndroidNoise.dto.TrackAssociation;
import com.SecretSquirrel.AndroidNoise.events.EventArtistTrackAlbumRequest;
import com.SecretSquirrel.AndroidNoise.events.EventPlayTrack;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;
import com.SecretSquirrel.AndroidNoise.support.NoiseUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class ArtistTracksAlbumsFragment extends Fragment
										implements ServiceResultReceiver.Receiver {
	private static final String     ARTIST       = "artist";
	private static final String     ARTIST_TRACK = "artistTrack";
	private static final String     ALBUM_LIST   = "albumList";

	private Artist                  mArtist;
	private ArtistTrack             mArtistTrack;
	private ArrayList<Album>        mAlbumList;
	private AlbumListAdapter        mListAdapter;

	@Inject	EventBus                mEventBus;
	@Inject ServiceResultReceiver   mReceiver;
	@Inject	INoiseData              mNoiseData;

	@InjectView( R.id.ata_track_name )	TextView    mTrackNameView;
	@InjectView( R.id.ata_album_list )	ListView    mAlbumListView;

	public static ArtistTracksAlbumsFragment newInstance( Artist artist, ArtistTrack track ) {
		ArtistTracksAlbumsFragment  fragment = new ArtistTracksAlbumsFragment();
		Bundle                      args = new Bundle();

		args.putParcelable( ARTIST, artist );
		args.putParcelable( ARTIST_TRACK, track );
		fragment.setArguments( args );

		return( fragment );
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		if( savedInstanceState != null ) {
			mArtist = savedInstanceState.getParcelable( ARTIST );
			mArtistTrack = savedInstanceState.getParcelable( ARTIST_TRACK );
			mAlbumList = savedInstanceState.getParcelableArrayList( ALBUM_LIST );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				mArtist = args.getParcelable( ARTIST );
				mArtistTrack = args.getParcelable( ARTIST_TRACK );
				mAlbumList = args.getParcelableArrayList( ALBUM_LIST );
			}
		}

		if( mAlbumList == null ) {
			mAlbumList = new ArrayList<Album>();
		}

		mListAdapter = new AlbumListAdapter( getActivity(), mArtistTrack.getTracks());

		if( mArtist == null ) {
			Timber.e( "Artist was not set." );
		}
		if( mArtistTrack == null ) {
			Timber.e( "ArtistTrack was not set." );
		}
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_artist_tracks_albums, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );

			mAlbumListView.setAdapter( mListAdapter );

			updateDisplay();
		}

		return( myView );
	}

	@Override
	public void onResume() {
		super.onResume();

		if( mAlbumList.size() == 0 ) {
			mReceiver.setReceiver( this );

			mNoiseData.GetAlbumList( mArtist.getArtistId(), mReceiver );
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		mReceiver.clearReceiver();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		ButterKnife.reset( this );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putParcelable( ARTIST, mArtist );
		outState.putParcelable( ARTIST_TRACK, mArtistTrack );
		outState.putParcelableArrayList( ALBUM_LIST, mAlbumList );
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			int callCode = resultData.getInt( NoiseRemoteApi.RemoteApiParameter );

			switch( callCode ) {
				case NoiseRemoteApi.GetAlbumList:
					ArrayList<Album>    albumList = resultData.getParcelableArrayList( NoiseRemoteApi.AlbumList );

					setAlbumList( albumList );
					break;
			}
		}
	}

	@SuppressWarnings( "unused" )
	@OnClick( R.id.ata_close )
	public void onClickClose() {
		mEventBus.post( new EventArtistTrackAlbumRequest());
	}

	private void setAlbumList( List<Album> albumList ) {
		mAlbumList.clear();
		mAlbumList.addAll( albumList );

		mListAdapter.notifyDataSetChanged();
	}

	private void updateDisplay() {
		if( mArtistTrack != null ) {
			mTrackNameView.setText( mArtistTrack.getTrackName());
		}
	}

	protected class AlbumListAdapter extends ArrayAdapter<TrackAssociation> {
		private Context         mContext;
		private LayoutInflater  mLayoutInflater;
		private final String    mTrackNumberFormat;
		private final String    mVolumeNameFormat;
		private final String    mPublishedYearFormat;

		protected class ViewHolder {
			public ViewHolder( View view ) {
				ButterKnife.inject( this, view );
			}

			@InjectView( R.id.play_button )         Button      PlayButton;
			@InjectView( R.id.ati_album_name )      TextView    AlbumNameView;
			@InjectView( R.id.ati_track_number )    TextView    TrackNumberView;
			@InjectView( R.id.ati_volume_name )     TextView    VolumeNameView;
			@InjectView( R.id.ati_duration )        TextView    DurationView;
			@InjectView( R.id.ati_published )       TextView    PublishedView;

			@SuppressWarnings( "unused" )
			@OnClick( R.id.play_button )
			public void onPlayClick( View view ) {
				TrackAssociation track = (TrackAssociation)view.getTag();

				if( track != null ) {
					mEventBus.post( new EventPlayTrack( mArtist.getArtistId(),
														track.getTrackId(),
														mArtistTrack.getTrackName()));
				}
			}
		}

		public AlbumListAdapter( Context context, List<TrackAssociation> albumList ) {
			super( context, R.layout.artist_track_album_item, albumList );
			mContext = context;

			mTrackNumberFormat = getString( R.string.track_number_format );
			mVolumeNameFormat = getString( R.string.volume_name_format );
			mPublishedYearFormat = getString( R.string.published_year_format );

			mLayoutInflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View        retValue = convertView;
			ViewHolder  views = null;

			if( convertView == null ) {
				retValue = mLayoutInflater.inflate( R.layout.artist_track_album_item, parent, false );

				if( retValue != null ) {
					views = new ViewHolder( retValue );

					retValue.setTag( views );
				}
			}
			else {
				views = (ViewHolder)retValue.getTag();
			}

			displayListItem( views, position );

			return( retValue );
		}

		private void displayListItem( ViewHolder views, int position ) {
			if(( views != null ) &&
			   ( position < getCount())) {
				TrackAssociation    track = getItem( position );
				Album               album = getAlbum( track.getAlbumId());

				if( album != null ) {
					views.AlbumNameView.setText( album.getName());

					if( album.getHasPublishedYear()) {
						views.PublishedView.setText(
								String.format( mPublishedYearFormat,
										NoiseUtils.FormatPublishedYear( getActivity(), album.getPublishedYear())));
					}
					else {
						views.PublishedView.setText( "" );
					}
				}

				views.TrackNumberView.setText( String.format( mTrackNumberFormat, track.getTrackNumber()));

				if( TextUtils.isEmpty( track.getVolumeName() )) {
					views.VolumeNameView.setText( "" );
				}
				else {
					views.VolumeNameView.setText( String.format( mVolumeNameFormat, track.getVolumeName()));
				}

				views.DurationView.setText( NoiseUtils.formatTrackDuration( track.getDurationMilliseconds()));

				views.PlayButton.setTag( track );
			}
		}

		private Album getAlbum( long albumId ) {
			Album   retValue = null;

			for( Album album : mAlbumList ) {
				if( album.getAlbumId() == albumId ) {
					retValue = album;

					break;
				}
			}

			return( retValue );
		}
	}
}
