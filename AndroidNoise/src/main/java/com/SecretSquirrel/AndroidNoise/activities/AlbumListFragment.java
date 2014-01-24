package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.greenrobot.event.EventBus;

public class AlbumListFragment extends Fragment
							   implements ServiceResultReceiver.Receiver {
	private static final String     TAG = AlbumListFragment.class.getName();
	private static final String     ARTIST_KEY   = "AlbumListFragment_ArtistId";

	private ServiceResultReceiver   mReceiver;
	private long                    mCurrentArtist;
	private ArrayList<Album>        mAlbumList;
	private AlbumAdapter            mAlbumListAdapter;

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

		mAlbumList = new ArrayList<Album>();
		mAlbumListAdapter = new AlbumAdapter( getActivity(), mAlbumList );
		mReceiver = new ServiceResultReceiver( new Handler());
		mReceiver.setReceiver( this );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_album_list, container, false );

		if( myView != null ) {
			ListView albumListView = (ListView)myView.findViewById( R.id.al_album_list_view );

			albumListView.setAdapter( mAlbumListAdapter );

			albumListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick( AdapterView<?> adapterView, View view, int i, long l ) {
					Album album = mAlbumList.get( i );

					if( album != null ) {
						EventBus.getDefault().post( new EventAlbumSelected( album ));
					}
				}
			} );
		}

		if( savedInstanceState != null ) {
			mCurrentArtist = savedInstanceState.getLong( ARTIST_KEY );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				mCurrentArtist = args.getLong( ARTIST_KEY, Constants.NULL_ID );
			}
		}

		if( mCurrentArtist != Constants.NULL_ID ) {
			getApplicationState().getDataClient().GetAlbumList( mCurrentArtist, mReceiver );
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
				case NoiseRemoteApi.GetAlbumList:
					ArrayList<Album>    albumList = resultData.getParcelableArrayList( NoiseRemoteApi.AlbumList );

					setAlbumList( albumList );
					break;
			}
		}
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

	private class AlbumAdapter extends ArrayAdapter<Album> {
		private Context             mContext;
		private LayoutInflater      mLayoutInflater;
		private ArrayList<Album>    mAlbumList;

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
			mAlbumList = albumList;

			mLayoutInflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
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
			   ( position < mAlbumList.size())) {
				Album      album = mAlbumList.get( position );

				views.PlayButton.setTag( album );
				views.TitleTextView.setText( album.getName());
				views.TrackCountTextView.setText( String.format( " %d", album.getTrackCount()));
				if( album.getPublishedYear() > 1000 ) {
					views.PublishedTextView.setText( String.format( " %d", album.getPublishedYear()));
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
