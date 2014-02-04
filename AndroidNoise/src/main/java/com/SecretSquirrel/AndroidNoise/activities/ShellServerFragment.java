package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;

public class ShellServerFragment extends BaseShellFragment {
	private static final String SELECT_LAST_SERVER = "serverFragmentSelectLastServer";

	public static ShellServerFragment newInstance( int fragmentId, boolean selectLastServer ) {
		ShellServerFragment     fragment = new ShellServerFragment();
		Bundle                  args = new Bundle();

		args.putInt( SHELL_FRAGMENT_KEY, fragmentId );
		args.putBoolean( SELECT_LAST_SERVER, selectLastServer );
		fragment.setArguments( args );

		return( fragment );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		boolean selectLastServer = false;
		Bundle  args = getArguments();

		if( args != null ) {
			selectLastServer = args.getBoolean( SELECT_LAST_SERVER );
		}

		if( getChildFragmentManager().findFragmentById( R.id.ServerShellFrame ) == null ) {
			getChildFragmentManager()
					.beginTransaction()
					.replace( R.id.ServerShellFrame, ServerListFragment.newInstance( selectLastServer ))
					.commit();
		}

		return( inflater.inflate( R.layout.fragment_server_shell, container, false ));
	}
}
