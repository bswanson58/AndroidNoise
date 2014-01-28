package com.SecretSquirrel.AndroidNoise.support;

// Secret Squirrel Software - Created by bswanson on 1/28/14.

import java.util.Comparator;

public class ChainedComparator<T> implements Comparator<T> {
	private Comparator<T>   mFirstComparator;
	private Comparator<T>   mSecondComparator;

	public ChainedComparator( Comparator<T> firstComparator, Comparator<T> secondComparator ) {
		mFirstComparator = firstComparator;
		mSecondComparator = secondComparator;
	}
	public int compare( T o1, T o2 ) {
		int retValue = mFirstComparator.compare( o1, o2 );

		if( retValue == 0 ) {
			retValue = mSecondComparator.compare( o1, o2 );
		}

		return( retValue );
	}
}
