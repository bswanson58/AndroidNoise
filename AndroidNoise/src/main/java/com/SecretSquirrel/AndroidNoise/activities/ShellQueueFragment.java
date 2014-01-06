package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;

public class ShellQueueFragment extends Fragment {
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		getChildFragmentManager()
				.beginTransaction()
				.replace( R.id.queue_list_frame, QueueListFragment.newInstance())
				.replace( R.id.transport_commands_frame, TransportFragment.newInstance())
				.commit();

		return( inflater.inflate( R.layout.fragment_queue_shell, container, false ));
	}
}
