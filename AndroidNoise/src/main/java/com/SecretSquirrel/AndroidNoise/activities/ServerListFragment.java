package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.support.Constants;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import rx.Subscription;
import rx.android.concurrency.AndroidSchedulers;
import rx.util.functions.Action1;

public class ServerListFragment extends Fragment {
	private static final String             TAG = ServerListFragment.class.getName();

	private ArrayList<ServerInformation>    mServerList;
	private ServerAdapter                   mServerListAdapter;
	private Subscription                    mLocatorSubscription;

	public static ServerListFragment newInstance() {
		return( new ServerListFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mServerList = new ArrayList<ServerInformation>();
		mServerListAdapter = new ServerAdapter( getActivity(), mServerList );

		mLocatorSubscription = getApplicationState().locateServers()
			.observeOn( AndroidSchedulers.mainThread() )
			.subscribe( new Action1<ServerInformation>() {
							@Override
							public void call( ServerInformation s ) {
								onServerInformation( s );
							}
						},
						new Action1<Throwable>() {
							@Override
							public void call( Throwable throwable ) {
								if( Constants.LOG_ERROR ) {
									Log.e( TAG, "LocateServers returned error", throwable );
								}
							}
						});
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_server_list, container, false );

		if( myView != null ) {
			ListView    serverListView = (ListView) myView.findViewById( R.id.sl_server_list_view );

			serverListView.setEmptyView( myView.findViewById( R.id.sl_empty_view ));
			serverListView.setAdapter( mServerListAdapter );
			serverListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick( AdapterView<?> adapterView, View view, int i, long l ) {
					ServerInformation serverInformation = mServerList.get( i );

					if( serverInformation != null ) {
						selectServer( serverInformation );
					}
				}
			} );
		}

		return( myView );
	}

	@Override
	public void onPause() {
		if( mLocatorSubscription != null ) {
			mLocatorSubscription.unsubscribe();
			mLocatorSubscription = null;
		}

		super.onPause();
	}

	private void onServerInformation( ServerInformation serverInformation ) {
		switch( serverInformation.getServiceState()) {
			case ServiceResolved:
				boolean     exists = false;

				for( ServerInformation si : mServerList ) {
					if( si.getServerAddress().equals( serverInformation.getServerAddress() )) {
						exists = true;

						break;
					}
				}

				if(!exists ) {
					mServerList.add( serverInformation );
					mServerListAdapter.notifyDataSetChanged();
				}
				break;

			case ServiceDeleted:
				for( ServerInformation si : mServerList ) {
					if( si.getServerAddress().equals( serverInformation.getServerAddress())) {
						mServerList.remove( si );

						mServerListAdapter.notifyDataSetChanged();
						break;
					}
				}
				break;
		}
	}

	private IApplicationState getApplicationState() {
		NoiseRemoteApplication application = (NoiseRemoteApplication)getActivity().getApplication();

		return( application.getApplicationState());
	}

	private void selectServer( ServerInformation server ) {
		if( server != null ) {
			getApplicationState().SelectServer( server );

			EventBus.getDefault().post( new EventServerSelected( server ) );
		}
	}

	private class ServerAdapter extends ArrayAdapter<ServerInformation> {
		private Context                         mContext;
		private LayoutInflater                  mLayoutInflater;
		private ArrayList<ServerInformation>    mServerList;

		private class ViewHolder {
			public TextView     NameTextView;
			public TextView     AddressTextView;
		}

		public ServerAdapter( Context context, ArrayList<ServerInformation> serverList ) {
			super( context, R.layout.server_list_item, serverList );
			mContext = context;
			mServerList = serverList;

			mLayoutInflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View        retValue = convertView;
			ViewHolder  views;

			if( convertView == null ) {
				retValue = mLayoutInflater.inflate( R.layout.server_list_item, parent, false );

				views = new ViewHolder();

				if( retValue != null ) {
					views.NameTextView = (TextView) retValue.findViewById( R.id.sli_name );
					views.AddressTextView = (TextView)retValue.findViewById( R.id.sli_address );

					retValue.setTag( views );
				}
			}
			else {
				views = (ViewHolder)retValue.getTag();
			}

			if(( views != null ) &&
			   ( position < mServerList.size())) {
				ServerInformation   serverInfo = mServerList.get( position );

				views.NameTextView.setText( serverInfo.getHostName());
				views.AddressTextView.setText( String.format( "(%s)", serverInfo.getServerAddress()));
			}

			return( retValue );
		}
	}
}
