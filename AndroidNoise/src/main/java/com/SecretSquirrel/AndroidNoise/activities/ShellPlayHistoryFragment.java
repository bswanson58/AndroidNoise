package com.SecretSquirrel.AndroidNoise.activities;

// Created by BSwanson on 4/7/14.

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;

public class ShellPlayHistoryFragment extends BaseShellFragment {
	public static ShellPlayHistoryFragment newInstance( int fragmentId ) {
		ShellPlayHistoryFragment  fragment = new ShellPlayHistoryFragment();
		Bundle args = new Bundle();

		args.putInt( SHELL_FRAGMENT_KEY, fragmentId );
		fragment.setArguments( args );

		return( fragment );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		if( getChildFragmentManager().findFragmentById( R.id.history_shell_frame ) == null ) {
			getChildFragmentManager()
					.beginTransaction()
					.replace( R.id.history_shell_frame, PlayHistoryListFragment.newInstance())
					.commit();
		}

		return( inflater.inflate( R.layout.fragment_play_history_shell, container, false ));
	}
}
