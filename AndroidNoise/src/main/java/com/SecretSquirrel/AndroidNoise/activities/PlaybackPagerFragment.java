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

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PlaybackPagerFragment extends Fragment {
	private static final int        PAGE_COUNT     = 4;
	private static final String     CURRENT_PAGE = "playbackInformation_currentPage";

	private PlaybackPageAdapter     mPageAdapter;

	@InjectView( R.id.pp_view_pager ) ViewPager mViewPager;

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
			ButterKnife.inject( this, myView );

			mViewPager.setAdapter( mPageAdapter );
			mViewPager.setOffscreenPageLimit( PAGE_COUNT );

			if( savedInstanceState != null ) {
				mViewPager.setCurrentItem( savedInstanceState.getInt( CURRENT_PAGE ), false );
			}
		}

		return( myView );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		if( mViewPager != null ) {
			outState.putInt( CURRENT_PAGE, mViewPager.getCurrentItem());
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		ButterKnife.reset( this );
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
