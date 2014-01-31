package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 1/31/14.

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.dto.ArtistInfo;

public class ArtistExtendedInfoFragment extends Fragment {
	private static final String     ARTIST_KEY      = "ArtistInfoFragment_Artist";
	private static final String     ARTIST_INFO_KEY = "ArtistInfoFragment_ArtistInfo";

	private Artist                  mArtist;
	private ArtistInfo              mArtistInfo;
	private TextView                mArtistName;
	private TextView                mArtistGenre;
	private TextView                mArtistWebsite;
	private TextView                mArtistBiography;
	private ImageView               mArtistImage;
	private Bitmap                  mUnknownArtist;

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
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View myView = inflater.inflate( R.layout.fragment_artist_extended, container, false );

		if( myView != null ) {
			mArtistName = (TextView)myView.findViewById( R.id.aei_artist_name );
			mArtistGenre = (TextView)myView.findViewById( R.id.aei_artist_genre );
			mArtistWebsite = (TextView)myView.findViewById( R.id.aei_artist_website );
			mArtistBiography = (TextView)myView.findViewById( R.id.aei_biography );
			mArtistImage = (ImageView)myView.findViewById( R.id.aei_artist_image );

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
			mArtistBiography.setText( mArtistInfo.getBiography());
			mArtistWebsite.setText( mArtistInfo.getWebsite());
		}

		if( mArtist != null ) {
			mArtistName.setText( mArtist.getName());
			mArtistGenre.setText( mArtist.getGenre());
		}
	}
}
