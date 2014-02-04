package com.SecretSquirrel.AndroidNoise.interfaces;

// Secret Squirrel Software - Created by bswanson on 2/4/14.

import com.SecretSquirrel.AndroidNoise.dto.Artist;

import java.util.List;

public interface IRecentData {
	List<Artist>    getRecentlyPlayedArtists();
	List<Artist>    getRecentlyViewedArtists();
}
