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
		QueueListFragment       fragment = QueueListFragment.newInstance();

		fragmentTransaction.add( R.id.QueueShellFrame, fragment );
		fragmentTransaction.commit();

		return( inflater.inflate( R.layout.fragment_queue_shell, container, false ));
	}
}
