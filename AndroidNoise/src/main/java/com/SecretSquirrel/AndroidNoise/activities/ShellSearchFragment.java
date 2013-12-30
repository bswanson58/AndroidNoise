package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/30/13.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;

public class ShellSearchFragment extends Fragment {
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		FragmentManager     fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		SearchListFragment  searchListFragment = SearchListFragment.newInstance();
		SearchQueryFragment searchQueryFragment = SearchQueryFragment.newInstance();

		fragmentTransaction.add( R.id.search_list_frame, searchListFragment );
		fragmentTransaction.add( R.id.search_query_frame, searchQueryFragment );
		fragmentTransaction.commit();

		return( inflater.inflate( R.layout.fragment_search_shell, container, false ));
	}
}
