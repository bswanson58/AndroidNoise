package com.SecretSquirrel.AndroidNoise.events;

import com.SecretSquirrel.AndroidNoise.dto.Favorite;

// Created by BSwanson on 12/28/13.

public class EventPlayFavorite {
	private Favorite    mFavorite;

	public EventPlayFavorite( Favorite favorite ) {
		mFavorite = favorite;
	}

	public Favorite getFavorite() {
		return( mFavorite );
	}
}
