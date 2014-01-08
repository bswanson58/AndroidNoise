package com.SecretSquirrel.AndroidNoise.activities;

// Created by BSwanson on 12/28/13.

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Favorite;
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
	private ServiceResultReceiver   mServiceResultReceiver;
	private ArrayList<Favorite>     mFavoritesList;
	private FavoritesAdapter        mFavoritesListAdapter;

	public static FavoritesListFragment newInstance() {
		return( new FavoritesListFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mFavoritesList = new ArrayList<Favorite>();
		mFavoritesListAdapter = new FavoritesAdapter( getActivity(), mFavoritesList );

		mServiceResultReceiver = new ServiceResultReceiver( new Handler());
		mServiceResultReceiver.setReceiver( this );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_favorites_list, container, false );

		if( myView != null ) {
			ListView    favoritesListView = (ListView) myView.findViewById( R.id.FavoritesListView );

			favoritesListView.setAdapter( mFavoritesListAdapter );
		}

		if( getApplicationState().getIsConnected()) {
			getApplicationState().getDataClient().GetFavoritesList( mServiceResultReceiver );
		}

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
			ArrayList<Favorite>   favoritesList = resultData.getParcelableArrayList( NoiseRemoteApi.FavoritesList );

			setFavoritesList( favoritesList );
		}
	}

	public void setFavoritesList( ArrayList<Favorite> favoritesList ) {
		mFavoritesList.clear();
		mFavoritesList.addAll( favoritesList );

		Collections.sort( mFavoritesList, new Comparator<Favorite>() {
			public int compare( Favorite favorite1, Favorite favorite2 ) {
				return( favorite1.getArtist().compareToIgnoreCase( favorite2.getArtist()));
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
			public TextView     NameTextView;
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
					views.NameTextView = (TextView)retValue.findViewById( R.id.favorite_list_item_name );

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
				views.NameTextView.setText( favorite.getArtist() + "/" + favorite.getAlbum() + "/" + favorite.getTrack());
			}

			return( retValue );
		}
	}
}
