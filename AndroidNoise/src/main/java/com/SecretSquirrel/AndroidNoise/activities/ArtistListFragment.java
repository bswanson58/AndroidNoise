package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.events.EventArtistSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.IViewListener;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.greenrobot.event.EventBus;

public class ArtistListFragment extends Fragment
								implements ServiceResultReceiver.Receiver {
	private ServiceResultReceiver   mServiceResultReceiver;
	private ListView                mArtistListView;
	private ArrayList<Artist>       mArtistList;
	private ArtistAdapter           mArtistListAdapter;

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mArtistList = new ArrayList<Artist>();
		mArtistListAdapter = new ArtistAdapter( getActivity(), mArtistList );

		mServiceResultReceiver = new ServiceResultReceiver( new Handler());
		mServiceResultReceiver.setReceiver( this );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_artist_list, container, false );

		mArtistListView = (ListView) myView.findViewById( R.id.ArtistListView );
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

		if( getApplicationState().getIsConnected()) {
			getApplicationState().getDataClient().GetArtistList( mServiceResultReceiver );
		}

		return( myView );
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
				return (artist1.Name.compareToIgnoreCase( artist2.Name ));
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

	private class ArtistAdapter extends ArrayAdapter<Artist> {
		private Context mContext;
		private LayoutInflater mLayoutInflater;
		private ArrayList<Artist>   mArtistList;

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
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View        retValue = convertView;
			ViewHolder  views = null;

			if( convertView == null ) {
				retValue = mLayoutInflater.inflate( R.layout.artist_list_item, parent, false );

				views = new ViewHolder();
				views.NameTextView = (TextView)retValue.findViewById( R.id.artist_list_item_name );
				views.AlbumCountTextView = (TextView)retValue.findViewById( R.id.artist_list_item_albumCount );
				views.GenreTextView = (TextView)retValue.findViewById( R.id.artist_list_item_genre );

				retValue.setTag( views );
			}
			else {
				views = (ViewHolder)retValue.getTag();
			}

			if(( views != null ) &&
			   ( position < mArtistList.size())) {
				Artist      artist = mArtistList.get( position );

				views.NameTextView.setText( artist.Name );
				views.AlbumCountTextView.setText( "Albums: " + artist.AlbumCount );
				views.GenreTextView.setText( artist.Genre );
			}

			return( retValue );
		}
	}
}
