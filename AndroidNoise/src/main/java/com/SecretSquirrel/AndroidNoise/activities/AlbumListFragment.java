package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.IViewListener;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AlbumListFragment extends Fragment
							   implements ServiceResultReceiver.Receiver {
	private ServiceResultReceiver   mReceiver;
	private Artist                  mCurrentArtist;
	private ListView                mAlbumListView;
	private ArrayList<Album>        mAlbumList;
	private AlbumAdapter            mAlbumListAdapter;

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mAlbumList = new ArrayList<Album>();
		mReceiver = new ServiceResultReceiver( new Handler());
		mReceiver.setReceiver( this );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_album_list, container, false );

		mAlbumListView = (ListView)myView.findViewById( R.id.AlbumListView );
		mAlbumListAdapter = new AlbumAdapter( getActivity(), mAlbumList );
		mAlbumListView.setAdapter( mAlbumListAdapter );

		mAlbumListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick( AdapterView<?> adapterView, View view, int i, long l ) {
				Album album = mAlbumList.get( i );

				if( album != null ) {
					IViewListener listener = (IViewListener)getActivity();

					listener.getItemSelectedListener().OnAlbumSelected( album );
				}
			}
		} );

		mCurrentArtist = getApplicationState().getCurrentArtist();
		if( mCurrentArtist != null ) {
			getApplicationState().getDataClient().GetAlbumList( mCurrentArtist.ArtistId, mReceiver );
		}

		return( myView );
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

	public void setAlbumList( ArrayList<Album> albumList ) {
		mAlbumList.clear();
		mAlbumList.addAll( albumList );

		Collections.sort( mAlbumList, new Comparator<Album>() {
			public int compare( Album album1, Album album2 ) {
				return( album1.Name.compareToIgnoreCase( album2.Name ));
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
			public TextView     NameTextView;
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

				views = new ViewHolder();
				views.NameTextView = (TextView)retValue.findViewById( R.id.album_list_item_name );
				views.PlayButton = (Button)retValue.findViewById( R.id.play_button );

				views.PlayButton.setOnClickListener( new View.OnClickListener() {
					@Override
					public void onClick( View view ) {
						Album   album = (Album)view.getTag();

						if( album != null ) {
							IViewListener listener = (IViewListener)getActivity();

							listener.getQueueRequestListener().PlayAlbum( album );
						}
					}
				} );

				retValue.setTag( views );
			}
			else {
				views = (ViewHolder)retValue.getTag();
			}

			if(( views != null ) &&
			   ( position < mAlbumList.size())) {
				Album      album = mAlbumList.get( position );

				views.NameTextView.setText( album.Name );
				views.PlayButton.setTag( album );
			}

			return( retValue );
		}
	}
}
