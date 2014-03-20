package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by BSwanson on 3/20/14.

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBarActivity;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.events.EventActivityPausing;
import com.SecretSquirrel.AndroidNoise.events.EventActivityResuming;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.CATEGORY_LAUNCHER;

public class ServerActivity extends ActionBarActivity {
	private boolean         mSelectLastServer;

	@Inject	EventBus        mEventBus;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		if( isWrongInstance()) {
			finish();
			return;
		}

		IocUtility.inject( this );

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( this );
		mSelectLastServer = settings.getBoolean( getString( R.string.setting_use_last_server ), false );

		setContentView( R.layout.server_shell );

		if( getSupportFragmentManager().findFragmentById( R.id.container ) == null ) {
			getSupportFragmentManager()
					.beginTransaction()
					.replace( R.id.container, ShellServerFragment.newInstance( 104, mSelectLastServer ))
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
		Intent  intent = new Intent( this, ShellActivity.class );

		// don't come back here...
		intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		intent.addFlags( IntentCompat.FLAG_ACTIVITY_CLEAR_TASK );

		startActivity( intent );
	}

	/**
	 * Dev tools and the play store (and others?) launch with a different intent, and so
	 * lead to a redundant instance of this activity being spawned. <a
	 * href="http://stackoverflow.com/questions/17702202/find-out-whether-the-current-activity-will-be-task-root-eventually-after-pendin"
	 * >Details</a>.
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
