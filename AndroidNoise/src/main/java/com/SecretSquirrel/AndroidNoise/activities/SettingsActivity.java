package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 2/4/14.

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.SecretSquirrel.AndroidNoise.R;

public class SettingsActivity extends PreferenceActivity {
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		addPreferencesFromResource( R.xml.noise_preferences );
	}
}
