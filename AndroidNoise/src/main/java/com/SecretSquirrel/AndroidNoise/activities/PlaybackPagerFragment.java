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
	private static final int PAGE_COUNT     = 4;
	
	private PlaybackPageAdapter     mPageAdapter;

	public static PlaybackPagerFragment newInstance() {
		return( new PlaybackPagerFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		mPageAdapter = new PlaybackPageAdapter( getChildFragmentManager());
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_playback_pager, container, false );

		if( myView != null ) {
			ViewPager   viewPager = (ViewPager)myView.findViewById( R.id.pp_view_pager );

			viewPager.setAdapter( mPageAdapter );
			viewPager.setOffscreenPageLimit( PAGE_COUNT );
		}

		return( myView );
	}

	protected class PlaybackPageAdapter extends FragmentPagerAdapter {
		public PlaybackPageAdapter( FragmentManager fragmentManager ) {
			super( fragmentManager );
		}

		@Override
		public int getCount() {
			return( PAGE_COUNT );
		}

		@Override
		public Fragment getItem( int position ) {
			Fragment    fragment = null;

			switch( position ) {
				case 0:
					fragment = PlaybackInformationFragment.newInstance();
					break;

				case 1:
					fragment = PlaybackAudioFragment.newInstance();
					break;

				case 2:
					fragment = PlaybackPlayStrategyFragment.newInstance();
					break;

				case 3:
					fragment = PlaybackExhaustedStrategyFragment.newInstance();
					break;
			}

			return( fragment );
		}
	}
}
