package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 1/31/14.

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.SecretSquirrel.AndroidNoise.support.Constants;

public class ArtistExtendedInfoFragment extends Fragment {
	private static final String     TAG             = ArtistExtendedInfoFragment.class.getName();
	private static final String     ARTIST_KEY      = "ArtistInfoFragment_Artist";
	private static final String     ARTIST_INFO_KEY = "ArtistInfoFragment_ArtistInfo";

	private Artist                  mArtist;
	private ArtistInfo              mArtistInfo;
	private TextView                mArtistName;
	private TextView                mArtistGenre;
	private TextView                mArtistWebsite;
	private WebView                 mArtistBiography;
	private ImageView               mArtistImage;
	private Bitmap                  mUnknownArtist;
	private ListView                mBandMembersListView;
	private ListView                mSimilarArtistsListView;
	private ListView                mTopAlbumsListView;

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
			mArtistName = (TextView)myView.findViewById( R.id.aei_artist_name );
			mArtistGenre = (TextView)myView.findViewById( R.id.aei_artist_genre );
			mArtistWebsite = (TextView)myView.findViewById( R.id.aei_artist_website );
			mArtistBiography = (WebView)myView.findViewById( R.id.aei_biography );
			mArtistImage = (ImageView)myView.findViewById( R.id.aei_artist_image );
			mBandMembersListView = (ListView)myView.findViewById( R.id.aei_band_members );
			mSimilarArtistsListView = (ListView)myView.findViewById( R.id.aei_similar_artists );
			mTopAlbumsListView = (ListView)myView.findViewById( R.id.aei_top_albums );

			updateDisplay();
		}

		return( myView );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putParcelable( ARTIST_KEY, mArtist );
		outState.putParcelable( ARTIST_INFO_KEY, mArtistInfo );
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
			mTopAlbumsListView.setAdapter( new ArrayAdapter<String>( getActivity(), R.layout.simple_list_item, mArtistInfo.getTopAlbums()));
		}

		if( mArtist != null ) {
			mArtistName.setText( mArtist.getName());
			mArtistGenre.setText( mArtist.getGenre());
		}
	}
}