package com.SecretSquirrel.AndroidNoise.ui;

// Secret Squirrel Software - Created by bswanson on 12/26/13.

public class NavigationSectionItem implements NavigationDrawerItem {
	public static final int SECTION_TYPE = 0;

	private int     mId;
	private String  mLabel;

	private NavigationSectionItem() { }

	public static NavigationSectionItem create( int id, String label ) {
		NavigationSectionItem section = new NavigationSectionItem();

		section.setLabel( label );

		return( section );
	}

	@Override
	public int getType() {
		return( SECTION_TYPE );
	}

	public String getLabel() {
		return( mLabel );
	}

	public void setLabel( String label ) {
		mLabel = label;
	}

	@Override
	public boolean isEnabled() {
		return( false );
	}

	public int getId() {
		return( mId );
	}

	public void setId( int id ) {
		mId = id;
	}

	@Override
	public boolean updateActionBarTitle() {
		return( false );
	}
}
