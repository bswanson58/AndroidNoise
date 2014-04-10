package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 1/31/14.

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.dto.ArtistInfo;
import com.SecretSquirrel.AndroidNoise.events.EventNavigationUpEnable;
import com.SecretSquirrel.AndroidNoise.events.EventPlayTrackList;
import com.SecretSquirrel.AndroidNoise.support.Constants;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import de.greenrobot.event.EventBus;

public class ArtistExtendedInfoFragment extends Fragment {
	private static final String     TAG             = ArtistExtendedInfoFragment.class.getName();
	private static final String     ARTIST_KEY      = "ArtistInfoFragment_Artist";
	private static final String     ARTIST_INFO_KEY = "ArtistInfoFragment_ArtistInfo";

	private Artist                  mArtist;
	private Bitmap                  mUnknownArtist;
	private ArtistInfo              mArtistInfo;

	@Inject	EventBus                mEventBus;

	@InjectView( R.id.aei_artist_name )     TextView    mArtistName;
	@InjectView( R.id.aei_artist_genre )    TextView    mArtistGenre;
	@InjectView( R.id.aei_artist_website )  TextView    mArtistWebsite;
	@InjectView( R.id.aei_biography )       WebView     mArtistBiography;
	@InjectView( R.id.aei_artist_image )    ImageView   mArtistImage;
	@InjectView( R.id.aei_band_members )    ListView    mBandMembersListView;
	@InjectView( R.id.aei_similar_artists ) ListView    mSimilarArtistsListView;
	@Optional
	@InjectView( R.id.aei_top_albums )      ListView    mTopAlbumsListView;
	@Optional
	@InjectView( R.id.aei_top_tracks )      ListView    mTopTracksListView;

	public static ArtistExtendedInfoFragment newInstance( Artist artist, ArtistInfo artistInfo ) {
		ArtistExtendedInfoFragment  fragment = new ArtistExtendedInfoFragment();
		Bundle                      args = new Bundle();

		args.putParcelable( ARTIST_KEY, artist );
		args.putParcelable( ARTIST_INFO_KEY, artistInfo );
		fragment.setArguments( args );

		return( fragment );
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		setHasOptionsMenu( true );

		if( savedInstanceState != null ) {
			mArtist = savedInstanceState.getParcelable( ARTIST_KEY );
			mArtistInfo = savedInstanceState.getParcelable( ARTIST_INFO_KEY );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				mArtist = args.getParcelable( ARTIST_KEY );
				mArtistInfo = args.getParcelable( ARTIST_INFO_KEY );
			}
		}

		mUnknownArtist = BitmapFactory.decodeResource( getResources(), R.drawable.unknown_artist );

		if( Constants.LOG_ERROR ) {
			if( mArtist == null ) {
				Log.e( TAG, "Artist is null." );
			}
			if( mArtistInfo == null ) {
				Log.e( TAG, "ArtistInfo is null" );
			}
		}
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View myView = inflater.inflate( R.layout.fragment_artist_extended, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );

			updateDisplay();
		}

		return( myView );
	}

	@Override
	public void onResume() {
		super.onResume();

		mEventBus.post( new EventNavigationUpEnable() );
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		ButterKnife.reset( this );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putParcelable( ARTIST_KEY, mArtist );
		outState.putParcelable( ARTIST_INFO_KEY, mArtistInfo );
	}

	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		inflater.inflate( R.menu.artist_extended_info, menu );

		super.onCreateOptionsMenu( menu, inflater );
	}

	@Override
	public void onPrepareOptionsMenu( Menu menu ) {
		MenuItem    item = menu.findItem( R.id.action_play_artist_top_tracks );

		if( item != null ) {
			item.setEnabled(( mArtistInfo != null ) && ( mArtistInfo.getTopTrackIds().length > 0 ));
		}

		super.onPrepareOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		boolean retValue = true;

		switch( item.getItemId()) {
			case R.id.action_play_artist_top_tracks:
				if(( mArtistInfo != null ) &&
				   ( mArtistInfo.getTopTrackIds().length > 0 )) {
					mEventBus.post( new EventPlayTrackList( mArtistInfo.getTopTrackIds()));
				}
				break;

			case android.R.id.home:
				getActivity().onBackPressed();
				break;

			default:
				retValue = super.onOptionsItemSelected( item );
				break;
		}

		return( retValue );
	}

	private void updateDisplay() {
		if( mArtistInfo != null ) {
			Bitmap artistImage = mArtistInfo.getArtistImage();

			if( artistImage == null ) {
				artistImage = mUnknownArtist;
			}

			mArtistImage.setImageBitmap( artistImage );
			mArtistBiography.loadData( mArtistInfo.getBiography(), "text/html", "utf-8" );
			mArtistWebsite.setText( mArtistInfo.getWebsite());

			mBandMembersListView.setAdapter( new ArrayAdapter<String>( getActivity(), R.layout.simple_list_item, mArtistInfo.getBandMembers()));
			mSimilarArtistsListView.setAdapter( new ArrayAdapter<String>( getActivity(), R.layout.simple_list_item, mArtistInfo.getSimilarArtists()));
			if( mTopAlbumsListView != null ) {
				mTopAlbumsListView.setAdapter( new ArrayAdapter<String>( getActivity(), R.layout.simple_list_item, mArtistInfo.getTopAlbums()));
			}
			if( mTopTracksListView != null ) {
				mTopTracksListView.setAdapter( new ArrayAdapter<String>( getActivity(), R.layout.simple_list_item, mArtistInfo.getTopTracks()));
			}
		}

		if( mArtist != null ) {
			mArtistName.setText( mArtist.getName());
			mArtistGenre.setText( mArtist.getGenre());
		}
	}
}
