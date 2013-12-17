package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/17/13.

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Album;

import java.util.ArrayList;

public class AlbumListFragment extends Fragment {
	private ListView            mAlbumListView;
	private ArrayList<Album>    mAlbumList;
	private AlbumAdapter        mAlbumListAdapter;

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mAlbumList = new ArrayList<Album>();
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_album_list, container, false );

		mAlbumListView = (ListView)myView.findViewById( R.id.AlbumListView );
		mAlbumListAdapter = new AlbumAdapter( getActivity(), mAlbumList );
		mAlbumListView.setAdapter( mAlbumListAdapter );


		return( myView );
	}

	private class AlbumAdapter extends ArrayAdapter<Album> {
		private Context             mContext;
		private LayoutInflater      mLayoutInflater;
		private ArrayList<Album>    mAlbumList;

		private class ViewHolder {
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
				retValue = mLayoutInflater.inflate( R.layout.artist_list_item, parent, false );

				views = new ViewHolder();
				views.NameTextView = (TextView)retValue.findViewById( R.id.album_list_item_name );

				retValue.setTag( views );
			}
			else {
				views = (ViewHolder)retValue.getTag();
			}

			if(( views != null ) &&
			   ( position < mAlbumList.size())) {
				Album      album = mAlbumList.get( position );

				views.NameTextView.setText( album.Name );
			}

			return( retValue );
		}
	}
}
