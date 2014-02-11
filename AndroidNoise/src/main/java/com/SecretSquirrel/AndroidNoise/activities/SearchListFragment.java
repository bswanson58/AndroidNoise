package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/30/13.

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.SecretSquirrel.AndroidNoise.dto.SearchResult;
import com.SecretSquirrel.AndroidNoise.dto.SearchResultItem;
import com.SecretSquirrel.AndroidNoise.events.EventAlbumRequest;
import com.SecretSquirrel.AndroidNoise.events.EventArtistRequest;
import com.SecretSquirrel.AndroidNoise.events.EventPlaySearchItem;
import com.SecretSquirrel.AndroidNoise.events.EventSearchRequest;
import com.SecretSquirrel.AndroidNoise.services.NoiseSearchClient;
import com.SecretSquirrel.AndroidNoise.support.Constants;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.util.functions.Action1;

public class SearchListFragment extends Fragment {
	private final String                TAG = SearchListFragment.class.getName();
	private final String                SEARCH_LIST = "searchList";
	private final String                LIST_STATE = "searchListState";

	private ArrayList<SearchResultItem> mResultList;
	private Parcelable                  mListViewState;
	private SearchResultAdapter         mSearchListAdapter;
	private Subscription                mSearchSubscription;

	@Inject EventBus                    mEventBus;
	@Inject	NoiseSearchClient           mSearchClient;

	@InjectView( R.id.sl_list_view )    ListView    mSearchListView;
	@InjectView( R.id.sl_no_results )   View        mNoResultsView;

	public static SearchListFragment newInstance() {
		return( new SearchListFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		if( savedInstanceState != null ) {
			mResultList = savedInstanceState.getParcelableArrayList( SEARCH_LIST );
			mListViewState = savedInstanceState.getParcelable( LIST_STATE );
		}
		if( mResultList == null ) {
			mResultList = new ArrayList<SearchResultItem>();
		}

		mSearchListAdapter = new SearchResultAdapter( getActivity(), mResultList );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_search_list, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );

			mNoResultsView.setVisibility( View.INVISIBLE );

			mSearchListView.setAdapter( mSearchListAdapter );
			mSearchListView.setEmptyView( myView.findViewById( R.id.sl_empty_view ));
			mSearchListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick( AdapterView<?> adapterView, View view, int i, long l ) {
					SearchResultItem    searchItem = mResultList.get( i );

					if( searchItem.getIsArtist()) {
						EventBus.getDefault().post( new EventArtistRequest( searchItem.getArtistId()));
					}
					else if(( searchItem.getIsAlbum()) ||
							( searchItem.getIsTrack())) {
						EventBus.getDefault().post( new EventAlbumRequest( searchItem.getArtistId(), searchItem.getAlbumId()));
					}
				}
			} );

			if( mListViewState != null ) {
				mSearchListView.onRestoreInstanceState( mListViewState );
			}
		}

		return( myView );
	}

	@Override
	public void onResume() {
		super.onResume();

		mEventBus.register( this );
	}

	@Override
	public void onPause() {
		super.onPause();

		clearSubscription();
		mEventBus.unregister( this );
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		ButterKnife.reset( this );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		if( mResultList.size() > 0 ) {
			outState.putParcelableArrayList( SEARCH_LIST, mResultList );

			if( mSearchListView != null ) {
				mListViewState = mSearchListView.onSaveInstanceState();
			}
			if( mListViewState != null ) {
				outState.putParcelable( LIST_STATE, mListViewState );
			}
		}
	}

	private void clearSubscription() {
		if( mSearchSubscription != null ) {
			mSearchSubscription.unsubscribe();
			mSearchSubscription = null;
		}
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventSearchRequest args ) {
		mNoResultsView.setVisibility( View.INVISIBLE );
		mResultList.clear();
		mSearchListAdapter.notifyDataSetChanged();

		clearSubscription();

		if(!TextUtils.isEmpty( args.getSearchTerm())) {
			mSearchSubscription = AndroidObservable.fromFragment( this, mSearchClient.Search( args.getSearchTerm()))
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

		if( resultList.size() > 0 ) {
			mResultList.addAll( resultList );

			Collections.sort( mResultList, new Comparator<SearchResultItem>() {
				public int compare( SearchResultItem result1, SearchResultItem result2 ) {
					return( result1.getItemTitle().compareToIgnoreCase( result2.getItemTitle()));
				}
			} );
		}
		else {
			mNoResultsView.setVisibility( View.VISIBLE );
		}

		mSearchListAdapter.notifyDataSetChanged();
		clearSubscription();
	}

	protected class SearchResultAdapter extends ArrayAdapter<SearchResultItem> {
		private Context                     mContext;
		private LayoutInflater              mLayoutInflater;
		private ArrayList<SearchResultItem> mResultList;

		protected class ViewHolder {
			public ViewHolder( View view ) {
				ButterKnife.inject( this, view );
			}

			@InjectView( R.id.sli_title )                   TextView     TitleView;
			@InjectView( R.id.sli_subtitle )                TextView     SubTitleView;
			@InjectView( R.id.play_button )                 Button       PlayButton;
			@InjectView( R.id.sli_type_indicator_artist )   View         ArtistIndicatorView;
			@InjectView( R.id.sli_type_indicator_album )    View         AlbumIndicatorView;
			@InjectView( R.id.sli_type_indicator_track )    View         TrackIndicatorView;

			@SuppressWarnings( "unused" )
			@OnClick( R.id.play_button )
			public void onClick( View view ) {
				SearchResultItem    searchItem = (SearchResultItem)view.getTag();

				if( searchItem != null ) {
					EventBus.getDefault().post( new EventPlaySearchItem( searchItem ));
				}
			}
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

				if( retValue != null ) {
					views = new ViewHolder( retValue );

					retValue.setTag( views );
				}
			}
			else {
				views = (ViewHolder)retValue.getTag();
			}

			if(( views != null ) &&
			   ( position < mResultList.size())) {
				SearchResultItem    result = mResultList.get( position );

				views.TitleView.setText( result.getItemTitle() );
				views.SubTitleView.setText( result.getItemSubTitle());
				views.PlayButton.setVisibility( result.getCanPlay() ? View.VISIBLE : View.INVISIBLE );
				views.PlayButton.setTag( result );

				views.ArtistIndicatorView.setVisibility( View.GONE );
				views.AlbumIndicatorView.setVisibility( View.GONE );
				views.TrackIndicatorView.setVisibility( View.GONE );

				if( result.getIsArtist()) {
					views.ArtistIndicatorView.setVisibility( View.VISIBLE );
				}

				if( result.getIsAlbum()) {
					views.AlbumIndicatorView.setVisibility( View.VISIBLE );
				}

				if( result.getIsTrack()) {
					views.TrackIndicatorView.setVisibility( View.VISIBLE );
				}
			}

			return( retValue );
		}
	}
}
