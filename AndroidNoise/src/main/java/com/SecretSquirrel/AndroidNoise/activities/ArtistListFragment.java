package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import de.greenrobot.event.EventBus;

public class ArtistListFragment extends Fragment
								implements ServiceResultReceiver.Receiver {
	private final String            ARTIST_LIST = "artistList";
	private final String            LIST_STATE = "artistListState";

	private ServiceResultReceiver   mServiceResultReceiver;
	private ArrayList<Artist>       mArtistList;
	private ListView                mArtistListView;
	private ArtistAdapter           mArtistListAdapter;

	public static ArtistListFragment newInstance() {
		return( new ArtistListFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		if( savedInstanceState != null ) {
			mArtistList = savedInstanceState.getParcelableArrayList( ARTIST_LIST );
		}
		if( mArtistList == null ) {
			mArtistList = new ArrayList<Artist>();
		}

		mArtistListAdapter = new ArtistAdapter( getActivity(), mArtistList );

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
					Artist  artist = mArtistList.get( i );

					if( artist != null ) {
						selectArtist( artist );
					}
				}
			} );

			if( savedInstanceState != null ) {
				Parcelable  listState = savedInstanceState.getParcelable( LIST_STATE );

				if( listState != null ) {
					mArtistListView.onRestoreInstanceState( listState );
				}
			}
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
//		outState.putParcelable( LIST_STATE, mArtistListView.onSaveInstanceState());
	}

	@Override
	public void onPause() {
		super.onPause();

		mServiceResultReceiver.clearReceiver();
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			ArrayList<Artist>   artistList = resultData.getParcelableArrayList( NoiseRemoteApi.ArtistList );

			setArtistList( artistList );
		}
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

	private IApplicationState getApplicationState() {
		NoiseRemoteApplication application = (NoiseRemoteApplication)getActivity().getApplication();

		return( application.getApplicationState());
	}

	private class ArtistAdapter extends ArrayAdapter<Artist> implements SectionIndexer {
		private Context                     mContext;
		private LayoutInflater              mLayoutInflater;
		private ArrayList<Artist>           mArtistList;
		private HashMap<String, Integer>    mAlphaIndexer;
		private String[]                    mSections;

		private class ViewHolder {
			public TextView NameTextView;
			public TextView     AlbumCountTextView;
			public TextView     GenreTextView;
		}

		public ArtistAdapter( Context context, ArrayList<Artist> artistList ) {
			super( context, R.layout.artist_list_item, artistList );
			mContext = context;
			mArtistList = artistList;

			mLayoutInflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			mAlphaIndexer = new HashMap<String, Integer>();
			mSections = new String[0];
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
			   ( position < mArtistList.size())) {
				Artist      artist = mArtistList.get( position );

				views.NameTextView.setText( artist.getName());
				views.AlbumCountTextView.setText( "Albums: " + artist.getAlbumCount());
				views.GenreTextView.setText( artist.getGenre());
			}

			return( retValue );
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();

			updateAlphaIndex();
		}

		private void updateAlphaIndex() {
			mAlphaIndexer.clear();

			for( int index = 0; index < mArtistList.size(); index++ ) {
				String firstChar = mArtistList.get( index ).getName().substring( 0, 1 ).toUpperCase();

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
