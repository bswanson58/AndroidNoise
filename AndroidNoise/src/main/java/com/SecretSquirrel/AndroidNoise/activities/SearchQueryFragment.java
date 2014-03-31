package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 12/30/13.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.events.EventSearchRequest;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;
import com.SecretSquirrel.AndroidNoise.support.NoiseUtils;
import com.SecretSquirrel.AndroidNoise.views.ButtonEditText;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class SearchQueryFragment extends Fragment {
	private static final String     SEARCH_TEXT_KEY  = "SearchText";

	@Inject	EventBus                mEventBus;

	@InjectView( R.id.search_text_view )    ButtonEditText  mSearchText;

	public static SearchQueryFragment newInstance() {
		return( new SearchQueryFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_search_query, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );

			mSearchText.setDrawableClickListener( new ButtonEditText.DrawableClickListener() {
				@Override
				public void onClick( ButtonEditText.DrawableClickListener.DrawablePosition target ) {
					// Clear the edit box and the search results.
					mSearchText.setText( "" );
					EventBus.getDefault().post( new EventSearchRequest( "" ) );
				}
			} );

			mSearchText.setOnEditorActionListener( new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction( TextView textView, int i, KeyEvent keyEvent ) {
					boolean retValue = false;

					if(( keyEvent != null ) &&
					   ( keyEvent.getAction() == KeyEvent.ACTION_DOWN ) &&
					   ( keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER )) {
						executeSearch();

						retValue = true;
					}

					return (retValue);
				}
			} );

			if( savedInstanceState != null ) {
				mSearchText.setText( savedInstanceState.getString( SEARCH_TEXT_KEY ));
			}
			
			NoiseUtils.displayKeyboard( getActivity(), mSearchText );
		}

		return( myView );
	}

	@SuppressWarnings( "unused" )
	@OnClick( R.id.execute_search_button )
	public void onClick() {
		executeSearch();
	}

	private void executeSearch() {
		if(( mSearchText != null ) &&
		   ( mSearchText.getText() != null )) {
			String  searchText = mSearchText.getText().toString();

			if(!TextUtils.isEmpty( searchText )) {
				mEventBus.post( new EventSearchRequest( searchText ) );

				NoiseUtils.hideKeyboard( getActivity());
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		ButterKnife.reset( this );
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
