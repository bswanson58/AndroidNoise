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

public class ShellFavoritesFragment extends Fragment {
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		FragmentManager         fragmentManager = getFragmentManager();
		FragmentTransaction     fragmentTransaction = fragmentManager.beginTransaction();
		FavoritesListFragment   fragment = FavoritesListFragment.newInstance();

		fragmentTransaction.add( R.id.FavoritesShellFrame, fragment );
		fragmentTransaction.commit();

		return( inflater.inflate( R.layout.fragment_favorites_shell, container, false ));
	}
}
