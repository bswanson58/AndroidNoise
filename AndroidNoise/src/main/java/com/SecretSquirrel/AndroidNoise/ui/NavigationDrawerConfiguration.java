package com.SecretSquirrel.AndroidNoise.ui;

// Secret Squirrel Software - Created by bswanson on 12/26/13.

import android.widget.BaseAdapter;

public class NavigationDrawerConfiguration {
	private int                     mDrawerShadow;
	private int                     mDrawerLayoutId;
	private int                     mNavigationDrawerId;
	private int                     mDrawerIconId;
	private int                     mApplicationNameId;
	private int                     mGlobalMenuId;
	private int[]                   mActionMenuItemsToHideWhenDrawerOpen;
	private NavigationDrawerItem[]  mNavigationItems;
	private int                     mDrawerOpenDescription;
	private int                     mDrawerCloseDescription;
	private BaseAdapter             mBaseAdapter;

	public int getGlobalMenuId() {
		return( mGlobalMenuId );
	}

	public void setGlobalMenuId( int globalMenuId ) {
		mGlobalMenuId = globalMenuId;
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

	public int getNavigationDrawerId() {
		return(mNavigationDrawerId);
	}

	public void setNavigationDrawerId( int leftDrawerId ) {
		mNavigationDrawerId = leftDrawerId;
	}

	public int getDrawerIconId() {
		return( mDrawerIconId );
	}

	public void setDrawerIconId( int drawerIconId ) {
		mDrawerIconId = drawerIconId;
	}

	public int getApplicationNameId() {
		return( mApplicationNameId );
	}

	public void setApplicationNameId( int applicationNameId ) {
		mApplicationNameId = applicationNameId;
	}

	public int[] getActionMenuItemsToHideWhenDrawerOpen() {
		return( mActionMenuItemsToHideWhenDrawerOpen );
	}

	public void setActionMenuItemsToHideWhenDrawerOpen( int[] actionMenuItemsToHideWhenDrawerOpen ) {
		mActionMenuItemsToHideWhenDrawerOpen = actionMenuItemsToHideWhenDrawerOpen;
	}

	public NavigationDrawerItem[] getNavigationItems() {
		return( mNavigationItems );
	}

	public void setNavigationItems( NavigationDrawerItem[] navItems ) {
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
