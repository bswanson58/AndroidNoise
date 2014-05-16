package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by Bswanson on 5/16/14.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Library;
import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.events.EventLibraryManagementRequest;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseLibrary;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import timber.log.Timber;

public class LibraryEdit extends Fragment {
	private static final String SERVER_INFORMATION = "serverInformation";
	private static final String LIBRARY_INFORMATION  = "libraryInformation";
	private static final String LIBRARY_ACTION = "libraryAction";

	public  static final int    EDIT_LIBRARY    = 1;
	public  static final int    CREATE_LIBRARY  = 2;

	private ServerInformation   mServerInformation;
	private Library             mLibrary;
	private int                 mAction;

	@Inject	EventBus            mEventBus;
	@Inject	INoiseLibrary       mNoiseLibrary;

	@InjectView( R.id.le_library_name )	    EditText    mLibraryName;
	@InjectView( R.id.le_database_name )    EditText    mDatabaseName;
	@InjectView( R.id.le_media_location )   EditText    mMediaLocation;

	public static LibraryEdit newInstance( ServerInformation serverInformation, Library library, int action ) {
		LibraryEdit     fragment = new LibraryEdit();
		Bundle          args = new Bundle();

		args.putParcelable( SERVER_INFORMATION, serverInformation );
		args.putParcelable( LIBRARY_INFORMATION, library );
		args.putInt( LIBRARY_ACTION, action );
		fragment.setArguments( args );

		return( fragment );
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );

		if( savedInstanceState != null ) {
			mServerInformation = savedInstanceState.getParcelable( SERVER_INFORMATION );
			mLibrary = savedInstanceState.getParcelable( LIBRARY_INFORMATION );
			mAction = savedInstanceState.getInt( LIBRARY_ACTION, EDIT_LIBRARY );
		}
		else {
			Bundle  args = getArguments();

			if( args != null ) {
				mServerInformation = args.getParcelable( SERVER_INFORMATION );
				mLibrary = args.getParcelable( LIBRARY_INFORMATION );
				mAction = args.getInt( LIBRARY_ACTION, EDIT_LIBRARY );
			}
		}

		if( mServerInformation == null ) {
			Timber.e( "ServerInformation not set." );
		}

		if( mLibrary == null ) {
			Timber.e( "Library is not set." );
		}
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.config_library_edit, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );

			displayLibrary();
		}

		return( myView );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );

		updateFromUserInterface();

		outState.putParcelable( SERVER_INFORMATION, mServerInformation );
		outState.putParcelable( LIBRARY_INFORMATION, mLibrary );
		outState.putInt( LIBRARY_ACTION, mAction );
	}

	private void displayLibrary() {
		mLibraryName.setText( mLibrary.getLibraryName());
		mDatabaseName.setText( mLibrary.getDatabaseName() );
		mMediaLocation.setText( mLibrary.getMediaLocation());
	}

	private void updateFromUserInterface() {
		if( mLibraryName.getText() != null ) {
			mLibrary.setLibraryName( mLibraryName.getText().toString());
		}
		if( mDatabaseName.getText() != null ) {
			mLibrary.setDatabaseName( mDatabaseName.getText().toString());
		}
		if( mMediaLocation.getText() != null ) {
			mLibrary.setMediaLocation( mMediaLocation.getText().toString());
		}
	}

	@SuppressWarnings( "unused" )
	@OnClick( R.id.le_update )
	public void OnClickUpdate() {
		updateFromUserInterface();

		if((!TextUtils.isEmpty( mLibrary.getLibraryName())) &&
		   (!TextUtils.isEmpty( mLibrary.getDatabaseName())) &&
		   (!TextUtils.isEmpty( mLibrary.getMediaLocation()))) {
			if( mAction == EDIT_LIBRARY ) {
				updateLibrary( mLibrary );
			}
			else {
				createLibrary( mLibrary );
			}
		}
	}

	private void createLibrary( Library library ) {
		AndroidObservable.fromFragment( this, mNoiseLibrary.createLibrary( library ))
				.subscribe( new Action1<Library>() {
					            @Override
					            public void call( Library result ) {
						            mLibrary = result;
						            displayLibrary();

						            Toast.makeText( getActivity(), "Library created!", Toast.LENGTH_LONG ).show();
					            }
				            }, new Action1<Throwable>() {
					            @Override
					            public void call( Throwable throwable ) {
						            Timber.e( throwable, "createLibrary" );
					            }
				            }
				);
	}

	private void updateLibrary(Library library ) {
		AndroidObservable.fromFragment( this, mNoiseLibrary.updateLibrary( library ) )
				.subscribe( new Action1<BaseServerResult>() {
					            @Override
					            public void call( BaseServerResult result ) {
						            if( result.Success ) {
							            Toast.makeText( getActivity(), "Library updated!", Toast.LENGTH_LONG ).show();
						            }
						            else {
							            Toast.makeText( getActivity(), "Could not update library", Toast.LENGTH_LONG ).show();
						            }
					            }
				            }, new Action1<Throwable>() {
					            @Override
					            public void call( Throwable throwable ) {
						            Timber.e( throwable, "updateLibrary" );
					            }
				            }
				);
	}

	@SuppressWarnings( "unused" )
	@OnClick( R.id.le_close )
	public void onClickClose() {
		mEventBus.post( new EventLibraryManagementRequest( mServerInformation ));
	}
}
