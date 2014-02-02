package com.SecretSquirrel.AndroidNoise.ui;

// Created by BSwanson on 2/2/14.

import android.content.Context;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class FilteredArrayAdapter<T> extends ArrayAdapter<T>
											  implements Filterable, ListViewFilter.FilterClient<T> {
	public interface FilteredListWatcher {
		void    onListChanged( int itemCount );
	}

	private ArrayList<T>        mOriginalList;
	private ListViewFilter<T>   mFilter;
	private String              mFilterText;
	private FilteredListWatcher mListWatcher;

	public FilteredArrayAdapter( Context context, int resource, ArrayList<T> artistList ) {
		super( context, resource );
		mOriginalList = artistList;

		mFilterText = "";

		updateList( mOriginalList );
	}

	@Override
	public void updateList( List<T> list ) {
		setNotifyOnChange( false );

		clear();
		for( T item : list ) {
			add( item );
		}

		onListUpdated();

		if( mListWatcher != null ) {
			mListWatcher.onListChanged( getCount());
		}

		super.notifyDataSetChanged();
	}

	protected void onListUpdated() { }

	public void setListWatcher( FilteredListWatcher watcher ) {
		mListWatcher = watcher;
	}

	public void setFilterText( CharSequence filterText ) {
		if(!TextUtils.equals( mFilterText, filterText )) {
			mFilterText = filterText.toString();

			getFilter().filter( mFilterText );
		}
	}

	@Override
	public Filter getFilter() {
		if( mFilter == null ) {
			mFilter = new ListViewFilter<T>( mOriginalList, this );
		}

		return( mFilter );
	}

	@Override
	public void notifyDataSetChanged() {
		if( mFilter == null ) {
			updateList( mOriginalList );
		}
		else {
			mFilter.filter( mFilterText );
		}
	}

	public T getItemAtPosition( int position ) {
		T  retValue = null;

		if( position <= getCount()) {
			retValue = getItem( position );
		}

		return( retValue );
	}
}
