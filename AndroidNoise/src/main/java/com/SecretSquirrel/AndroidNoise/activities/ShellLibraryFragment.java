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
import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.events.EventAlbumSelected;
import com.SecretSquirrel.AndroidNoise.events.EventArtistSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;

import de.greenrobot.event.EventBus;

public class ShellLibraryFragment extends Fragment {
	private FragmentManager     mFragmentManager;

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View                myView = inflater.inflate( R.layout.fragment_library_shell, container, false );

		mFragmentManager = getFragmentManager();

		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		ArtistListFragment  fragment = new ArtistListFragment();

		fragmentTransaction.add( R.id.LibraryShellFrame, fragment );
		fragmentTransaction.commit();

		EventBus.getDefault().register( this );

		return( myView );
	}

	public void onEvent( EventArtistSelected args ) {
		Artist artist = args.getArtist();

		if( artist != null ) {
			getApplicationState().setCurrentArtist( artist );

			ArtistFragment      fragment = new ArtistFragment();
			FragmentTransaction transaction = mFragmentManager.beginTransaction().replace( R.id.LibraryShellFrame, fragment );

			transaction.addToBackStack( "artistFragment" );
			transaction.commit();
		}
	}

	public void onEvent( EventAlbumSelected args ) {
		Album   album = args.getAlbum();

		if( album != null ) {
			getApplicationState().setCurrentAlbum( album );

			AlbumFragment       fragment = new AlbumFragment();
			FragmentTransaction transaction = mFragmentManager.beginTransaction().replace( R.id.LibraryShellFrame, fragment );

			transaction.addToBackStack( "albumFragment" );
			transaction.commit();
		}
	}

	private IApplicationState getApplicationState() {
		NoiseRemoteApplication application = (NoiseRemoteApplication)getActivity().getApplication();

		return( application.getApplicationState());
	}
}
