package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.events.EventLibraryManagementRequest;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class ShellServerFragment extends BaseShellFragment {
	private static final String SELECT_LAST_SERVER = "serverFragmentSelectLastServer";

	private Fragment    mOverlay;

	@Inject	EventBus    mEventBus;

	public static ShellServerFragment newInstance( int fragmentId, boolean selectLastServer ) {
		ShellServerFragment     fragment = new ShellServerFragment();
		Bundle                  args = new Bundle();

		args.putInt( SHELL_FRAGMENT_KEY, fragmentId );
		args.putBoolean( SELECT_LAST_SERVER, selectLastServer );
		fragment.setArguments( args );

		return( fragment );
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		boolean selectLastServer = false;
		Bundle  args = getArguments();

		if( args != null ) {
			selectLastServer = args.getBoolean( SELECT_LAST_SERVER );
		}

		if( getChildFragmentManager().findFragmentById( R.id.ss_server_list ) == null ) {
			getChildFragmentManager()
					.beginTransaction()
					.replace( R.id.ss_server_list, ServerListFragment.newInstance( selectLastServer ))
					.commit();
		}

		mOverlay = getChildFragmentManager().findFragmentById( R.id.ss_popup );

		return( inflater.inflate( R.layout.fragment_server_shell, container, false ));
	}

	@Override
	public void onResume() {
		super.onResume();

		mEventBus.register( this );
	}

	@Override
	public void onPause() {
		super.onPause();

		mEventBus.unregister( this );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventLibraryManagementRequest args ) {
		if( args.getCloseRequest()) {
			if( mOverlay != null ) {
				getChildFragmentManager()
						.beginTransaction()
						.setCustomAnimations( R.anim.fragment_slide_in, R.anim.fragment_slide_out )
						.remove( mOverlay )
						.commit();

				mOverlay = null;
			}
		}
		else {
			mOverlay = LibraryConfiguration.newInstance( args.getServerInformation());

			getChildFragmentManager()
					.beginTransaction()
					.setCustomAnimations( R.anim.fragment_slide_in, R.anim.fragment_slide_out )
					.replace( R.id.ss_popup, mOverlay )
					.commit();
		}
	}
}
