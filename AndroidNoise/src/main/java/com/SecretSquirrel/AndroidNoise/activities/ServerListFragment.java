package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

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
import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class ServerListFragment extends Fragment
								implements ServiceResultReceiver.Receiver {
	private ArrayList<ServerInformation>    mServerList;
	private ListView                        mServerListView;
	private ServerAdapter                   mServerListAdapter;
	private ServiceResultReceiver           mServiceResultReceiver;

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mServerList = new ArrayList<ServerInformation>();
		mServerListAdapter = new ServerAdapter( getActivity(), mServerList );
		mServiceResultReceiver = new ServiceResultReceiver( new Handler());
		mServiceResultReceiver.setReceiver( this );

		getApplicationState().LocateServers( mServiceResultReceiver );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_server_list, container, false );

		mServerListView = (ListView) myView.findViewById( R.id.ServerListView );
		mServerListView.setAdapter( mServerListAdapter );

		mServerListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick( AdapterView<?> adapterView, View view, int i, long l ) {
				ServerInformation  serverInformation = mServerList.get( i );

				if( serverInformation != null ) {
					selectServer( serverInformation );
				}
			}
		} );

		return( myView );
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			ArrayList<ServerInformation>    serverList = resultData.getParcelableArrayList( NoiseRemoteApi.LocateServicesList );

			/*
			if(( serverList != null ) &&
			   ( serverList.size() == 1 )) {
				selectServer( serverList.get( 0 ));
			}
			else {
				mServerList.addAll( serverList );
				mServerListAdapter.notifyDataSetChanged();
			}
			*/
			mServerList.addAll( serverList );
			mServerListAdapter.notifyDataSetChanged();
		}
	}

	private IApplicationState getApplicationState() {
		NoiseRemoteApplication application = (NoiseRemoteApplication)getActivity().getApplication();

		return( application.getApplicationState());
	}

	private void selectServer( ServerInformation server ) {
		if( server != null ) {
			getApplicationState().SelectServer( server );

			EventBus.getDefault().post( new EventServerSelected( server ));
		}
	}

	private class ServerAdapter extends ArrayAdapter<ServerInformation> {
		private Context                         mContext;
		private LayoutInflater                  mLayoutInflater;
		private ArrayList<ServerInformation>    mServerList;

		private class ViewHolder {
			public TextView NameTextView;
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
			ViewHolder  views = null;

			if( convertView == null ) {
				retValue = mLayoutInflater.inflate( R.layout.server_list_item, parent, false );

				views = new ViewHolder();
				views.NameTextView = (TextView)retValue.findViewById( R.id.server_list_item_name );

				retValue.setTag( views );
			}
			else {
				views = (ViewHolder)retValue.getTag();
			}

			if(( views != null ) &&
			   ( position < mServerList.size())) {
				ServerInformation   serverInfo = mServerList.get( position );

				views.NameTextView.setText( serverInfo.getServerAddress());
			}

			return( retValue );
		}
	}
}
