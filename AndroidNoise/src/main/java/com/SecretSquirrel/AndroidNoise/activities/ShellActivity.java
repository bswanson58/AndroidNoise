package com.SecretSquirrel.AndroidNoise.activities;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.support.Constants;

import de.greenrobot.event.EventBus;

public class ShellActivity extends ActionBarActivity
						   implements NavigationDrawerFragment.NavigationDrawerCallbacks {
	// Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	private NavigationDrawerFragment mNavigationDrawerFragment;

	// Used to store the last screen title. For use in {@link #restoreActionBar()}.
	private CharSequence    mTitle;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		// from: http://stackoverflow.com/questions/16283079/re-launch-of-activity-on-home-button-but-only-the-first-time?rq=1
		if (!isTaskRoot()) {
			// Android launched another instance of the root activity into an existing task
			//  so just quietly finish and go away, dropping the user back into the activity
			//  at the top of the stack (ie: the last state of this task)
			finish();
			return;
		}

		if( Constants.LOG_DEBUG ) {
			getFragmentManager().enableDebugLogging( true );
		}

		setContentView( R.layout.activity_shell );

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById( R.id.navigation_drawer );
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp( R.id.navigation_drawer, (DrawerLayout) findViewById( R.id.drawer_layout ));

		EventBus.getDefault().register( this );
	}

	public void onEvent( EventServerSelected args ) {
		mNavigationDrawerFragment.selectItem( 1 );
	}

	@Override
	public void onNavigationDrawerItemSelected( int position ) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();

		switch( position ) {
			case 0:
				fragmentManager.beginTransaction().replace( R.id.container, new ShellServerFragment()).commit();
				break;
			case 1:
				fragmentManager.beginTransaction().replace( R.id.container, new ShellLibraryFragment()).commit();
				break;
			case 2:
				fragmentManager.beginTransaction().replace( R.id.container, new ShellFavoritesFragment()).commit();
				break;
			case 3:
				fragmentManager.beginTransaction().replace( R.id.container, new ShellQueueFragment()).commit();
				break;
		}
	}

	public void onSectionAttached( int number ) {
		switch( number ) {
			case 1:
				mTitle = getString( R.string.title_server_section );
				break;
			case 2:
				mTitle = getString( R.string.title_library_section );
				break;
			case 3:
				mTitle = getString( R.string.title_favorites_section );
				break;
			case 4:
				mTitle = getString( R.string.title_queue_section );
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();

		actionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_STANDARD );
		actionBar.setDisplayShowTitleEnabled( true );
		actionBar.setTitle( mTitle );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		if( !mNavigationDrawerFragment.isDrawerOpen() ) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate( R.menu.shell, menu );
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch( item.getItemId() ) {
			case R.id.action_settings:
				return true;
		}
		return super.onOptionsItemSelected( item );
	}
}
