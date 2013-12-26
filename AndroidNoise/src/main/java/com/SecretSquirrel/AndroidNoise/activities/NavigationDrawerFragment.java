package com.SecretSquirrel.AndroidNoise.activities;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.ui.NavigationDrawerConfiguration;
import com.SecretSquirrel.AndroidNoise.ui.NavigationDrawerItem;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

	// Remember the position of the selected item.
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the user manually
	 * expands it. This shared preference tracks this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	// A pointer to the current callbacks instance (the Activity).
	private NavigationDrawerCallbacks   mCallbacks;

	// Helper component that ties the action bar to the navigation drawer.
	private ActionBarDrawerToggle       mDrawerToggle;

	private DrawerLayout                mDrawerLayout;
	private ListView                    mDrawerListView;
	private View                        mFragmentContainerView;

	private int                         mCurrentSelectedPosition = 0;
	private boolean                     mFromSavedInstanceState;
	private boolean                     mUserLearnedDrawer;

	private CharSequence                    mDrawerTitle;
	private CharSequence                    mTitle;

	private NavigationDrawerConfiguration   mConfiguration;

	public NavigationDrawerFragment() {
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		// Read in the flag indicating whether or not the user has demonstrated awareness of the
		// drawer. See PREF_USER_LEARNED_DRAWER for details.
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( getActivity() );
		mUserLearnedDrawer = sp.getBoolean( PREF_USER_LEARNED_DRAWER, false );

		if( savedInstanceState != null ) {
			mCurrentSelectedPosition = savedInstanceState.getInt( STATE_SELECTED_POSITION );
			mFromSavedInstanceState = true;
		}

		// Indicate that this fragment would like to influence the set of actions in the action bar.
		setHasOptionsMenu( true );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		mDrawerListView = (ListView) inflater.inflate( R.layout.fragment_navigation_drawer, container, false );
		mDrawerListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
				selectPosition( position );
			}
		} );

		return mDrawerListView;
	}

	public void setConfiguration( NavigationDrawerConfiguration configuration ) {
		mConfiguration = configuration;

		mFragmentContainerView = getActivity().findViewById( mConfiguration.getLeftDrawerId());
		mDrawerLayout = (DrawerLayout)getActivity().findViewById( mConfiguration.getDrawerLayoutId());

		mDrawerLayout.setDrawerShadow( mConfiguration.getDrawerShadow(), GravityCompat.START );

		mDrawerListView.setAdapter( mConfiguration.getBaseAdapter());
		mDrawerListView.setItemChecked( mCurrentSelectedPosition, true );

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled( true );
		actionBar.setHomeButtonEnabled( true );

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle( getActivity(), mDrawerLayout, R.drawable.ic_drawer,
												   mConfiguration.getDrawerOpenDesc(), mConfiguration.getDrawerCloseDesc()) {
			@Override
			public void onDrawerClosed( View drawerView ) {
				super.onDrawerClosed( drawerView );
				if( !isAdded()) {
					return;
				}

				getActionBar().setTitle( mTitle );
				getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened( View drawerView ) {
				super.onDrawerOpened( drawerView );
				if( !isAdded() ) {
					return;
				}

				if(!mUserLearnedDrawer ) {
					// The user manually opened the drawer; store this flag to prevent auto-showing
					// the navigation drawer automatically in the future.
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( getActivity() );
					sp.edit().putBoolean( PREF_USER_LEARNED_DRAWER, true ).commit();
				}

				getActionBar().setTitle( mDrawerTitle );
				getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
			}
		};

		// If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
		// per the navigation drawer design guidelines.
		if( !mUserLearnedDrawer && !mFromSavedInstanceState ) {
			mDrawerLayout.openDrawer( mFragmentContainerView );
		}

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post( new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		} );

		mDrawerLayout.setDrawerListener( mDrawerToggle );

		// Select either the default item (0) or the last selected item.
		selectPosition( mCurrentSelectedPosition );
	}

	public boolean isDrawerOpen() {
		return( mDrawerLayout != null && mDrawerLayout.isDrawerOpen( mFragmentContainerView ));
	}

	private void selectPosition( int position ) {
		mCurrentSelectedPosition = position;

		NavigationDrawerItem selectedItem = mConfiguration.getNavItems()[position];

		if( mDrawerListView != null ) {
			mDrawerListView.setItemChecked( position, true );
		}
		if( mDrawerLayout != null ) {
			mDrawerLayout.closeDrawer( mFragmentContainerView );
		}

		if( selectedItem != null ) {
			selectItem( selectedItem );
		}
	}

	public void selectId( int id ) {
		for( NavigationDrawerItem item : mConfiguration.getNavItems()) {
			if( item.getId() == id ) {
				selectItem( item );

				break;
			}
		}
	}

	public void selectItem( NavigationDrawerItem selectedItem ) {
		if( selectedItem != null ) {
			if( selectedItem.updateActionBarTitle()) {
				setTitle( selectedItem.getLabel());
			}

			if( mCallbacks != null ) {
				mCallbacks.onNavigationDrawerItemSelected( selectedItem.getId());
			}
		}
	}

	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle( mTitle );
	}

	@Override
	public void onAttach( Activity activity ) {
		super.onAttach( activity );
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
			mTitle = mDrawerTitle = getActivity().getTitle();
		}
		catch( ClassCastException e ) {
			throw new ClassCastException( "Activity must implement NavigationDrawerCallbacks." );
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();

		mCallbacks = null;
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		outState.putInt( STATE_SELECTED_POSITION, mCurrentSelectedPosition );
	}

	@Override
	public void onConfigurationChanged( Configuration newConfig ) {
		super.onConfigurationChanged( newConfig );

		// Forward the new configuration the drawer toggle component.
		mDrawerToggle.onConfigurationChanged( newConfig );
	}

	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		// If the drawer is open, show the global app actions in the action bar. See also
		// showGlobalContextActionBar, which controls the top-left area of the action bar.
		if( mDrawerLayout != null && isDrawerOpen()) {
			inflater.inflate( R.menu.global, menu );
			showGlobalContextActionBar();
		}

		super.onCreateOptionsMenu( menu, inflater );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		if( mDrawerToggle.onOptionsItemSelected( item )) {
			return true;
		}

		switch( item.getItemId() ) {
			case R.id.action_example:
				Toast.makeText( getActivity(), "Example action.", Toast.LENGTH_SHORT ).show();
				return true;
		}

		return super.onOptionsItemSelected( item );
	}

	/**
	 * Per the navigation drawer design guidelines, updates the action bar to show the global app
	 * 'context', rather than just what's in the current screen.
	 */
	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled( true );
		actionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_STANDARD );
		actionBar.setTitle( R.string.app_name );
	}

	private ActionBar getActionBar() {
		return ((ActionBarActivity) getActivity()).getSupportActionBar();
	}

	/**
	 * Callbacks interface that all activities using this fragment must implement.
	 */
	public static interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected( int position );
	}
}
