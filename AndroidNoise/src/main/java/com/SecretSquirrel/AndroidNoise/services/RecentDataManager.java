package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by bswanson on 2/4/14.

import android.content.Context;
import android.util.Log;

import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.events.EventArtistPlayed;
import com.SecretSquirrel.AndroidNoise.events.EventArtistViewed;
import com.SecretSquirrel.AndroidNoise.events.EventRecentDataUpdated;
import com.SecretSquirrel.AndroidNoise.interfaces.IRecentData;
import com.SecretSquirrel.AndroidNoise.support.Constants;
import com.SecretSquirrel.AndroidNoise.support.NoiseUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class RecentDataManager implements IRecentData {
	private final static String     TAG = RecentDataManager.class.getName();
	private final static String     RECENTLY_VIEWED_FILE_NAME_FORMAT = "recently viewed - %s";
	private final static String     RECENTLY_PLAYED_FILE_NAME_FORMAT = "recently played - %s";

	private final Context           mContext;
	private final ServerInformation mCurrentServer;
	private RecentArtistList        mRecentlyPlayedList;
	private RecentArtistList        mRecentlyViewedList;

	public RecentDataManager( Context context, ServerInformation currentServer ) {
		mContext = context;
		mCurrentServer = currentServer;

		mRecentlyPlayedList = new RecentArtistList( Constants.RECENT_LIST_SIZE );
		mRecentlyViewedList = new RecentArtistList( Constants.RECENT_LIST_SIZE );
	}

	@Override
	public void start() {
		loadArtistList( mRecentlyViewedList,
						String.format( RECENTLY_VIEWED_FILE_NAME_FORMAT,
									   mCurrentServer.getHostName()));
		loadArtistList( mRecentlyPlayedList,
						String.format( RECENTLY_PLAYED_FILE_NAME_FORMAT,
									   mCurrentServer.getHostName()));

		EventBus.getDefault().register( this );
	}

	@Override
	public void persistData() {
		saveArtistList( mRecentlyViewedList,
						String.format( RECENTLY_VIEWED_FILE_NAME_FORMAT,
									   mCurrentServer.getHostName()));
		saveArtistList( mRecentlyPlayedList,
						String.format( RECENTLY_PLAYED_FILE_NAME_FORMAT,
									   mCurrentServer.getHostName()));
	}

	@Override
	public void stop() {
		EventBus.getDefault().unregister( this );
	}

	@Override
	public List<Artist> getRecentlyPlayedArtists() {
		return( mRecentlyPlayedList );
	}

	@Override
	public List<Artist> getRecentlyViewedArtists() {
		return( mRecentlyViewedList );
	}

	@SuppressWarnings("unused")
	public void onEvent( EventArtistViewed args ) {
		mRecentlyViewedList.putMostRecentArtist( args.getArtist() );

		EventBus.getDefault().post( new EventRecentDataUpdated() );
	}

	@SuppressWarnings("unused")
	public void onEvent( EventArtistPlayed args ) {
		mRecentlyPlayedList.putMostRecentArtist( args.getArtist() );

		EventBus.getDefault().post( new EventRecentDataUpdated() );
	}

	private void loadArtistList( RecentArtistList artistList, String fileName ) {
		ArrayList<Artist>   artists = loadArtistList( fileName );

		artistList.clear();

		if( artists != null ) {
			for( Artist artist : artists ) {
				artistList.add( artist );
			}
		}
	}

	private ArrayList<Artist> loadArtistList( String fileName ) {
		ArrayList<Artist>   retValue = null;
		Gson                gson = new Gson();

		try {
			FileInputStream     inputStream = mContext.openFileInput( fileName );
			String              recentlyViewed = NoiseUtils.convertStreamToString( inputStream );
			Type                collectionType = new TypeToken<ArrayList<Artist>>(){}.getType();

			retValue = gson.fromJson( recentlyViewed, collectionType );
		} catch( FileNotFoundException e ) {
			if( Constants.LOG_ERROR ) {
				Log.d( TAG, String.format( "The recent artist file (%s) is not present.", fileName ));
			}
		}
		catch( Exception ex ) {
			if( Constants.LOG_ERROR ) {
				Log.e( TAG, String.format( "Could not read recent artist file (%s)", fileName ));
			}
		}

		return( retValue );
	}

	private void saveArtistList( ArrayList<Artist> artistList, String fileName ) {
		try {
			Gson                gson = new Gson();
			FileOutputStream    outputStream = mContext.openFileOutput( fileName, Context.MODE_PRIVATE );
			String              recentlyViewed = gson.toJson( artistList );

			outputStream.write( recentlyViewed.getBytes());
		}
		catch( Exception ex ) {
			if( Constants.LOG_ERROR ) {
				Log.e( TAG, "Could not write recently viewed artist file." );
			}
		}
	}
}
