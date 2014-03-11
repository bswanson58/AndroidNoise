package com.SecretSquirrel.AndroidNoise.activities;// Created by BSwanson on 3/10/14.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;

public class PlaybackPagerFragment extends Fragment {
	private PageAdapter     mPageAdapter;

	public static PlaybackPagerFragment newInstance() {
		return( new PlaybackPagerFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mPageAdapter = new PageAdapter( getChildFragmentManager());
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_playback_pager, container, false );

		if( myView != null ) {
			ViewPager   viewPager = (ViewPager)myView.findViewById( R.id.pp_container );

			viewPager.setAdapter( mPageAdapter );
		}

		return( myView );
	}

	public static class PageAdapter extends FragmentPagerAdapter {
		public PageAdapter( FragmentManager fragmentManager ) {
			super( fragmentManager );
		}

		@Override
		public int getCount() {
			return( 3 );
		}

		@Override
		public Fragment getItem( int position ) {
			Fragment    fragment = null;

			switch (position) {
				case 0:
					fragment = PlaybackInformationFragment.newInstance();
					break;

				case 1:
					fragment = PlaybackAudioFragment.newInstance();
					break;

				case 2:
					fragment = PlaybackStrategyFragment.newInstance();
			}

			return( fragment );
		}
	}
}
