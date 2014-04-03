package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by BSwanson on 3/20/14.

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.events.EventActivityPausing;
import com.SecretSquirrel.AndroidNoise.events.EventActivityResuming;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationServices;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import com.crashlytics.android.Crashlytics;
import javax.inject.Inject;

import de.greenrobot.event.EventBus;

import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.CATEGORY_LAUNCHER;

public class ServerActivity extends ActionBarActivity {
	@Inject	EventBus                mEventBus;

	@SuppressWarnings( "unused" )
	@Inject	IApplicationServices    mApplicationServices;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		Crashlytics.start(this);

		if( isWrongInstance()) {
			finish();
			return;
		}

		IocUtility.inject( this );

		setContentView( R.layout.activity_server_shell );

		if( getSupportFragmentManager().findFragmentById( R.id.container ) == null ) {
			SharedPreferences   settings = PreferenceManager.getDefaultSharedPreferences( this );
			boolean             selectLastServer = settings.getBoolean( getString( R.string.setting_use_last_server ), false );

			getSupportFragmentManager()
					.beginTransaction()
					.replace( R.id.container, ShellServerFragment.newInstance( selectLastServer ))
					.commit();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		mEventBus.post( new EventActivityResuming());
		mEventBus.register( this );
	}

	@Override
	protected void onPause() {
		super.onPause();

		mEventBus.post( new EventActivityPausing());
		mEventBus.unregister( this );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventServerSelected args ) {
		Intent  intent = new Intent( this, LibraryActivity.class );

		// don't come back here...
		intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		intent.addFlags( IntentCompat.FLAG_ACTIVITY_CLEAR_TASK );

		startActivity( intent );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		getMenuInflater().inflate( R.menu.server_activity, menu );

		return( true );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		boolean retValue = false;

		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch( item.getItemId()) {
			case R.id.action_settings:
				Intent intent = new Intent( this, SettingsActivity.class );

				startActivity( intent );
				retValue = true;
				break;
		}

		if(!retValue ) {
			retValue = super.onOptionsItemSelected( item );
		}

		return( retValue );
	}

	/**
	 * Dev tools and the play store (and others?) launch with a different intent, and so
	 * lead to a redundant instance of this activity being spawned.
	 * from: http://stackoverflow.com/questions/17702202/find-out-whether-the-current-activity-will-be-task-root-eventually-after-pendin
	 */
	private boolean isWrongInstance() {
		if(!isTaskRoot()) {
			Intent intent = getIntent();
			boolean isMainAction = (( intent.getAction() != null ) &&
									( intent.getAction().equals( ACTION_MAIN )));

			return(( intent.hasCategory( CATEGORY_LAUNCHER )) &&
				   ( isMainAction ));
		}

		return( false );
	}
}
