package com.SecretSquirrel.AndroidNoise.ui;

// Secret Squirrel Software - Created by bswanson on 12/26/13.

public interface NavigationDrawerItem {
	public int      getId();
	public String   getLabel();
	public int      getType();
	public boolean  isEnabled();
	public boolean  updateActionBarTitle();
}
