package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/30/13.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.events.EventSearchRequest;

import de.greenrobot.event.EventBus;

public class SearchQueryFragment extends Fragment {
	private static final String     SEARCH_TEXT_KEY  = "SearchText";

	private EditText                mSearchText;

	public static SearchQueryFragment newInstance() {
		return( new SearchQueryFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_search_query, container, false );

		if( myView != null ) {
			mSearchText = (EditText) myView.findViewById( R.id.search_text_view );
			Button executeSearch = (Button) myView.findViewById( R.id.execute_search_button );

			executeSearch.setOnClickListener( new View.OnClickListener() {
				@Override
				public void onClick( View view ) {
					executeSearch();
				}
			} );

			if( savedInstanceState != null ) {
				mSearchText.setText( savedInstanceState.getString( SEARCH_TEXT_KEY, "" ));
			}
		}

		return( myView );
	}

	private void executeSearch() {
		if(( mSearchText != null ) &&
		   ( mSearchText.getText() != null )) {
			String  searchText = mSearchText.getText().toString();

			if(!TextUtils.isEmpty( searchText )) {
				EventBus.getDefault().post( new EventSearchRequest( searchText ));
			}
		}
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		if(( mSearchText != null ) &&
		   ( mSearchText.getText() != null )) {
			outState.putString( SEARCH_TEXT_KEY, mSearchText.getText().toString());
		}
	}
}
