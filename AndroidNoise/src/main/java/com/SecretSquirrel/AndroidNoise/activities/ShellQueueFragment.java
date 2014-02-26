package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;

public class ShellQueueFragment extends BaseShellFragment {
	public static ShellQueueFragment newInstance( int fragmentId ) {
		ShellQueueFragment  fragment = new ShellQueueFragment();
		Bundle              args = new Bundle();

		args.putInt( SHELL_FRAGMENT_KEY, fragmentId );
		fragment.setArguments( args );

		return( fragment );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		if( getChildFragmentManager().findFragmentById( R.id.queue_list_frame ) == null ) {
			getChildFragmentManager()
					.beginTransaction()
					.replace( R.id.queue_list_frame, QueueListFragment.newInstance())
					.commit();
		}

		return( inflater.inflate( R.layout.fragment_queue_shell, container, false ));
	}
}
