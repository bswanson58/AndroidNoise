package com.SecretSquirrel.AndroidNoise.activities;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.events.EventServerSelected;
import com.SecretSquirrel.AndroidNoise.ui.NavigationDrawerAdapter;
import com.SecretSquirrel.AndroidNoise.ui.NavigationDrawerConfiguration;
import com.SecretSquirrel.AndroidNoise.ui.NavigationDrawerItem;
import com.SecretSquirrel.AndroidNoise.ui.NavigationMenuItem;

import de.greenrobot.event.EventBus;

public class ShellActivity extends ActionBarActivity
						   implements NavigationDrawerFragment.NavigationDrawerCallbacks {
	private final int   LIBRARY_ITEM_ID     = 101;
	private final int   FAVORITES_ITEM_ID   = 102;
	private final int   QUEUE_ITEM_ID       = 103;
	private final int   SERVERS_ITEM_ID     = 104;
	private final int   SEARCH_ITEM_ID      = 105;

	// Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	private NavigationDrawerFragment    mNavigationDrawerFragment;

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

		setContentView( R.layout.activity_shell );

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById( R.id.navigation_drawer );
		mNavigationDrawerFragment.setConfiguration( getNavigationDrawerConfiguration());

		if( savedInstanceState == null ) {
			mNavigationDrawerFragment.selectId( SERVERS_ITEM_ID );
		}

		EventBus.getDefault().register( this );
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister( this );

		super.onDestroy();
	}

	private NavigationDrawerConfiguration getNavigationDrawerConfiguration() {
		NavigationDrawerConfiguration   retValue = new NavigationDrawerConfiguration();
		NavigationDrawerItem[]          menu = new NavigationDrawerItem[] {
				NavigationMenuItem.create( LIBRARY_ITEM_ID, getString( R.string.title_library_section ), "", true, this ),
				NavigationMenuItem.create( FAVORITES_ITEM_ID, getString( R.string.title_favorites_section ), "", true, this ),
				NavigationMenuItem.create( QUEUE_ITEM_ID, getString( R.string.title_queue_section ), "", true, this ),
				NavigationMenuItem.create( SERVERS_ITEM_ID, getString( R.string.title_server_section ), "", true, this ),
				NavigationMenuItem.create( SEARCH_ITEM_ID, getString( R.string.title_search_section ), "", true, this )};

		retValue.setApplicationNameId( R.string.app_name );
		retValue.setGlobalMenuId( R.menu.global );
		retValue.setNavigationDrawerId( R.id.navigation_drawer );
		retValue.setNavigationItems( menu );
		retValue.setDrawerLayoutId( R.id.drawer_layout );
		retValue.setDrawerIconId( R.drawable.ic_drawer );
		retValue.setDrawerShadow( R.drawable.drawer_shadow );
		retValue.setDrawerOpenDesc( R.string.navigation_drawer_open );
		retValue.setDrawerCloseDesc( R.string.navigation_drawer_close );
		retValue.setBaseAdapter( new NavigationDrawerAdapter( this, R.layout.navigation_drawer_item, menu ));

		return( retValue );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventServerSelected args ) {
		mNavigationDrawerFragment.selectId( LIBRARY_ITEM_ID );
	}

	@Override
	public void onNavigationDrawerItemSelected( int itemId ) {
		// update the main content by replacing fragments
		Fragment    fragment = null;

		switch( itemId ) {
			case SERVERS_ITEM_ID:
				fragment = new ShellServerFragment();
				break;
			case LIBRARY_ITEM_ID:
				fragment = new ShellLibraryFragment();
				break;
			case FAVORITES_ITEM_ID:
				fragment = new ShellFavoritesFragment();
				break;
			case QUEUE_ITEM_ID:
				fragment = new ShellQueueFragment();
				break;
			case SEARCH_ITEM_ID:
				fragment = new ShellSearchFragment();
				break;
		}

		if( fragment != null ) {
			FragmentManager fragmentManager = getSupportFragmentManager();

			fragmentManager.beginTransaction()
					.replace( R.id.container, fragment )
					.addToBackStack( null )
					.commit();
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();

		actionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_STANDARD );
		actionBar.setDisplayShowTitleEnabled( true );
		actionBar.setTitle( mNavigationDrawerFragment.getTitle());
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		if(!mNavigationDrawerFragment.isDrawerOpen()) {
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
		switch( item.getItemId()) {
			case R.id.action_settings:
				return true;
		}
		return super.onOptionsItemSelected( item );
	}
}
