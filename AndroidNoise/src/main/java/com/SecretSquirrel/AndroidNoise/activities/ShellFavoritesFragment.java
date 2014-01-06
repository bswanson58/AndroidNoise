package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/23/13.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;

public class ShellFavoritesFragment extends BaseShellFragment {
	public static ShellFavoritesFragment newInstance( int fragmentId ) {
		return( new ShellFavoritesFragment( fragmentId ));
	}

	protected ShellFavoritesFragment( int fragmentId ) {
		super( fragmentId );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		getChildFragmentManager()
				.beginTransaction()
				.replace( R.id.FavoritesShellFrame, FavoritesListFragment.newInstance())
				.commit();

		return( inflater.inflate( R.layout.fragment_favorites_shell, container, false ));
	}
}
