package com.SecretSquirrel.AndroidNoise.ui;

// Secret Squirrel Software - Created by bswanson on 12/26/13.

import android.content.Context;

public class NavigationMenuItem implements NavigationDrawerItem {
	public  static final int ITEM_TYPE = 1 ;

	private int         mId;
	private String      mLabel;
	private int         mIcon;
	private boolean     mUpdateActionBarTitle;

	private NavigationMenuItem() { }

	public static NavigationMenuItem create( int id, String label, String icon, boolean updateActionBarTitle, Context context ) {
		NavigationMenuItem retValue = new NavigationMenuItem();

		retValue.setId( id );
		retValue.setLabel( label );
		retValue.setIcon( context.getResources().getIdentifier( icon, "drawable", context.getPackageName() ) );
		retValue.setUpdateActionBarTitle( updateActionBarTitle );

		return( retValue );
	}

	@Override
	public int getType() {
		return( ITEM_TYPE );
	}

	public int getId() {
		return( mId );
	}

	public void setId( int id ) {
		mId = id;
	}

	public String getLabel() {
		return( mLabel );
	}

	public void setLabel( String label ) {
		mLabel = label;
	}

	public int getIcon() {
		return( mIcon );
	}

	public void setIcon( int icon ) {
		mIcon = icon;
	}

	@Override
	public boolean isEnabled() {
		return( true );
	}

	@Override
	public boolean updateActionBarTitle() {
		return( mUpdateActionBarTitle );
	}

	public void setUpdateActionBarTitle(boolean updateActionBarTitle) {
		mUpdateActionBarTitle = updateActionBarTitle;
	}}
