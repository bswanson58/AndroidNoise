package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 2/7/14.

import com.SecretSquirrel.AndroidNoise.model.ApplicationModule;
import com.SecretSquirrel.AndroidNoise.services.ServicesModule;

import dagger.Module;

@Module(
		includes = {
				ServicesModule.class,
				ApplicationModule.class
		},
		injects = {
				AlbumInfoFragment.class,
				AlbumListFragment.class,
				ArtistExtendedInfoFragment.class,
				ArtistFragment.class,
				ArtistInfoFragment.class,
				ArtistListFragment.class,
				ArtistTracksFragment.class,
				ArtistTracksAlbumsFragment.class,
				FavoritesListFragment.class,
				LibraryConfiguration.class,
				PlaybackInformationFragment.class,
				QueueListFragment.class,
				RecentlyPlayedListFragment.class,
				RecentlyViewedListFragment.class,
				SearchListFragment.class,
				SearchQueryFragment.class,
				ServerListFragment.class,
				TrackListFragment.class,
				TransportFragment.class,
				ShellActivity.class,
				ShellLibraryFragment.class,
				ShellServerFragment.class
		}
)
public class ActivitiesModule {
}
