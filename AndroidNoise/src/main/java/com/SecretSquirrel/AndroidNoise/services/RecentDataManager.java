package com.SecretSquirrel.AndroidNoise.services;

// Secret Squirrel Software - Created by BSwanson on 2/4/14.

import android.content.Context;
import android.text.TextUtils;

import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.events.EventArtistPlayed;
import com.SecretSquirrel.AndroidNoise.events.EventArtistViewed;
import com.SecretSquirrel.AndroidNoise.events.EventRecentDataUpdated;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.IRecentData;
import com.SecretSquirrel.AndroidNoise.interfaces.IRecentDataManager;
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

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class RecentDataManager implements IRecentData, IRecentDataManager {
	private final static String     RECENTLY_VIEWED_FILE_NAME_FORMAT = "recently viewed - %s";
	private final static String     RECENTLY_PLAYED_FILE_NAME_FORMAT = "recently played - %s";

	private final Context           mContext;
	private final EventBus          mEventBus;
	private final IApplicationState mApplicationState;
	private RecentArtistList        mRecentlyPlayedList;
	private RecentArtistList        mRecentlyViewedList;
	private String                  mCurrentHostName;

	@Inject
	public RecentDataManager( Context context, EventBus eventBus, IApplicationState applicationState ) {
		mContext = context;
		mEventBus = eventBus;
		mApplicationState = applicationState;

		mRecentlyPlayedList = new RecentArtistList( Constants.RECENT_LIST_SIZE );
		mRecentlyViewedList = new RecentArtistList( Constants.RECENT_LIST_SIZE );
	}

	@Override
	public IRecentData getRecentData() {
		return( this );
	}

	@Override
	public void start() {
		mRecentlyViewedList.clear();
		mRecentlyPlayedList.clear();

		if( mApplicationState.getIsConnected()) {
			mCurrentHostName = mApplicationState.getCurrentServer().getServerName();

			loadArtistList( mRecentlyViewedList, String.format( RECENTLY_VIEWED_FILE_NAME_FORMAT, mCurrentHostName ));
			loadArtistList( mRecentlyPlayedList, String.format( RECENTLY_PLAYED_FILE_NAME_FORMAT, mCurrentHostName ));
		}

		if(!mEventBus.isRegistered( this )) {
			mEventBus.register( this );
		}
	}

	@Override
	public void persistData() {
		if(!TextUtils.isEmpty( mCurrentHostName )) {
			saveArtistList( mRecentlyViewedList, String.format( RECENTLY_VIEWED_FILE_NAME_FORMAT, mCurrentHostName ));
			saveArtistList( mRecentlyPlayedList, String.format( RECENTLY_PLAYED_FILE_NAME_FORMAT, mCurrentHostName ));
		}
	}

	@Override
	public void stop() {
		mEventBus.unregister( this );

		mCurrentHostName = "";
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
		mRecentlyViewedList.putMostRecentArtist( args.getArtist());

		mEventBus.post( new EventRecentDataUpdated());
	}

	@SuppressWarnings("unused")
	public void onEvent( EventArtistPlayed args ) {
		mRecentlyPlayedList.putMostRecentArtist( args.getArtist());

		mEventBus.post( new EventRecentDataUpdated());
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
			Timber.d( String.format( "The recent artist file (%s) is not present.", fileName ));
		}
		catch( Exception ex ) {
			Timber.e( String.format( "Could not read recent artist file (%s)", fileName ));
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
			Timber.e( ex, "Could not write recently viewed artist file." );
		}
	}
}
