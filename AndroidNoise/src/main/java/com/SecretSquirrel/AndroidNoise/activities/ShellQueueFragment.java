package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;

public class ShellQueueFragment extends Fragment {
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		FragmentManager         fragmentManager = getFragmentManager();
		FragmentTransaction     fragmentTransaction = fragmentManager.beginTransaction();
		QueueListFragment       queueFragment = QueueListFragment.newInstance();
		TransportFragment       transportFragment = TransportFragment.newInstance();

		fragmentTransaction.add( R.id.queue_list_frame, queueFragment );
		fragmentTransaction.add( R.id.transport_commands_frame, transportFragment );
		fragmentTransaction.commit();

		return( inflater.inflate( R.layout.fragment_queue_shell, container, false ));
	}
}
