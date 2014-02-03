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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.events.EventArtistSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;
import com.SecretSquirrel.AndroidNoise.ui.FilteredArrayAdapter;
import com.SecretSquirrel.AndroidNoise.ui.ListViewFilter;
import com.SecretSquirrel.AndroidNoise.ui.ScaledHeightAnimation;
import com.SecretSquirrel.AndroidNoise.views.ButtonEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import de.greenrobot.event.EventBus;

public class ArtistListFragment extends Fragment
								implements ServiceResultReceiver.Receiver,
										   FilteredArrayAdapter.FilteredListWatcher {
	private static final String     ARTIST_LIST = "artistList";
	private static final String     LIST_STATE  = "artistListState";
	private static final String     FILTER_TEXT = "artistListFilterText";
	private static final String     FILTER_DISPLAYED = "artistListFilterDisplayed";

	private ServiceResultReceiver   mServiceResultReceiver;
	private ArrayList<Artist>       mArtistList;
	private ListView                mArtistListView;
	private ButtonEditText          mFilterEditText;
	private TextView                mArtistCount;
	private Parcelable              mListViewState;
	private ArtistAdapter           mArtistListAdapter;
	private String                  mFilterText;
	private View                    mFilterPanel;
	private boolean                 mFilterPanelDisplayed;

	public static ArtistListFragment newInstance() {
		return( new ArtistListFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		setHasOptionsMenu( true );

		if( savedInstanceState != null ) {
			mArtistList = savedInstanceState.getParcelableArrayList( ARTIST_LIST );
			mListViewState = savedInstanceState.getParcelable( LIST_STATE );
			mFilterText = savedInstanceState.getString( FILTER_TEXT );
			mFilterPanelDisplayed = savedInstanceState.getBoolean( FILTER_DISPLAYED );
		}
		if( mArtistList == null ) {
			mArtistList = new ArrayList<Artist>();
		}

		mArtistListAdapter = new ArtistAdapter( getActivity(), mArtistList );
		mArtistListAdapter.setListWatcher( this );

		mServiceResultReceiver = new ServiceResultReceiver( new Handler());
		mServiceResultReceiver.setReceiver( this );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_artist_list, container, false );

		if( myView != null ) {
			mArtistListView = (ListView) myView.findViewById( R.id.al_artist_list_view );

			mArtistListView.setAdapter( mArtistListAdapter );
			mArtistListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick( AdapterView<?> adapterView, View view, int i, long l ) {
					Artist  artist = mArtistListAdapter.getItemAtPosition( i );

					if( artist != null ) {
						selectArtist( artist );
					}
				}
			} );

			mFilterEditText = (ButtonEditText)myView.findViewById( R.id.al_artist_filter );
			mFilterEditText.addTextChangedListener( new TextWatcher() {
				@Override
				public void onTextChanged( CharSequence charSequence, int i, int i2, int i3 ) {
					mArtistListAdapter.setFilterText( charSequence );
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

			if( mListViewState != null ) {
				mArtistListView.onRestoreInstanceState( mListViewState );
			}

			mArtistCount = (TextView)myView.findViewById( R.id.al_list_count );

			mFilterPanel = myView.findViewById( R.id.al_filter_panel );
			displayFilterPanel( mFilterPanelDisplayed, false );
		}

		if(( mArtistList.size() == 0 ) &&
		   ( getApplicationState().getIsConnected())) {
			getApplicationState().getDataClient().GetArtistList( mServiceResultReceiver );
		}

		return( myView );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putParcelableArrayList( ARTIST_LIST, mArtistList );

		if( mArtistListView != null ) {
			mListViewState = mArtistListView.onSaveInstanceState();
		}
		if( mListViewState != null ) {
			outState.putParcelable( LIST_STATE, mListViewState );
		}

		if(( mFilterEditText != null ) &&
		   ( mFilterEditText.getText() != null )) {
			mFilterText = mFilterEditText.getText().toString();
		}
		if(!TextUtils.isEmpty( mFilterText )) {
			outState.putString( FILTER_TEXT, mFilterText );
		}

		outState.putBoolean( FILTER_DISPLAYED, mFilterPanelDisplayed );
	}

	@Override
	public void onPause() {
		super.onPause();

		if( mArtistListAdapter != null ) {
			mArtistListAdapter.setListWatcher( null );
		}

		mServiceResultReceiver.clearReceiver();
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			ArrayList<Artist>   artistList = resultData.getParcelableArrayList( NoiseRemoteApi.ArtistList );

			setArtistList( artistList );
		}
	}

	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		inflater.inflate( R.menu.artist_list, menu );

		super.onCreateOptionsMenu( menu, inflater );
	}

	@Override
	public void onPrepareOptionsMenu( Menu menu ) {
		MenuItem    filterItem = menu.findItem( R.id.action_filter_artist_list );

		if( filterItem != null ) {
			filterItem.setTitle( mArtistListAdapter.getHaveFilteredItems() ? R.string.action_filter_artist_list_on :
																			 R.string.action_filter_artist_list );
		}

		super.onPrepareOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		boolean retValue = true;

		switch( item.getItemId()) {
			case R.id.action_filter_artist_list:
				displayFilterPanel( !mFilterPanelDisplayed, true );
				break;

			default:
				retValue = super.onOptionsItemSelected( item );
				break;
		}

		return( retValue );
	}

	public void setArtistList( ArrayList<Artist> artistList ) {
		mArtistList.clear();
		mArtistList.addAll( artistList );

		Collections.sort( mArtistList, new Comparator<Artist>() {
			public int compare( Artist artist1, Artist artist2 ) {
				return (artist1.getName().compareToIgnoreCase( artist2.getName()));
			}
		} );

		mArtistListAdapter.notifyDataSetChanged();
	}

	private void selectArtist( Artist artist ) {
		if( artist != null ) {
			EventBus.getDefault().post( new EventArtistSelected( artist ));
		}
	}

	@Override
	public void onListChanged( int itemCount ) {
		if( mArtistCount != null ) {
			mArtistCount.setText( String.format( "%d artists", itemCount ));
		}

		// update the action menu with the filter state.
		ActivityCompat.invalidateOptionsMenu( getActivity());
	}

	private void displayFilterPanel( boolean display, boolean withAnimation ) {
		if( mFilterPanel != null ) {
			Animation   animation;

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

	private IApplicationState getApplicationState() {
		NoiseRemoteApplication application = (NoiseRemoteApplication)getActivity().getApplication();

		return( application.getApplicationState());
	}

	private class ArtistAdapter extends FilteredArrayAdapter<Artist>
								implements SectionIndexer, Filterable, ListViewFilter.FilterClient<Artist> {
		private Context                     mContext;
		private LayoutInflater              mLayoutInflater;
		private HashMap<String, Integer>    mAlphaIndexer;
		private String[]                    mSections;

		private class ViewHolder {
			public TextView     NameTextView;
			public TextView     AlbumCountTextView;
			public TextView     GenreTextView;
		}

		public ArtistAdapter( Context context, ArrayList<Artist> artistList ) {
			super( context, R.layout.artist_list_item, artistList );
			mContext = context;

			mLayoutInflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		}

		@Override
		public boolean shouldItemBeDisplayed( Artist item, String filterText ) {
			boolean retValue = false;
			String  lowerText = filterText.toLowerCase();

			if(( item.getName().toLowerCase().contains( lowerText )) ||
			   ( item.getGenre().toLowerCase().contains( lowerText ))) {
				retValue = true;
			}

			return( retValue );
		}

		@Override
		protected void onListUpdated() {
			updateAlphaIndex();
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View        retValue = convertView;
			ViewHolder  views = null;

			if( convertView == null ) {
				retValue = mLayoutInflater.inflate( R.layout.artist_list_item, parent, false );

				if( retValue != null ) {
					views = new ViewHolder();
					views.NameTextView = (TextView)retValue.findViewById( R.id.artist_list_item_name );
					views.AlbumCountTextView = (TextView)retValue.findViewById( R.id.artist_list_item_albumCount );
					views.GenreTextView = (TextView)retValue.findViewById( R.id.artist_list_item_genre );

					retValue.setTag( views );
				}
			}
			else {
				views = (ViewHolder)retValue.getTag();
			}

			if(( views != null ) &&
			   ( position < getCount())) {
				Artist      artist = getItem( position );

				views.NameTextView.setText( artist.getName());
				views.AlbumCountTextView.setText( "Albums: " + artist.getAlbumCount());
				views.GenreTextView.setText( artist.getGenre());
			}

			return( retValue );
		}

		private void updateAlphaIndex() {
			if( mAlphaIndexer == null ) {
				mAlphaIndexer = new HashMap<String, Integer>();
			}
			else {
				mAlphaIndexer.clear();
			}

			for( int index = 0; index < getCount(); index++ ) {
				String firstChar = getItem( index ).getName().substring( 0, 1 ).toUpperCase();

				// put only if the key does not exist
				if(!mAlphaIndexer.containsKey( firstChar )) {
					mAlphaIndexer.put( firstChar, index );
				}
			}

			// create a list from the set to sort
			ArrayList<String> sectionList = new ArrayList<String>( mAlphaIndexer.keySet());

			Collections.sort( sectionList );

			mSections = new String[sectionList.size()];

			sectionList.toArray( mSections );
		}

		@Override
		public Object[] getSections() {
			return( mSections );
		}

		@Override
		public int getPositionForSection( int section ) {
			return( mAlphaIndexer.get( mSections[section]));
		}

		@Override
		public int getSectionForPosition( int position ) {
			//Iterate over the sections to find the closest index
			//that is not greater than the position
			int closestIndex = 0;
			int latestDelta = Integer.MAX_VALUE;

			for( int index = 0; index < mSections.length; index++ ) {
				int current = mAlphaIndexer.get( mSections[index]);
				if( current == position ) {
					//If position matches an index, return it immediately
					return( index );
				} else if( current < position ) {
					//Check if this is closer than the last index we inspected
					int delta = position - current;
					if( delta < latestDelta ) {
						closestIndex = index;
						latestDelta = delta;
					}
				}
			}

			return( closestIndex );
		}
	}
}
