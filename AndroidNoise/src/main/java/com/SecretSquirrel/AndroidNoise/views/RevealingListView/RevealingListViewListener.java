package com.secretSquirrel.sandbox.RevealingListView;

// Secret Squirrel Software - Created by BSwanson on 3/28/14.

public interface RevealingListViewListener {
	void    onItemClicked( int position );
	void    onItemLongClicked( int position );

	void    onRevealOpened( int position, int action );
	void    onRevealClosed( int position );
}
