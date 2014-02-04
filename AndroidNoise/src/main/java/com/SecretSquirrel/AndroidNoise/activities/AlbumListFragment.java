package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import com.SecretSquirrel.AndroidNoise.events.EventAlbumSelected;
import com.SecretSquirrel.AndroidNoise.events.EventPlayAlbum;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;
import com.SecretSquirrel.AndroidNoise.support.Constants;
import com.SecretSquirrel.AndroidNoise.support.NoiseUtils;
import com.SecretSquirrel.AndroidNoise.ui.FilteredArrayAdapter;
import com.SecretSquirrel.AndroidNoise.ui.ListViewFilter;
import com.SecretSquirrel.AndroidNoise.ui.ScaledHeightAnimation;
import com.SecretSquirrel.AndroidNoise.views.ButtonEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.greenrobot.event.EventBus;

public class AlbumListFragment extends Fragment
							   implements ServiceResultReceiver.Receiver,
										  FilteredArrayAdapter.FilteredListWatcher {
	private static final String     TAG = AlbumListFragment.class.getName();
	private static final String     ARTIST_KEY  = "albumListArtistId";
	private static final String     ALBUM_LIST  = "albumList";
	private static final String     LIST_STATE  = "albumListState";
	private static final String     FILTER_TEXT = "albumListFilterText";
	private static final String     FILTER_DISPLAYED = "albumListFilterDisplayed";

	private ServiceResultReceiver   mReceiver;
	private long                    mCurrentArtist;
	private ArrayList<Album>        mAlbumList;
	private ListView                mAlbumListView;
	private Parcelable              mAlbumListViewState;
	private AlbumAdapter            mAlbumListAdapter;
	private TextView                mAlbumCount;
	private ButtonEditText          mFilterEditText;
	private String                  mFilterText;
	private View                    mFilterPanel;
	private boolean                 mFilterPanelDisplayed;

	public static AlbumListFragment newInstance( long artistId ) {
		AlbumListFragment   fragment = new AlbumListFragment();
		Bundle              args = new Bundle();

		args.putLong( ARTIST_KEY, artistId );
		fragment.setArguments( args );

		return( fragment );
	}

	public AlbumListFragment() {
		mCurrentArtist = Constants.NULL_ID;
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		setHasOptionsMenu( true );

		if( savedInstanceState != null ) {
			mAlbumList = savedInstanceState.getParcelableArrayList( ALBUM_LIST );
			mAlbumListViewState = savedInstanceState.getParcelable( LIST_STATE );
			mFilterText = savedInstanceState.getString( FILTER_TEXT );
			mFilterPanelDisplayed = savedInstanceState.getBoolean( FILTER_DISPLAYED );
		}
		if( mAlbumList == null ) {
			mAlbumList = new ArrayList<Album>();
		}

		mAlbumListAdapter = new AlbumAdapter( getActivity(), mAlbumList );
		mAlbumListAdapter.setListWatcher( this );

		mReceiver = new ServiceResultReceiver( new Handler());
		mReceiver.setReceiver( this );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_album_list, container, false );

		if( myView != null ) {
			mAlbumListView = (ListView)myView.findViewById( R.id.al_album_list_view );

			mAlbumListView.setAdapter( mAlbumListAdapter );
			mAlbumListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick( AdapterView<?> adapterView, View view, int i, long l ) {
					Album album = mAlbumListAdapter.getItemAtPosition( i );

					if( album != null ) {
						EventBus.getDefault().post( new EventAlbumSelected( album ));
					}
				}
			} );

			mFilterEditText = (ButtonEditText)myView.findViewById( R.id.al_album_filter );
			mFilterEditText.addTextChangedListener( new TextWatcher() {
				@Override
				public void onTextChanged( CharSequence charSequence, int i, int i2, int i3 ) {
					mAlbumListAdapter.setFilterText( charSequence );
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

			mAlbumCount = (TextView)myView.findViewById( R.id.al_list_count );

			mFilterPanel = myView.findViewById( R.id.al_filter_panel );
			displayFilterPanel( mFilterPanelDisplayed, false );

			if( savedInstanceState != null ) {
				if( mAlbumListViewState != null ) {
					mAlbumListView.onRestoreInstanceState( mAlbumListViewState );
				}

				mCurrentArtist = savedInstanceState.getLong( ARTIST_KEY );
			}
			else {
				Bundle  args = getArguments();

				if( args != null ) {
					mCurrentArtist = args.getLong( ARTIST_KEY, Constants.NULL_ID );
				}
			}
		}

		if( mCurrentArtist != Constants.NULL_ID ) {
			if( mAlbumList.size() == 0 ) {
				getApplicationState().getDataClient().GetAlbumList( mCurrentArtist, mReceiver );
			}
		}
		else {
			if( Constants.LOG_ERROR ) {
				Log.e( TAG, "The current artist could not be determined." );
			}
		}

		return( myView );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putLong( ARTIST_KEY, mCurrentArtist );
		outState.putParcelableArrayList( ALBUM_LIST, mAlbumList );

		if( mAlbumListView != null ) {
			mAlbumListViewState = mAlbumListView.onSaveInstanceState();
		}
		if( mAlbumListViewState != null ) {
			outState.putParcelable( LIST_STATE, mAlbumListViewState );
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		if( mAlbumListAdapter != null ) {
			mAlbumListAdapter.setListWatcher( null );
		}

		if( mReceiver != null ) {
			mReceiver.clearReceiver();
		}
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

	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		inflater.inflate( R.menu.album_list, menu );

		super.onCreateOptionsMenu( menu, inflater );
	}

	@Override
	public void onPrepareOptionsMenu( Menu menu ) {
		MenuItem filterItem = menu.findItem( R.id.action_filter_album_list );

		if( filterItem != null ) {
			filterItem.setTitle( mAlbumListAdapter.getHaveFilteredItems() ? R.string.action_filter_album_list_on :
																			R.string.action_filter_album_list );
		}

		super.onPrepareOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		boolean retValue = true;

		switch( item.getItemId()) {
			case R.id.action_filter_album_list:
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
		if( mAlbumCount != null ) {
			mAlbumCount.setText( String.format( "%d albums", itemCount ));
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
			}

			if( withAnimation ) {
				animation.setDuration( 300 );
			}

			mFilterPanel.startAnimation( animation );
		}

		mFilterPanelDisplayed = display;
	}

	private void setAlbumList( ArrayList<Album> albumList ) {
		mAlbumList.clear();
		mAlbumList.addAll( albumList );

		Collections.sort( mAlbumList, new Comparator<Album>() {
			public int compare( Album album1, Album album2 ) {
				return( album1.getName().compareToIgnoreCase( album2.getName()));
			}
		} );

		mAlbumListAdapter.notifyDataSetChanged();
	}

	private IApplicationState getApplicationState() {
		NoiseRemoteApplication application = (NoiseRemoteApplication)getActivity().getApplication();

		return( application.getApplicationState());
	}

	private class AlbumAdapter extends FilteredArrayAdapter<Album>
							   implements Filterable, ListViewFilter.FilterClient<Album> {
		private Context             mContext;
		private LayoutInflater      mLayoutInflater;

		private class ViewHolder {
			public Button       PlayButton;
			public TextView     TitleTextView;
			public TextView     TrackCountTextView;
			public TextView     PublishedTextView;
			public TextView     PublishedHeaderTextView;
		}

		public AlbumAdapter( Context context, ArrayList<Album> albumList ) {
			super( context, R.layout.artist_list_item, albumList );
			mContext = context;

			mLayoutInflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		}

		@Override
		public boolean shouldItemBeDisplayed( Album item, String filterText ) {
			boolean retValue = false;
			String  lowerText = filterText.toLowerCase();

			if( item.getName().toLowerCase().contains( lowerText )) {
				retValue = true;
			}

			return( retValue );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View        retValue = convertView;
			ViewHolder  views = null;

			if( convertView == null ) {
				retValue = mLayoutInflater.inflate( R.layout.album_list_item, parent, false );

				if( retValue != null ) {
					views = new ViewHolder();
					views.TitleTextView = (TextView)retValue.findViewById( R.id.ali_title );
					views.TrackCountTextView = (TextView)retValue.findViewById( R.id.ali_track_count );
					views.PublishedTextView = (TextView)retValue.findViewById( R.id.ali_published );
					views.PublishedHeaderTextView = (TextView)retValue.findViewById( R.id.ali_published_header );
					views.PlayButton = (Button)retValue.findViewById( R.id.play_button );

					views.PlayButton.setOnClickListener( new View.OnClickListener() {
						@Override
						public void onClick( View view ) {
							Album   album = (Album)view.getTag();

							if( album != null ) {
								EventBus.getDefault().post( new EventPlayAlbum( album ));
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
			   ( position < getCount())) {
				Album      album = getItem( position );

				views.PlayButton.setTag( album );
				views.TitleTextView.setText( album.getName());
				views.TrackCountTextView.setText( String.format( "%d", album.getTrackCount()));
				if( album.getHasPublishedYear()) {
					views.PublishedTextView.setText( NoiseUtils.FormatPublishedYear( getActivity(), album.getPublishedYear()));
					views.PublishedTextView.setVisibility( View.VISIBLE );
					views.PublishedHeaderTextView.setVisibility( View.VISIBLE );
				}
				else {
					views.PublishedTextView.setVisibility( View.INVISIBLE );
					views.PublishedHeaderTextView.setVisibility( View.INVISIBLE );
				}
			}

			return( retValue );
		}
	}
}
