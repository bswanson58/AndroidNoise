package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 2/4/14.

import android.content.Context;
import android.os.Bundle;
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
import com.SecretSquirrel.AndroidNoise.events.EventArtistRequest;
import com.SecretSquirrel.AndroidNoise.events.EventRecentDataUpdated;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class RecentlyViewedListFragment extends Fragment {
	private ArrayList<Artist>       mArtistList;
	private RecentArtistListAdapter mListAdapter;
	private ListView                mRecentlyViewedList;

	@Inject IApplicationState       mApplicationState;

	public static RecentlyViewedListFragment newInstance() {
		return( new RecentlyViewedListFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		mArtistList = new ArrayList<Artist>();
		mListAdapter = new RecentArtistListAdapter( getActivity(), mArtistList );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_recently_viewed_list, container, false );

		if( myView != null ) {
			mRecentlyViewedList = (ListView)myView.findViewById( R.id.rvl_recently_viewed_list );
			mRecentlyViewedList.setAdapter( mListAdapter );
			mRecentlyViewedList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick( AdapterView<?> adapterView, View view, int i, long l ) {
					Artist artist = mArtistList.get( i );

					if( artist != null ) {
						EventBus.getDefault().post( new EventArtistRequest( artist.getArtistId()));
					}
				}
			} );
		}

		return( myView );
	}

	@Override
	public void onResume() {
		super.onResume();

		updateList();
		EventBus.getDefault().register( this );
	}

	@Override
	public void onPause() {
		super.onPause();

		EventBus.getDefault().unregister( this );
	}

	@SuppressWarnings("unused")
	public void onEvent( EventRecentDataUpdated args ) {
		updateList();
	}

	private void updateList() {
		mArtistList.clear();

		if(( mRecentlyViewedList != null ) &&
		   ( mApplicationState.getIsConnected())) {
			mArtistList.addAll( mApplicationState.getRecentData().getRecentlyViewedArtists() );
		}

		mListAdapter.notifyDataSetChanged();
	}

	private class RecentArtistListAdapter extends ArrayAdapter<Artist> {
		private LayoutInflater      mLayoutInflater;

		public RecentArtistListAdapter( Context context, List<Artist> artistList ) {
			super( context, R.layout.simple_list_item, artistList );

			mLayoutInflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		}

		private class ViewHolder {
			public TextView     TitleTextView;
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View        retValue = convertView;
			ViewHolder  views = null;

			if( convertView == null ) {
				retValue = mLayoutInflater.inflate( R.layout.recent_list_item, parent, false );

				if( retValue != null ) {
					views = new ViewHolder();
					views.TitleTextView = (TextView)retValue.findViewById( R.id.rli_title );

					retValue.setTag( views );
				}
			}
			else {
				views = (ViewHolder)retValue.getTag();
			}

			if(( views != null ) &&
			   ( position < getCount())) {
				Artist  artist = getItem( position );

				views.TitleTextView.setText( artist.getName() );
			}

			return( retValue );
		}
	}
}
