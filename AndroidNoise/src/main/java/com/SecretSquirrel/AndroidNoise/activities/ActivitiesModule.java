package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 2/7/14.

import com.SecretSquirrel.AndroidNoise.application.ApplicationModule;
import com.SecretSquirrel.AndroidNoise.models.LibraryState;
import com.SecretSquirrel.AndroidNoise.services.ServicesModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@SuppressWarnings( "unused" )
@Module(
		includes = {
				ServicesModule.class,
				ApplicationModule.class
		},
		injects = {
				AlbumInfoFragment.class,
				AlbumListFragment.class,
				AlbumPagerFragment.class,
				ArtistExtendedInfoFragment.class,
				ArtistFragment.class,
				ArtistInfoFragment.class,
				ArtistListFragment.class,
				ArtistTracksFragment.class,
				ArtistTracksAlbumsFragment.class,
				FavoritesListFragment.class,
				LibraryActivity.class,
				LibraryConfiguration.class,
				PlaybackAudioFragment.class,
				PlaybackExhaustedStrategyFragment.class,
				PlaybackInformationFragment.class,
				PlaybackStatusFragment.class,
				PlaybackPlayStrategyFragment.class,
				QueueListFragment.class,
				RecentlyPlayedListFragment.class,
				RecentlyViewedListFragment.class,
				SearchListFragment.class,
				SearchQueryFragment.class,
				ServerActivity.class,
				ServerListFragment.class,
				LibraryActivity.class,
				ShellLibraryFragment.class,
				ShellServerFragment.class,
				TrackListFragment.class,
				TransportFragment.class
		}
)
public class ActivitiesModule {
	@Singleton
	@Provides
	public LibraryState providesLibraryState() {
		return( new LibraryState());
	}
}
