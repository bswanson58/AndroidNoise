package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/30/13.

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.SearchResultItem;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchListFragment extends Fragment
								implements ServiceResultReceiver.Receiver {
	private ServiceResultReceiver       mServiceResultReceiver;
	private ListView                    mSearchListView;
	private ArrayList<SearchResultItem> mResultList;
	private SearchResultAdapter         mSearchListAdapter;

	public static SearchListFragment newInstance() {
		return( new SearchListFragment());
	}
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mResultList = new ArrayList<SearchResultItem>();
		mSearchListAdapter = new SearchResultAdapter( getActivity(), mResultList );

		mServiceResultReceiver = new ServiceResultReceiver( new Handler());
		mServiceResultReceiver.setReceiver( this );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_search_list, container, false );

		mSearchListView = (ListView) myView.findViewById( R.id.search_list_view );
		mSearchListView.setAdapter( mSearchListAdapter );

		return( myView );
	}

	@Override
	public void onPause() {
		super.onPause();

		mServiceResultReceiver.clearReceiver();
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
//			ArrayList<SearchResultItem>   resultList = resultData.getParcelableArrayList( NoiseRemoteApi.ArtistList );

//			setResultList( resultList );
		}
	}

	public void setResultList( ArrayList<SearchResultItem> resultList ) {
		mResultList.clear();
		mResultList.addAll( resultList );

		Collections.sort( mResultList, new Comparator<SearchResultItem>() {
			public int compare( SearchResultItem result1, SearchResultItem result2 ) {
				return( result1.getArtistName().compareToIgnoreCase( result2.getArtistName()));
			}
		} );

		mSearchListAdapter.notifyDataSetChanged();
	}

	private IApplicationState getApplicationState() {
		NoiseRemoteApplication application = (NoiseRemoteApplication)getActivity().getApplication();

		return( application.getApplicationState());
	}

	private class SearchResultAdapter extends ArrayAdapter<SearchResultItem> {
		private Context                     mContext;
		private LayoutInflater              mLayoutInflater;
		private ArrayList<SearchResultItem> mResultList;

		private class ViewHolder {
			public TextView NameTextView;
		}

		public SearchResultAdapter( Context context, ArrayList<SearchResultItem> resultList ) {
			super( context, R.layout.search_list_item, resultList );
			mContext = context;
			mResultList = resultList;

			mLayoutInflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View        retValue = convertView;
			ViewHolder  views = null;

			if( convertView == null ) {
				retValue = mLayoutInflater.inflate( R.layout.search_list_item, parent, false );

				views = new ViewHolder();
				views.NameTextView = (TextView)retValue.findViewById( R.id.search_list_item_name );

				retValue.setTag( views );
			}
			else {
				views = (ViewHolder)retValue.getTag();
			}

			if(( views != null ) &&
			   ( position < mResultList.size())) {
				SearchResultItem    result = mResultList.get( position );

				views.NameTextView.setText( result.getArtistName());
			}

			return( retValue );
		}
	}
}
