package com.SecretSquirrel.AndroidNoise.activities;

// Created by BSwanson on 12/28/13.

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Favorite;
import com.SecretSquirrel.AndroidNoise.events.EventAlbumRequest;
import com.SecretSquirrel.AndroidNoise.events.EventArtistRequest;
import com.SecretSquirrel.AndroidNoise.events.EventPlayFavorite;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.greenrobot.event.EventBus;

public class FavoritesListFragment extends Fragment
								   implements ServiceResultReceiver.Receiver {
	private final String            LIST_STATE = "favoritesListState";
	private final String            FAVORITES_LIST = "favoritesList";

	private ServiceResultReceiver   mServiceResultReceiver;
	private ArrayList<Favorite>     mFavoritesList;
	private ListView                mFavoritesListView;
	private FavoritesAdapter        mFavoritesListAdapter;

	public static FavoritesListFragment newInstance() {
		return( new FavoritesListFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		if( savedInstanceState != null ) {
			mFavoritesList = savedInstanceState.getParcelableArrayList( FAVORITES_LIST );
		}
		if( mFavoritesList == null ) {
			mFavoritesList = new ArrayList<Favorite>();
		}

		mFavoritesListAdapter = new FavoritesAdapter( getActivity(), mFavoritesList );

		mServiceResultReceiver = new ServiceResultReceiver( new Handler());
		mServiceResultReceiver.setReceiver( this );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_favorites_list, container, false );

		if( myView != null ) {
			mFavoritesListView = (ListView) myView.findViewById( R.id.FavoritesListView );

			mFavoritesListView.setAdapter( mFavoritesListAdapter );
			mFavoritesListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick( AdapterView<?> adapterView, View view, int i, long l ) {
					Favorite    favorite = mFavoritesList.get( i );

					if( favorite.getIsArtist()) {
						EventBus.getDefault().post( new EventArtistRequest( favorite.getArtistId()));
					}
					else if(( favorite.getIsAlbum()) ||
							( favorite.getIsTrack())) {
						EventBus.getDefault().post( new EventAlbumRequest( favorite.getArtistId(), favorite.getAlbumId()));
					}
				}
			} );

			if( savedInstanceState != null ) {
				Parcelable  listState = savedInstanceState.getParcelable( LIST_STATE );

				if( listState != null ) {
					mFavoritesListView.onRestoreInstanceState( listState );
				}
			}
		}

		if(( mFavoritesList.size() == 0 ) &&
		   ( getApplicationState().getIsConnected())) {
			getApplicationState().getDataClient().GetFavoritesList( mServiceResultReceiver );
		}

		return( myView );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putParcelableArrayList( FAVORITES_LIST, mFavoritesList );
		outState.putParcelable( LIST_STATE, mFavoritesListView.onSaveInstanceState());
	}

	@Override
	public void onPause() {
		super.onPause();

		mServiceResultReceiver.clearReceiver();
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			ArrayList<Favorite>   favoritesList = resultData.getParcelableArrayList( NoiseRemoteApi.FavoritesList );

			setFavoritesList( favoritesList );
		}
	}

	public void setFavoritesList( ArrayList<Favorite> favoritesList ) {
		mFavoritesList.clear();
		mFavoritesList.addAll( favoritesList );

		Collections.sort( mFavoritesList, new Comparator<Favorite>() {
			public int compare( Favorite favorite1, Favorite favorite2 ) {
				return( favorite1.getSortingName().compareToIgnoreCase( favorite2.getSortingName()));
			}
		} );

		mFavoritesListAdapter.notifyDataSetChanged();
	}

	private IApplicationState getApplicationState() {
		NoiseRemoteApplication application = (NoiseRemoteApplication)getActivity().getApplication();

		return( application.getApplicationState());
	}

	private class FavoritesAdapter extends ArrayAdapter<Favorite> {
		private Context             mContext;
		private LayoutInflater      mLayoutInflater;
		private ArrayList<Favorite> mFavoritesList;

		private class ViewHolder {
			public Button       PlayButton;
			public TextView     TitleTextView;
			public TextView     SubtitleTextView;
			public View         ArtistIndicatorView;
			public View         AlbumIndicatorView;
			public View         TrackIndicatorView;
		}

		public FavoritesAdapter( Context context, ArrayList<Favorite> favoritesList ) {
			super( context, R.layout.artist_list_item, favoritesList );
			mContext = context;
			mFavoritesList = favoritesList;

			mLayoutInflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View        retValue = convertView;
			ViewHolder  views = null;

			if( convertView == null ) {
				retValue = mLayoutInflater.inflate( R.layout.favorite_list_item, parent, false );

				if( retValue != null ) {
					views = new ViewHolder();
					views.TitleTextView = (TextView)retValue.findViewById( R.id.fli_name );
					views.SubtitleTextView = (TextView)retValue.findViewById( R.id.fli_album_name );
					views.ArtistIndicatorView = retValue.findViewById( R.id.fli_type_indicator_artist );
					views.AlbumIndicatorView = retValue.findViewById( R.id.fli_type_indicator_album );
					views.TrackIndicatorView = retValue.findViewById( R.id.fli_type_indicator_track );

					views.PlayButton = (Button) retValue.findViewById( R.id.play_button );
					views.PlayButton.setOnClickListener( new View.OnClickListener() {
						@Override
						public void onClick( View view ) {
							Favorite    favorite = (Favorite)view.getTag();

							if( favorite != null ) {
								EventBus.getDefault().post( new EventPlayFavorite( favorite ));
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
			   ( position < mFavoritesList.size())) {
				Favorite    favorite = mFavoritesList.get( position );

				views.PlayButton.setTag( favorite );
				views.TitleTextView.setText( favorite.getItemTitle());
				views.SubtitleTextView.setText( favorite.getItemSubTitle());

				views.ArtistIndicatorView.setVisibility( View.GONE );
				views.AlbumIndicatorView.setVisibility( View.GONE );
				views.TrackIndicatorView.setVisibility( View.GONE );

				if( favorite.getIsArtist()) {
					views.ArtistIndicatorView.setVisibility( View.VISIBLE );
				}

				if( favorite.getIsAlbum()) {
					views.AlbumIndicatorView.setVisibility( View.VISIBLE );
				}

				if( favorite.getIsTrack()) {
					views.TrackIndicatorView.setVisibility( View.VISIBLE );
				}
			}

			return( retValue );
		}
	}
}
