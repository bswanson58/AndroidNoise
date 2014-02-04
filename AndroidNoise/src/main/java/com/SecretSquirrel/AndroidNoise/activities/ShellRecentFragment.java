package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 2/4/14.

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;

public class ShellRecentFragment extends BaseShellFragment {
	public static ShellRecentFragment newInstance( int fragmentId ) {
		ShellRecentFragment fragment = new ShellRecentFragment();
		Bundle              args = new Bundle();

		args.putInt( SHELL_FRAGMENT_KEY, fragmentId );
		fragment.setArguments( args );

		return( fragment );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		if( getChildFragmentManager().findFragmentById( R.id.frame_recently_viewed ) == null ) {
			getChildFragmentManager()
					.beginTransaction()
					.replace( R.id.frame_recently_viewed, RecentlyViewedListFragment.newInstance())
					.replace( R.id.frame_recently_played, RecentlyPlayedListFragment.newInstance())
					.commit();
		}

		return( inflater.inflate( R.layout.fragment_recent_shell, container, false ));
	}
}
