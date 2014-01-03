package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.dto.ServerVersion;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceLocatorObservable;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;
import com.SecretSquirrel.AndroidNoise.services.rto.RoServerVersion;
import com.SecretSquirrel.AndroidNoise.support.NetworkUtility;
import com.SecretSquirrel.AndroidNoise.support.ThreadExecutor;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscription;
import rx.android.concurrency.AndroidSchedulers;
import rx.concurrency.Schedulers;
import rx.util.functions.Action1;

public class ServerListFragment extends Fragment
								implements ServiceResultReceiver.Receiver { //}, javax.jmdns.ServiceListener {
	private static final String             TAG = ServerListFragment.class.getName();

	private ArrayList<ServerInformation>    mServerList;
	private ListView                        mServerListView;
	private ServerAdapter                   mServerListAdapter;
	//private ServiceInfoAdapter              mServerListAdapter;
	private ServiceResultReceiver           mServiceResultReceiver;
	private ServiceLocatorObservable        mServiceLocator;
	private Observable<String>              mLocatorObservable;

	public final static String              NOISE_TYPE = "_Noise._Tcp.local.";
	public final static String              HOSTNAME = "NoiseRemote";

	private static JmDNS                        mZeroConfig = null;
	private static WifiManager.MulticastLock    mLock = null;

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mServerList = new ArrayList<ServerInformation>();
		mServerListAdapter = new ServerAdapter( getActivity(), mServerList );
		//mServerListAdapter = new ServiceInfoAdapter( getActivity());
		mServiceResultReceiver = new ServiceResultReceiver( new Handler());
		mServiceResultReceiver.setReceiver( this );

		mServiceLocator = new ServiceLocatorObservable( NOISE_TYPE, HOSTNAME );
		mLocatorObservable = mServiceLocator.start( getActivity());
		mLocatorObservable.observeOn( AndroidSchedulers.mainThread()).subscribe( new Action1<String>() {
			@Override
			public void call( String s ) {
				mServerList.add( new ServerInformation( s, new ServerVersion( new RoServerVersion())));
				mServerListAdapter.notifyDataSetChanged();
			}
		} );
//		getApplicationState().LocateServers( mServiceResultReceiver );
/*		ThreadExecutor.runTask( new Runnable() {

			public void run() {
				try {
					ServerListFragment.this.startProbe();
				}
				catch( Exception e ) {
					Log.d( TAG, String.format( "onCreate Error: %s", e.getMessage()));
				}
			}
		} );*/
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_server_list, container, false );

		mServerListView = (ListView) myView.findViewById( R.id.ServerListView );
		mServerListView.setAdapter( mServerListAdapter );

		mServerListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick( AdapterView<?> adapterView, View view, int i, long l ) {
				ServerInformation serverInformation = mServerList.get( i );

				if( serverInformation != null ) {
					selectServer( serverInformation );
				}
			}
		} );

		return( myView );
	}
/*
	protected void startProbe() throws Exception {
		getActivity().runOnUiThread( new Runnable() {

			public void run() {
				mServerListAdapter.clear();
				mServerListAdapter.notifyDataSetChanged();
			}

		} );

		WifiManager wifi = (WifiManager) getActivity().getSystemService( Context.WIFI_SERVICE );
		InetAddress address = NetworkUtility.getWirelessIpAddress( wifi );

		Log.d( TAG, String.format( "Local address is: %s", address.toString() ));

		// start multicast lock
		mLock = wifi.createMulticastLock( String.format( "%s lock", HOSTNAME ));
		mLock.setReferenceCounted( true );
		mLock.acquire();

		mZeroConfig = JmDNS.create( address, HOSTNAME );
		//ServiceInfo[]   services = mZeroConfig.list( NOISE_TYPE );
		mZeroConfig.addServiceListener( NOISE_TYPE, this );
	}

	protected void stopProbe() {
		mZeroConfig.removeServiceListener( NOISE_TYPE, this );

		ThreadExecutor.runTask(new Runnable() {

			public void run() {
				try {
					mZeroConfig.close();
					mZeroConfig = null;
				} catch (IOException e) {
					Log.d( TAG, String.format( "ZeroConf Error: %s", e.getMessage() ) );
				}
			}
		});

		if( mLock != null ) {
			mLock.release();
			mLock = null;
		}
	}

	private final static int DELAY = 500;

	public void serviceAdded( ServiceEvent event ) {
		// someone is yelling about their touch-able service
		// go figure out what their ip address is
		Log.w( TAG, String.format( "serviceAdded(event=\n%s\n)", event.toString()));
		//final String name = event.getName();

		// trigger delayed gui event
		// needs to be delayed because jmdns hasn't parsed txt info yet
		//resultsUpdated.sendMessageDelayed( Message.obtain( resultsUpdated, -1, name ), DELAY );
	}

	public void serviceRemoved( ServiceEvent event ) {
		Log.w( TAG, String.format( "serviceRemoved(event=\n%s\n)", event.toString()));
	}

	public void serviceResolved( ServiceEvent event ) {
		Log.w( TAG, String.format( "serviceResolved(event=\n%s\n)", event.toString()));

		if( mServerListAdapter.notifyFound( event.getName())) {
			getActivity().runOnUiThread( new Runnable() {
				@Override
				public void run() {
					mServerListAdapter.notifyDataSetChanged();
				}
			} );
		}
	}

	public Handler resultsUpdated = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if( msg.obj != null ) {
				boolean result = mServerListAdapter.notifyFound((String) msg.obj );

				// only update UI if a new one was added
				if( result ) {
					mServerListAdapter.notifyDataSetChanged();
				}
			}
		}
	};
*/
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

	public static JmDNS getZeroConf() {
		return mZeroConfig;
	}

	private void selectServer( ServerInformation server ) {
		if( server != null ) {
			getApplicationState().SelectServer( server );

			EventBus.getDefault().post( new EventServerSelected( server ) );
		}
	}

	public class ServiceInfoAdapter extends BaseAdapter {
		protected Context context;
		protected LayoutInflater inflater;
		public View footerView;
		protected final LinkedList<ServiceInfo> known = new LinkedList<ServiceInfo>();

		public ServiceInfoAdapter(Context context) {
			this.context = context;
			this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.footerView = inflater.inflate(R.layout.server_list_item, null, false);
		}

		public boolean notifyFound( String serviceName ) {
			boolean result = false;
			try {
				Log.d( TAG, String.format( "DNS Name: %s", serviceName ));
				ServiceInfo serviceInfo = getZeroConf().getServiceInfo( NOISE_TYPE, serviceName );

				if( serviceInfo != null) {
					String libraryName = serviceInfo.getName();

					// check if we already have this DatabaseId
					for( ServiceInfo service : known ) {
						if( libraryName.equalsIgnoreCase( service.getName())) {
							Log.w( TAG, "Already have DatabaseId loaded = " + libraryName );
							return result;
						}
					}

					if(!known.contains( serviceInfo )) {
						known.add( serviceInfo );
						result = true;
					}
				}
			} catch( Exception e ) {
				Log.d( TAG, String.format( "Problem getting ZeroConf information %s", e.getMessage()));
			}

			return result;
		}

		public void clear() {
			known.clear();
		}

		public Object getItem(int position) {
			return known.get(position);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		public int getCount() {
			return known.size();
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null)
				convertView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);

			try {
				// fetch the dns txt record to get library info
				final ServiceInfo   serviceInfo = (ServiceInfo) this.getItem(position);
				final String        title = serviceInfo.getName();
				final String        address = serviceInfo.getHostAddresses()[0]; // grab first one

				Log.d( TAG, String.format("ZeroConf Server: %s", serviceInfo.getServer()));
				Log.d( TAG, String.format("ZeroConf Port: %s", serviceInfo.getPort()));
				Log.d( TAG, String.format("ZeroConf Title: %s", title));
				Log.d( TAG, String.format("ZeroConf IP Address: %s", address));

				((TextView) convertView.findViewById(android.R.id.text1)).setText( title );
				((TextView) convertView.findViewById(android.R.id.text2)).setText( address );

			} catch (Exception e) {
				Log.d(TAG, String.format("Problem getting ZeroConf information %s", e.getMessage()));
				((TextView) convertView.findViewById(android.R.id.text1)).setText("Unknown");
				((TextView) convertView.findViewById(android.R.id.text2)).setText("Unknown");
			}

			return convertView;
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
				views.NameTextView = (TextView) retValue.findViewById( R.id.server_list_item_name );

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
