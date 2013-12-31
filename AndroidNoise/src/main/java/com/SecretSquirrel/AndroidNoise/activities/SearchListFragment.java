package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/30/13.

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.SearchResult;
import com.SecretSquirrel.AndroidNoise.dto.SearchResultItem;
import com.SecretSquirrel.AndroidNoise.events.EventSearchRequest;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.support.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.greenrobot.event.EventBus;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.util.functions.Action1;

public class SearchListFragment extends Fragment {
	private final String                TAG = SearchListFragment.class.getName();

	private ListView                    mSearchListView;
	private ArrayList<SearchResultItem> mResultList;
	private SearchResultAdapter         mSearchListAdapter;
	private Subscription                mSearchSubscription;

	public static SearchListFragment newInstance() {
		return( new SearchListFragment());
	}
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mResultList = new ArrayList<SearchResultItem>();
		mSearchListAdapter = new SearchResultAdapter( getActivity(), mResultList );

		EventBus.getDefault().register( this );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_search_list, container, false );

		mSearchListView = (ListView) myView.findViewById( R.id.search_list_view );
		mSearchListView.setAdapter( mSearchListAdapter );

		return( myView );
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		clearSubscription();
		EventBus.getDefault().unregister( this );
	}

	private void clearSubscription() {
		if( mSearchSubscription != null ) {
			mSearchSubscription.unsubscribe();
			mSearchSubscription = null;
		}
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventSearchRequest args ) {
		mResultList.clear();
		mSearchListAdapter.notifyDataSetChanged();

		if(!TextUtils.isEmpty( args.getSearchTerm())) {
			clearSubscription();

			mSearchSubscription = AndroidObservable.fromFragment( this, getApplicationState().getSearchClient().Search( args.getSearchTerm()))
					.subscribe( new Action1<SearchResult>() {
							@Override
							public void call( SearchResult searchResult ) {
								setResultList( searchResult.getResults());
							}
						},
						new Action1<Throwable>() {
							@Override
							public void call( Throwable throwable ) {
								if( Constants.LOG_ERROR ) {
									Log.e( TAG, "SearchListFragment:search failed", throwable );
								}
							}
						});
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
		clearSubscription();
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
