package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by BSwanson on 12/23/13.

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.events.EventLibraryManagementRequest;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;
import com.SecretSquirrel.AndroidNoise.views.RevealingListView.DefaultRevealingListViewListener;
import com.SecretSquirrel.AndroidNoise.views.RevealingListView.RevealingListView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class ServerListFragment extends Fragment {
	private static final String             SELECT_LAST_SERVER = "serverListSelectLast";

	private ArrayList<ServerInformation>    mServerList;
	private ServerAdapter                   mServerListAdapter;
	private Subscription                    mLocatorSubscription;
	private String                          mLastServer;

	@Inject EventBus                        mEventBus;
	@Inject IApplicationState               mApplicationState;

	public static ServerListFragment newInstance( boolean selectLastServer ) {
		ServerListFragment  fragment = new ServerListFragment();
		Bundle              args = new Bundle();

		args.putBoolean( SELECT_LAST_SERVER, selectLastServer );
		fragment.setArguments( args );

		return( fragment );
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		setHasOptionsMenu( true );

		mServerList = new ArrayList<ServerInformation>();
		mServerListAdapter = new ServerAdapter( getActivity(), mServerList );

		Bundle  args = getArguments();

		if( args != null ) {
			boolean selectLastServer = args.getBoolean( SELECT_LAST_SERVER );

			if( selectLastServer ) {
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( getActivity());

				mLastServer = settings.getString( getString( R.string.setting_last_server_used ), "" );
			}
		}

		startServerSubscription();
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_server_list, container, false );

		if( myView != null ) {
			RevealingListView serverListView = (RevealingListView) myView.findViewById( R.id.sl_server_list_view );

			serverListView.setEmptyView( myView.findViewById( R.id.sl_empty_view ));
			serverListView.setAdapter( mServerListAdapter );
			serverListView.setRevealingListViewListener( new DefaultRevealingListViewListener() {
				@Override
				public void onItemClicked( int position ) {
					ServerInformation serverInformation = mServerList.get( position );

					if( serverInformation != null ) {
						selectServer( serverInformation );
					}
				}
			});

			try {
				PackageManager  manager = getActivity().getPackageManager();

				if( manager != null ) {
					TextView    versionView = (TextView)myView.findViewById( R.id.software_version );
					String      softwareVersionFormat = getString( R.string.software_version_format );
					String      versionName = manager.getPackageInfo(
												getActivity().getPackageName(), 0 ).versionName;

					versionView.setText( String.format( softwareVersionFormat, versionName ) );
				}
			}
			catch( Exception ex ) {
				Timber.e( ex, "Software version could not be determined." );
			}
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

	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		inflater.inflate( R.menu.server_list, menu );

		super.onCreateOptionsMenu( menu, inflater );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		boolean retValue = true;

		switch( item.getItemId()) {
			case R.id.action_refresh_server_list:
				resetServerSubscription();
				break;

			default:
				retValue = super.onOptionsItemSelected( item );
				break;
		}

		return( retValue );
	}

	private void resetServerSubscription() {
		if( mLocatorSubscription != null ) {
			mLocatorSubscription.unsubscribe();

			mLocatorSubscription = null;
		}

		mServerList.clear();
		mServerListAdapter.notifyDataSetChanged();

		Handler handler = new Handler();
		handler.postDelayed( new Runnable() {
			public void run() {
				startServerSubscription();
			}
		}, 2000 );
	}

	private void startServerSubscription() {
		mLocatorSubscription = mApplicationState.locateServers()
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
								Timber.e( throwable, "LocateServers returned error" );
							}
						});
	}

	private void onServerInformation( ServerInformation serverInformation ) {
		switch( serverInformation.getServiceState()) {
			case ServiceResolved:
				boolean     exists = false;

				for( ServerInformation si : mServerList ) {
					if( si.getServerAddress().equals( serverInformation.getServerAddress())) {
						exists = true;

						break;
					}
				}

				if(!exists ) {
					mServerList.add( serverInformation );

					if( serverInformation.getServerName().equals( mLastServer )) {
						selectServer( serverInformation );
					}

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

	private void selectServer( ServerInformation server ) {
		if( server != null ) {
			mApplicationState.setCurrentServer( server );

			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( getActivity());
			SharedPreferences.Editor editor = settings.edit();

			editor.putString( getString( R.string.setting_last_server_used ), server.getServerName());
			editor.commit();

			mEventBus.post( new EventServerSelected( server ));
		}
	}

	protected class ServerAdapter extends ArrayAdapter<ServerInformation> {
		private Context                         mContext;
		private LayoutInflater                  mLayoutInflater;
		private ArrayList<ServerInformation>    mServerList;
		private String                          mTitleFormat;
		private String                          mSubtitleFormat;

		protected class ViewHolder {
			public ViewHolder( View view ) {
				ButterKnife.inject( this, view );
			}

			@InjectView( R.id.sli_name )    TextView    NameTextView;
			@InjectView( R.id.sli_address ) TextView    AddressTextView;
			@InjectView( R.id.sli_manage )  View        ManageView;

			@SuppressWarnings( "unused" )
			@OnClick( R.id.sli_manage )
			public void onClick( View view ) {
				ServerInformation   serverInformation = (ServerInformation)view.getTag();

				if( serverInformation != null ) {
					mEventBus.post( new EventLibraryManagementRequest( serverInformation ));
				}
			}
		}

		public ServerAdapter( Context context, ArrayList<ServerInformation> serverList ) {
			super( context, R.layout.server_list_item, serverList );
			mContext = context;
			mServerList = serverList;

			mTitleFormat = getString( R.string.sli_title_format );
			mSubtitleFormat = getString( R.string.sli_subtitle_format );

			mLayoutInflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View        retValue = convertView;
			ViewHolder  views;

			if( convertView == null ) {
				retValue = mLayoutInflater.inflate( R.layout.server_list_item, parent, false );

				views = new ViewHolder( retValue );

				if( retValue != null ) {
					retValue.setTag( views );
				}
			}
			else {
				views = (ViewHolder)retValue.getTag();
			}

			if(( views != null ) &&
			   ( position < mServerList.size())) {
				ServerInformation   serverInfo = mServerList.get( position );

				views.NameTextView.setText( String.format( mTitleFormat, serverInfo.getServerName(), serverInfo.getLibraryName()));
				views.AddressTextView.setText( String.format( mSubtitleFormat, serverInfo.getServerName(), serverInfo.getServerAddress()));

				views.ManageView.setTag( serverInfo );
			}

			return( retValue );
		}
	}
}
