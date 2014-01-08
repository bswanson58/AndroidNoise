package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/30/13.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;

public class ShellSearchFragment extends BaseShellFragment {
	public static ShellSearchFragment newInstance( int fragmentId ) {
		ShellSearchFragment     fragment = new ShellSearchFragment();
		Bundle                  args = new Bundle();

		args.putInt( SHELL_FRAGMENT_KEY, fragmentId );
		fragment.setArguments( args );

		return( fragment );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		getChildFragmentManager()
				.beginTransaction()
				.replace( R.id.search_list_frame, SearchListFragment.newInstance())
				.replace( R.id.search_query_frame, SearchQueryFragment.newInstance())
				.commit();

		return( inflater.inflate( R.layout.fragment_search_shell, container, false ));
	}
}
