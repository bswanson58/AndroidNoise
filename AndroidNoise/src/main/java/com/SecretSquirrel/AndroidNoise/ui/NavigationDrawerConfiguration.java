package com.SecretSquirrel.AndroidNoise.ui;

// Secret Squirrel Software - Created by bswanson on 12/26/13.

import android.widget.BaseAdapter;

public class NavigationDrawerConfiguration {
	private int                     mMainLayout;
	private int                     mDrawerShadow;
	private int                     mDrawerLayoutId;
	private int                     mLeftDrawerId;
	private int[]                   mActionMenuItemsToHideWhenDrawerOpen;
	private NavigationDrawerItem[]  mNavigationItems;
	private int                     mDrawerOpenDescription;
	private int                     mDrawerCloseDescription;
	private BaseAdapter             mBaseAdapter;

	public int getMainLayout() {
		return( mMainLayout );
	}

	public void setMainLayout( int mainLayout ) {
		mMainLayout = mainLayout;
	}

	public int getDrawerShadow() {
		return( mDrawerShadow );
	}

	public void setDrawerShadow( int drawerShadow ) {
		mDrawerShadow = drawerShadow;
	}

	public int getDrawerLayoutId() {
		return( mDrawerLayoutId );
	}

	public void setDrawerLayoutId( int drawerLayoutId ) {
		mDrawerLayoutId = drawerLayoutId;
	}

	public int getLeftDrawerId() {
		return( mLeftDrawerId );
	}

	public void setLeftDrawerId( int leftDrawerId ) {
		mLeftDrawerId = leftDrawerId;
	}

	public int[] getActionMenuItemsToHideWhenDrawerOpen() {
		return( mActionMenuItemsToHideWhenDrawerOpen );
	}

	public void setActionMenuItemsToHideWhenDrawerOpen( int[] actionMenuItemsToHideWhenDrawerOpen ) {
		mActionMenuItemsToHideWhenDrawerOpen = actionMenuItemsToHideWhenDrawerOpen;
	}

	public NavigationDrawerItem[] getNavItems() {
		return( mNavigationItems );
	}

	public void setNavItems( NavigationDrawerItem[] navItems ) {
		mNavigationItems = navItems;
	}

	public int getDrawerOpenDesc() {
		return( mDrawerOpenDescription );
	}

	public void setDrawerOpenDesc( int drawerOpenDesc ) {
		mDrawerOpenDescription = drawerOpenDesc;
	}

	public int getDrawerCloseDesc() {
		return( mDrawerCloseDescription );
	}

	public void setDrawerCloseDesc( int drawerCloseDesc ) {
		mDrawerCloseDescription = drawerCloseDesc;
	}

	public BaseAdapter getBaseAdapter() {
		return( mBaseAdapter );
	}

	public void setBaseAdapter( BaseAdapter baseAdapter ) {
		mBaseAdapter = baseAdapter;
	}
}
