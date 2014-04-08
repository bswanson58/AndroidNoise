package com.SecretSquirrel.AndroidNoise.activities;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.LibraryFocusArgs;
import com.SecretSquirrel.AndroidNoise.events.EventActivityPausing;
import com.SecretSquirrel.AndroidNoise.events.EventActivityResuming;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationServices;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;
import com.SecretSquirrel.AndroidNoise.ui.NavigationDrawerAdapter;
import com.SecretSquirrel.AndroidNoise.ui.NavigationDrawerConfiguration;
import com.SecretSquirrel.AndroidNoise.ui.NavigationDrawerItem;
import com.SecretSquirrel.AndroidNoise.ui.NavigationMenuItem;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

import static android.content.Intent.ACTION_MAIN;
import static android.content.Intent.CATEGORY_LAUNCHER;

public class LibraryActivity extends ActionBarActivity
							 implements NavigationDrawerFragment.NavigationDrawerCallbacks,
									    NavigationRequestResponder.NavigationRequestListener {
	public static final int     LIBRARY_ITEM_ID     = 101;
	public static final int     FAVORITES_ITEM_ID   = 102;
	public static final int     QUEUE_ITEM_ID       = 103;
	public static final int     SEARCH_ITEM_ID      = 104;
	public static final int     RECENT_ITEM_ID      = 105;
	public static final int     HISTORY_ITEM_ID     = 106;
	public static final int     SERVERS_ITEM_ID     = 107;

	// Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	private NavigationDrawerFragment    mNavigationDrawerFragment;
	private BaseShellFragment           mCurrentChildFragment;
	private LibraryFocusArgs            mLibraryFocusArgs;

	@Inject EventBus                    mEventBus;
	@Inject IApplicationState           mApplicationState;
	@SuppressWarnings( "unused" )
	@Inject	IApplicationServices        mApplicationServices;
	@Inject	NavigationRequestResponder  mNavigationRequestResponder;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		if( isWrongInstance()) {
			finish();
			return;
		}

		IocUtility.inject( this );

		setContentView( R.layout.activity_library_shell );

		if( getSupportFragmentManager().findFragmentById( R.id.transport_panel ) == null ) {
			getSupportFragmentManager()
					.beginTransaction()
					.replace( R.id.transport_panel, PlaybackShellFragment.newInstance())
					.commit();
		}

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById( R.id.navigation_drawer );
		mNavigationDrawerFragment.setConfiguration( getNavigationDrawerConfiguration() );

		if( savedInstanceState == null ) {
			if( mApplicationState.getIsConnected()) {
				mNavigationDrawerFragment.selectId( LIBRARY_ITEM_ID );
			}
			else {
				launchServerActivity();
			}
		}
		else {
			mCurrentChildFragment = (BaseShellFragment)getSupportFragmentManager().findFragmentById( R.id.container );
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		mEventBus.post( new EventActivityResuming());

		// If we cannot resume operation, display the server selection screen.
		if(!mApplicationState.canResumeWithCurrentServer()) {
			launchServerActivity();
		}

		mNavigationRequestResponder.setListener( this );
	}

	@Override
	protected void onPause() {
		super.onPause();

		mNavigationRequestResponder.setListener( null );
		mEventBus.post( new EventActivityPausing() );
	}

	@Override
	public void onBackPressed() {
		disableActionUp();

		// extend back pressed to the current child fragment stack.
		if( mCurrentChildFragment != null ){
			if( mCurrentChildFragment.getChildFragmentManager().getBackStackEntryCount() > 0 ) {
				mCurrentChildFragment.getChildFragmentManager().popBackStack();
			}
			else {
				super.onBackPressed();
			}

			ActivityCompat.invalidateOptionsMenu( this );
		}
		else {
			super.onBackPressed();
		}

		mCurrentChildFragment = (BaseShellFragment) getSupportFragmentManager().findFragmentById( R.id.container );
		if( mCurrentChildFragment != null ) {
			mNavigationDrawerFragment.syncWithFragment( mCurrentChildFragment.getFragmentId());
		}
		else {
			finish();
		}
	}

	@Override
	public void navigateRequest( int id, LibraryFocusArgs args ) {
		mLibraryFocusArgs = args;

		mNavigationDrawerFragment.selectId( id );
	}

	@Override
	public void enableActionUp() {
		mNavigationDrawerFragment.getDrawerToggle().setDrawerIndicatorEnabled( false );
		getSupportActionBar().setDisplayHomeAsUpEnabled( true );
	}

	private void disableActionUp() {
		mNavigationDrawerFragment.getDrawerToggle().setDrawerIndicatorEnabled( true );
	}

	@Override
	public boolean canSelectNavigationDrawerItem( int itemId ) {
		return( true );
	}

	@Override
	public void onNavigationDrawerItemSelected( int itemId ) {
		disableActionUp();

		// update the main content by replacing fragments
		BaseShellFragment   fragment = null;

		switch( itemId ) {
			case LIBRARY_ITEM_ID:
				fragment = ShellLibraryFragment.newInstance( LIBRARY_ITEM_ID, mLibraryFocusArgs );
				mLibraryFocusArgs = null;
				break;
			case FAVORITES_ITEM_ID:
				fragment = ShellFavoritesFragment.newInstance( FAVORITES_ITEM_ID );
				break;
			case QUEUE_ITEM_ID:
				fragment = ShellQueueFragment.newInstance( QUEUE_ITEM_ID );
				break;
			case SEARCH_ITEM_ID:
				fragment = ShellSearchFragment.newInstance( SEARCH_ITEM_ID );
				break;
			case RECENT_ITEM_ID:
				fragment = ShellRecentFragment.newInstance( RECENT_ITEM_ID );
				break;
			case HISTORY_ITEM_ID:
				fragment = ShellPlayHistoryFragment.newInstance( HISTORY_ITEM_ID );
				break;
			case SERVERS_ITEM_ID:
				launchServerActivity();
				break;
		}

		if( fragment != null ) {
			FragmentManager fragmentManager = getSupportFragmentManager();

			fragmentManager.beginTransaction()
					.replace( R.id.container, fragment )
					.addToBackStack( null )
					.commit();

			mCurrentChildFragment = fragment;
		}

		ActivityCompat.invalidateOptionsMenu( this );
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();

		actionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_STANDARD );
		actionBar.setDisplayShowTitleEnabled( true );
		actionBar.setTitle( mNavigationDrawerFragment.getTitle() );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		if(!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate( R.menu.library_activity, menu );
			restoreActionBar();

			return( true );
		}

		return super.onCreateOptionsMenu( menu );
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

	private NavigationDrawerConfiguration getNavigationDrawerConfiguration() {
		NavigationDrawerConfiguration   retValue = new NavigationDrawerConfiguration();
		NavigationDrawerItem[]          menu = new NavigationDrawerItem[] {
				NavigationMenuItem.create( LIBRARY_ITEM_ID, getString( R.string.title_library_section ), "ic_action_library", true, this ),
				NavigationMenuItem.create( FAVORITES_ITEM_ID, getString( R.string.title_favorites_section ), "ic_action_favorites", true, this ),
				NavigationMenuItem.create( QUEUE_ITEM_ID, getString( R.string.title_queue_section ), "ic_action_queue", true, this ),
				NavigationMenuItem.create( SEARCH_ITEM_ID, getString( R.string.title_search_section ), "ic_action_search", true, this ),
				NavigationMenuItem.create( RECENT_ITEM_ID, getString( R.string.title_recent_section ), "ic_action_recent", true, this ),
				NavigationMenuItem.create( HISTORY_ITEM_ID, getString( R.string.title_history_section ), "ic_action_play_history", true, this ),
				NavigationMenuItem.create( SERVERS_ITEM_ID, getString( R.string.title_server_section ), "ic_action_servers", true, this )};

		retValue.setApplicationNameId( R.string.app_name );
		retValue.setGlobalMenuId( R.menu.library_activity );
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

	private void launchServerActivity() {
		Intent  intent = new Intent( this, ServerActivity.class );

		// don't come back here...
		intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		intent.addFlags( IntentCompat.FLAG_ACTIVITY_CLEAR_TASK );

		startActivity( intent );
	}

	/**
	 * Dev tools and the play store (and others?) launch with a different intent, and so
	 * lead to a redundant instance of this activity being spawned.
	 * from: http://stackoverflow.com/questions/17702202/find-out-whether-the-current-activity-will-be-task-root-eventually-after-pendin
	 */
	private boolean isWrongInstance() {
		if(!isTaskRoot()) {
			Intent  intent = getIntent();
			boolean isMainAction = (( intent.getAction() != null ) &&
									( intent.getAction().equals( ACTION_MAIN )));

			return(( intent.hasCategory( CATEGORY_LAUNCHER )) &&
				   ( isMainAction ));
		}

		return( false );
	}
}
