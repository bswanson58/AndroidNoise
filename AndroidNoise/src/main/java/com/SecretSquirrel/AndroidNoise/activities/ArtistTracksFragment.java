package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by BSwanson on 3/4/14.

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.dto.ArtistTrack;
import com.SecretSquirrel.AndroidNoise.dto.ArtistTrackList;
import com.SecretSquirrel.AndroidNoise.dto.TrackAssociation;
import com.SecretSquirrel.AndroidNoise.events.EventArtistTrackAlbumRequest;
import com.SecretSquirrel.AndroidNoise.events.EventNavigationUpEnable;
import com.SecretSquirrel.AndroidNoise.events.EventPlayTrack;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;
import com.SecretSquirrel.AndroidNoise.support.NoiseUtils;
import com.SecretSquirrel.AndroidNoise.ui.FilteredArrayAdapter;
import com.SecretSquirrel.AndroidNoise.ui.ListViewFilter;
import com.SecretSquirrel.AndroidNoise.ui.ScaledHeightAnimation;
import com.SecretSquirrel.AndroidNoise.views.ButtonEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class ArtistTracksFragment extends Fragment
								  implements ServiceResultReceiver.Receiver,
											 FilteredArrayAdapter.FilteredListWatcher{
	private static final String     ARTIST_KEY  = "artistTracksArtist";
	private static final String     TRACKS_LIST = "artistTracksList";
	private static final String     ALBUMS_LIST = "artistAlbumsList";
	private static final String     LIST_STATE  = "artistTracksListState";
	private static final String     FILTER_TEXT = "artistTracksFilterText";
	private static final String     FILTER_DISPLAYED = "artistTracksFilterDisplayed";

	private Artist                  mCurrentArtist;
	private ArrayList<ArtistTrack>  mTracks;
	private ArrayList<Album>        mAlbums;
	private ArtistTracksAdapter     mListAdapter;
	private Parcelable              mTracksListViewState;
	private String                  mFilterText;
	private boolean                 mFilterPanelDisplayed;

	@Inject	EventBus                mEventBus;
	@Inject	INoiseData              mNoiseData;
	@Inject	ServiceResultReceiver   mReceiver;

	@InjectView( R.id.at_track_list )	ListView        mTracksListView;
	@InjectView( R.id.at_list_count )   TextView        mTrackCount;
	@InjectView( R.id.at_filter_panel ) View            mFilterPanel;
	@InjectView( R.id.at_track_filter )	ButtonEditText  mFilterEditText;

	public static ArtistTracksFragment newInstance( Artist artist ) {
		ArtistTracksFragment    fragment = new ArtistTracksFragment();
		Bundle                  args = new Bundle();

		args.putParcelable( ARTIST_KEY, artist );

		fragment.setArguments( args );

		return( fragment );
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		setHasOptionsMenu( true );

		if( savedInstanceState != null ) {
			mCurrentArtist = savedInstanceState.getParcelable( ARTIST_KEY );
			mTracks = savedInstanceState.getParcelableArrayList( TRACKS_LIST );
			mAlbums = savedInstanceState.getParcelableArrayList( ALBUMS_LIST );
			mFilterText = savedInstanceState.getString( FILTER_TEXT );
			mFilterPanelDisplayed = savedInstanceState.getBoolean( FILTER_DISPLAYED );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				mCurrentArtist = args.getParcelable( ARTIST_KEY );
			}
		}

		if( mTracks == null ) {
			mTracks = new ArrayList<ArtistTrack>();
		}
		if( mAlbums == null ) {
			mAlbums = new ArrayList<Album>();
		}

		mListAdapter = new ArtistTracksAdapter( getActivity(), mTracks );

		if(  mCurrentArtist == null ) {
			Timber.e( "The current artist could not be determined." );
		}
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_artist_tracks, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );

			mTracksListView.setAdapter( mListAdapter );

			mTracksListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick( AdapterView<?> adapterView, View view, int i, long l ) {
					ArtistTrack track = mListAdapter.getItem( i );

					if( track.getTracks().size() > 1 ) {
						mEventBus.post( new EventArtistTrackAlbumRequest( mCurrentArtist, track ));
					}
				}
			} );

			mFilterEditText.addTextChangedListener( new TextWatcher() {
				@Override
				public void onTextChanged( CharSequence charSequence, int i, int i2, int i3 ) {
					mListAdapter.setFilterText( charSequence );
				}
				@Override
				public void beforeTextChanged( CharSequence charSequence, int i, int i2, int i3 ) {	}
				@Override
				public void afterTextChanged( Editable editable ) {	}
			} );
			mFilterEditText.setDrawableClickListener( new ButtonEditText.DrawableClickListener() {
				@Override
				public void onClick( ButtonEditText.DrawableClickListener.DrawablePosition target ) {
					// Clear the edit box and the search results.
					mFilterEditText.setText( "" );
				}
			} );

			if(!TextUtils.isEmpty( mFilterText )) {
				mFilterEditText.setText( mFilterText );
			}

			displayFilterPanel( mFilterPanelDisplayed, false );

			if(( savedInstanceState != null ) &&
			   ( mTracksListViewState != null )) {
				mTracksListView.onRestoreInstanceState( mTracksListViewState );
			}
		}

		return( myView );
	}

	@Override
	public void onResume() {
		super.onResume();

		mListAdapter.setListWatcher( this );

		if( mTracks.size() == 0 ) {
			mReceiver.setReceiver( this );

			mNoiseData.GetArtistTracks( mCurrentArtist.getArtistId(), mReceiver );
		}

		if( mAlbums.size() == 0 ) {
			mReceiver.setReceiver( this );

			mNoiseData.GetAlbumList( mCurrentArtist.getArtistId(), mReceiver );
		}

		mEventBus.post( new EventNavigationUpEnable());
	}

	@Override
	public void onPause() {
		super.onPause();

		mListAdapter.setListWatcher( null );
		mReceiver.clearReceiver();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		ButterKnife.reset( this );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putParcelable( ARTIST_KEY, mCurrentArtist );
		outState.putParcelableArrayList( TRACKS_LIST, mTracks );
		outState.putParcelableArrayList( ALBUMS_LIST, mAlbums );

		if( mTracksListView != null ) {
			mTracksListViewState = mTracksListView.onSaveInstanceState();
		}
		if( mTracksListViewState != null ) {
			outState.putParcelable( LIST_STATE, mTracksListViewState );
		}

		outState.putString( FILTER_TEXT, mFilterText );
		outState.putBoolean( FILTER_DISPLAYED, mFilterPanelDisplayed );
	}

	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		inflater.inflate( R.menu.artist_tracks, menu );

		super.onCreateOptionsMenu( menu, inflater );
	}

	@Override
	public void onPrepareOptionsMenu( Menu menu ) {
		MenuItem filterItem = menu.findItem( R.id.action_filter_track_list );

		if( filterItem != null ) {
			filterItem.setTitle( mListAdapter.getHaveFilteredItems() ? R.string.action_filter_track_list_on :
																	   R.string.action_filter_track_list );
		}

		super.onPrepareOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		boolean retValue = true;

		switch( item.getItemId()) {
			case android.R.id.home:
				getActivity().onBackPressed();
				break;

			case R.id.action_filter_track_list:
				displayFilterPanel( !mFilterPanelDisplayed, true );
				break;

			default:
				retValue = super.onOptionsItemSelected( item );
				break;
		}

		return( retValue );
	}

	@Override
	public void onListChanged( int itemCount ) {
		if( mTrackCount != null ) {
			mTrackCount.setText( String.format( getString( R.string.track_count_format ), itemCount ));
		}

		// update the action menu with the filter state.
		ActivityCompat.invalidateOptionsMenu( getActivity() );
	}

	private void displayFilterPanel( boolean display, boolean withAnimation ) {
		if( mFilterPanel != null ) {
			Animation animation;

			if( display ) {
				animation = new ScaledHeightAnimation( mFilterPanel, 0, 1 );
			}
			else {
				animation = new ScaledHeightAnimation( mFilterPanel, 1, 0 );

				NoiseUtils.hideKeyboard( getActivity());
			}

			if( withAnimation ) {
				animation.setDuration( 300 );
			}

			mFilterPanel.startAnimation( animation );
		}

		mFilterPanelDisplayed = display;
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			int callCode = resultData.getInt( NoiseRemoteApi.RemoteApiParameter );

			if( callCode == NoiseRemoteApi.GetArtistTracks ) {
				ArtistTrackList tracksList = resultData.getParcelable( NoiseRemoteApi.ArtistTrackList );

				if(( tracksList != null ) &&
				   ( tracksList.getArtistId() == mCurrentArtist.getArtistId())) {
					setTracksList( tracksList );
				}
				else {
					Timber.i( "Received an artist tracks list for the wrong artist." );
				}
			}
			else if( callCode == NoiseRemoteApi.GetAlbumList ) {
				ArrayList<Album>    albumList = resultData.getParcelableArrayList( NoiseRemoteApi.AlbumList );

				setAlbumList( albumList );
			}
			else {
				Timber.i( "Requested an artist tracks list, but received %d", callCode );
			}
		}
		else {
			Timber.i( "Artist track list was not received successfully" );
		}
	}

	private void setTracksList( ArtistTrackList trackList ) {
		mTracks.clear();
		mTracks.addAll( trackList.getTracks());

		Collections.sort( mTracks, new Comparator<ArtistTrack>() {
			public int compare( ArtistTrack track1, ArtistTrack track2 ) {
				return( track1.getTrackName().compareToIgnoreCase( track2.getTrackName()));
			}
		} );

		mListAdapter.notifyDataSetChanged();
	}

	private void setAlbumList( ArrayList<Album> albumList ) {
		if( albumList != null ) {
			mAlbums = albumList;
		}

		mListAdapter.notifyDataSetChanged();
	}

	protected class ArtistTracksAdapter extends FilteredArrayAdapter<ArtistTrack>
										implements Filterable, ListViewFilter.FilterClient<ArtistTrack> {
		private final Context           mContext;
		private final LayoutInflater    mLayoutInflater;
		private final String            mMultipleAlbums;
		private final String            mPublishedYearFormat;

		protected class ViewHolder {
			@InjectView( R.id.play_button )     View        PlayButton;
			@InjectView( R.id.atl_track_name )  TextView    TrackNameTextView;
			@InjectView( R.id.atl_album_name )  TextView    AlbumNameTextView;
			@InjectView( R.id.atl_duration )    TextView    DurationView;
			@InjectView( R.id.atl_published )   TextView    PublishedView;

			public ViewHolder( View view ) {
				ButterKnife.inject( this, view );
			}

			@SuppressWarnings( "unused" )
			@OnClick( R.id.play_button )
			public void onClickPlay( Button button ) {
				ArtistTrack track = (ArtistTrack)button.getTag();

				if( track != null ) {
					mEventBus.post( new EventPlayTrack( mCurrentArtist.getArtistId(),
														track.getTracks().get( 0 ).getTrackId(),
														track.getTrackName()));
				}
			}
		}

		public ArtistTracksAdapter( Context context, ArrayList<ArtistTrack> trackList ) {
			super( context, R.layout.artist_tracks_list_item, trackList );
			mContext = context;

			mMultipleAlbums = getString( R.string.atl_multiple_albums );
			mPublishedYearFormat = getString( R.string.published_year_format );

			mLayoutInflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		}

		@Override
		public boolean shouldItemBeDisplayed( ArtistTrack item, String filterText ) {
			boolean retValue = false;
			String  lowerText = filterText.toLowerCase();

			if( item.getTrackName().toLowerCase().contains( lowerText )) {
				retValue = true;
			}

			return( retValue );
		}

		private Album getAlbum( long albumId ) {
			Album   retValue = null;

			for( Album album : mAlbums ) {
				if( album.getAlbumId() == albumId ) {
					retValue = album;

					break;
				}
			}

			return( retValue );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View        retValue = convertView;
			ViewHolder  views = null;

			if( convertView == null ) {
				retValue = mLayoutInflater.inflate( R.layout.artist_tracks_list_item, parent, false );

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
				ArtistTrack     track = getItem( position );

				views.TrackNameTextView.setText( track.getTrackName());

				if( track.getTracks().size() == 1 ) {
					TrackAssociation    association = track.getTracks().get( 0 );
					Album               album = getAlbum( association.getAlbumId());

					views.PlayButton.setVisibility( View.VISIBLE );

					if( album != null ) {
						views.AlbumNameTextView.setText( album.getName());

						if( album.getHasPublishedYear()) {
							views.PublishedView.setText(
									String.format( mPublishedYearFormat,
											NoiseUtils.FormatPublishedYear( getActivity(), album.getPublishedYear())));
						}
						else {
							views.PublishedView.setText( "" );
						}
					}
					else {
						views.AlbumNameTextView.setText( "" );
						views.PublishedView.setText( "" );
					}

					views.DurationView.setText( NoiseUtils.formatTrackDuration( association.getDurationMilliseconds()));
				}
				else {
					views.PlayButton.setVisibility( View.INVISIBLE );

					views.PublishedView.setText( "" );
					views.DurationView.setText( "" );

					views.AlbumNameTextView.setText( mMultipleAlbums );
				}

				views.PlayButton.setTag( track );
			}
		}
	}
}
