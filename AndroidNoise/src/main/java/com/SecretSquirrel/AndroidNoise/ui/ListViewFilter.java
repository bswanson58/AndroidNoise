package com.SecretSquirrel.AndroidNoise.ui;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

// Created by BSwanson on 2/1/14.

public class ListViewFilter<T> extends Filter {
	public interface FilterClient<T> {
		boolean shouldItemBeDisplayed( T item, String filterText );
		void    updateList( List<T> filteredList );
	}

	private List<T>         mSourceObjects;
	private FilterClient<T> mFilterClient;

	public ListViewFilter( List<T> objects, FilterClient<T> filter ) {
		mSourceObjects = objects;
		mFilterClient = filter;
	}

	@Override
	protected FilterResults performFiltering( CharSequence chars ) {
		String          filterSeq = chars.toString();
		FilterResults   result = new FilterResults();

		if(( filterSeq != null ) &&
		   ( filterSeq.length() > 0 )) {
			ArrayList<T> filter = new ArrayList<T>();

			for( T object : mSourceObjects ) {
				if( mFilterClient.shouldItemBeDisplayed( object, filterSeq )) {
					filter.add( object );
				}
			}

			result.count = filter.size();
			result.values = filter;
		}
		else {
			// add all objects
			synchronized( this ) {
				result.values = mSourceObjects;
				result.count = mSourceObjects.size();
			}
		}

		return( result );
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void publishResults( CharSequence constraint, FilterResults results ) {
		mFilterClient.updateList( (List<T>) results.values );
	}
}

