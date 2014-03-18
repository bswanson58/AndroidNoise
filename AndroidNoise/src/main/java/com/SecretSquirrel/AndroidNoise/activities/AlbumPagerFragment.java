package com.SecretSquirrel.AndroidNoise.activities;

// Created by BSwanson on 3/18/14.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseData;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class AlbumPagerFragment extends Fragment
								implements ServiceResultReceiver.Receiver{
	private static final String     ARTIST = "albumPager_artist";
	private static final String     ALBUM = "albumPager_firstAlbum";
	private static final String     ALBUM_LIST = "albumPager_albumList";
	private static final String     EXTERNAL_REQUEST = "albumPager_externalRequest";

	private Artist                  mCurrentArtist;
	private Album                   mCurrentAlbum;
	private List<Album>             mAlbumList;
	private boolean                 mIsExternalRequest;

	@Inject	INoiseData              mNoiseData;
	@Inject	ServiceResultReceiver   mReceiver;

	@InjectView( R.id.ap_view_pager )	        ViewPager       mViewPager;
	@InjectView( R.id.ap_view_pager_indicator )	UnderlinePageIndicator mPageIndicator;

	public static AlbumPagerFragment newInstance( Artist artist, Album album, boolean isExternalRequest ) {
		AlbumPagerFragment  fragment = new AlbumPagerFragment();
		Bundle              args = new Bundle();

		args.putParcelable( ARTIST, artist );
		args.putParcelable( ALBUM, album );
		args.putBoolean( EXTERNAL_REQUEST, isExternalRequest );
		fragment.setArguments( args );

		return( fragment);
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		if( savedInstanceState != null ) {
			mCurrentArtist = savedInstanceState.getParcelable( ARTIST );
			mCurrentAlbum = savedInstanceState.getParcelable( ALBUM );
			mAlbumList = savedInstanceState.getParcelableArrayList( ALBUM_LIST );
			mIsExternalRequest = savedInstanceState.getBoolean( EXTERNAL_REQUEST );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				mCurrentArtist = args.getParcelable( ARTIST );
				mCurrentAlbum = args.getParcelable( ALBUM );
				mIsExternalRequest = args.getBoolean( EXTERNAL_REQUEST );
			}
		}

		if( mCurrentArtist == null ) {
			Timber.e( "Current Artist could not be determined." );
		}

		if( mCurrentAlbum == null ) {
			Timber.e( "Current Album could not be determined." );
		}

		if( mAlbumList == null ) {
			retrieveAlbums();
		}
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_album_pager, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView  );

		}

		return( myView );
	}

	private void retrieveAlbums() {
		mReceiver.setReceiver( this );

		mNoiseData.GetAlbumList( mCurrentArtist.getArtistId(), mReceiver );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putBoolean( EXTERNAL_REQUEST, mIsExternalRequest );
		outState.putParcelable( ARTIST, mCurrentArtist );
		outState.putParcelable( ALBUM, mCurrentAlbum );
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			int callCode = resultData.getInt( NoiseRemoteApi.RemoteApiParameter );

			if( callCode == NoiseRemoteApi.GetAlbumList ) {
				ArrayList<Album> albumList = resultData.getParcelableArrayList( NoiseRemoteApi.AlbumList );

				setAlbumList( albumList );
			}
		}
	}

	private void setAlbumList( List<Album> albumList ) {
		mAlbumList = albumList;

		Collections.sort( mAlbumList, new Comparator<Album>() {
			public int compare( Album album1, Album album2 ) {
				return (album1.getName().compareToIgnoreCase( album2.getName() ));
			}
		} );

		int position = 0;

		for( Album album : mAlbumList ) {
			if( album.getAlbumId() == mCurrentAlbum.getAlbumId()) {
				break;
			}
			else {
				position++;
			}
		}

		mViewPager.setAdapter( new AlbumPageAdapter( getChildFragmentManager()));
		mPageIndicator.setViewPager( mViewPager );
		mPageIndicator.setOnPageChangeListener( new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected( int i ) {
				mCurrentAlbum = mAlbumList.get( i );
			}

			@Override
			public void onPageScrolled( int i, float v, int i2 ) { }
			@Override
			public void onPageScrollStateChanged( int i ) {	}
		} );

		mViewPager.setCurrentItem( position, false );
	}

	protected class AlbumPageAdapter extends FragmentStatePagerAdapter {
		public AlbumPageAdapter( FragmentManager fragmentManager ) {
			super( fragmentManager );
		}

		@Override
		public int getCount() {
			return( mAlbumList.size());
		}

		@Override
		public Fragment getItem( int position ) {
			return( AlbumFragment.newInstance( mCurrentArtist, mAlbumList.get( position ), mIsExternalRequest ));
		}
	}
}
