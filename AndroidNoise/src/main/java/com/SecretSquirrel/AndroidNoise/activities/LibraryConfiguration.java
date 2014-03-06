package com.SecretSquirrel.AndroidNoise.activities;

// Created by BSwanson on 3/6/14.

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Library;
import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.events.EventLibraryManagementRequest;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class LibraryConfiguration extends Fragment {
	private static final String SERVER_INFORMATION  = "sererInformation";

	private ServerInformation   mServer;
	private ArrayList<Library>  mLibraries;
	private LibraryAdapter      mLibraryAdapter;

	@Inject	EventBus            mEventBus;

	@InjectView( R.id.lm_library_selector )	Spinner mLibrarySelector;

	public static LibraryConfiguration newInstance( ServerInformation serverInformation ) {
		LibraryConfiguration    fragment = new LibraryConfiguration();
		Bundle                  args = new Bundle();

		args.putParcelable( SERVER_INFORMATION, serverInformation );
		fragment.setArguments( args );

		return( fragment );
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		if( savedInstanceState != null ) {
			mServer = savedInstanceState.getParcelable( SERVER_INFORMATION );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				mServer = args.getParcelable( SERVER_INFORMATION );
			}
		}

		if( mServer == null ) {
			Timber.e( "ServerInformation was not set." );
		}

		if( mLibraries == null ) {
			mLibraries = new ArrayList<Library>();

			mLibraries.add( new Library( "Tiny Noise"));
			mLibraries.add( new Library( "Ranch Noise"));
			mLibraries.add( new Library( "Default Noise"));
		}

		mLibraryAdapter = new LibraryAdapter( getActivity(), mLibraries );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.config_library_management, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );

			mLibrarySelector.setAdapter( mLibraryAdapter );
		}

		return( myView );
	}

	@SuppressWarnings( "unused" )
	@OnClick( R.id.lm_close )
	public void onClickClose() {
		mEventBus.post( new EventLibraryManagementRequest());
	}

	protected class LibraryAdapter extends BaseAdapter implements SpinnerAdapter {
		private final Context           mContext;
		private final LayoutInflater    mLayoutInflater;
		private final List<Library>     mLibraryList;

		protected class ViewHolder {
			public ViewHolder( View view ) {
				ButterKnife.inject( this, view );
			}

			@InjectView( R.id.sli_library_name )    TextView    LibraryName;
		}

		public LibraryAdapter( Context context, List<Library> libraries ) {
			mContext = context;
			mLibraryList = libraries;

			mLayoutInflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		}

		@Override
		public int getCount() {
			return( mLibraryList.size());
		}

		@Override
		public Object getItem( int i ) {
			return( mLibraryList.get( i ));
		}

		@Override
		public long getItemId( int i ) {
			return( i );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View        retValue = convertView;
			ViewHolder  views = null;

			if( convertView == null ) {
				retValue = mLayoutInflater.inflate( R.layout.spinner_library_item, parent, false );

				if( retValue != null ) {
					views = new ViewHolder( retValue );

					retValue.setTag( views );
				}
			}
			else {
				views = (ViewHolder)retValue.getTag();
			}

			if(( views != null ) &&
			   ( position < mLibraryList.size())) {
				Library library = (Library)getItem( position );

				if( library != null ) {
					views.LibraryName.setText( library.Name );
				}
			}

			return( retValue );
		}
	}
}
