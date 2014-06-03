package com.SecretSquirrel.AndroidNoise.activities;

// Created by BSwanson on 3/6/14.

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.AudioDevice;
import com.SecretSquirrel.AndroidNoise.dto.Library;
import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.events.EventLibraryEditRequest;
import com.SecretSquirrel.AndroidNoise.events.EventLibraryManagementRequest;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseLibrary;
import com.SecretSquirrel.AndroidNoise.interfaces.INoiseServer;
import com.SecretSquirrel.AndroidNoise.services.rto.BaseServerResult;
import com.SecretSquirrel.AndroidNoise.support.Constants;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import timber.log.Timber;

public class LibraryConfiguration extends Fragment {
	private static final String SERVER_INFORMATION  = "serverInformation";

	private ServerInformation   mServer;
	private ArrayList<Library>  mLibraries;
	private LibraryAdapter      mLibraryAdapter;
	private AudioDeviceAdapter  mDeviceAdapter;
	private long                mSelectedLibrary;

	@Inject	EventBus            mEventBus;
	@Inject	INoiseServer        mNoiseServer;
	@Inject	INoiseLibrary       mNoiseLibrary;
	@Inject	IApplicationState   mApplicationState;

	@InjectView( R.id.lm_library_selector )	Spinner mLibrarySelector;
	@InjectView( R.id.lm_device_selector )  Spinner mDeviceSelector;

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

		if( mServer != null ) {
			mSelectedLibrary = mServer.getLibraryId();
			mDeviceAdapter = new AudioDeviceAdapter( getActivity(), mServer.getAudioDevices());
		}
		else {
			Timber.e( "ServerInformation was not set." );
		}

		if( mLibraries == null ) {
			mLibraries = new ArrayList<Library>();
		}

		mLibraryAdapter = new LibraryAdapter( getActivity(), mLibraries );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.config_library_management, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );

			mLibrarySelector.setAdapter( mLibraryAdapter );
			mLibrarySelector.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected( AdapterView<?> adapterView, View view, int i, long l ) {
					selectLibrary( mLibraries.get( i ) );
				}

				@Override
				public void onNothingSelected( AdapterView<?> adapterView ) { }
			} );

			if( mDeviceAdapter != null ) {
				mDeviceSelector.setAdapter( mDeviceAdapter );
				mDeviceSelector.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected( AdapterView<?> adapterView, View view, int i, long l ) {
						selectAudioDevice( mServer.getAudioDevices().get( i ));
					}

					@Override
					public void onNothingSelected( AdapterView<?> adapterView ) { }
				} );

				setSelectedDevice( mServer.getCurrentAudioDevice());
			}
		}

		return( myView );
	}

	@SuppressWarnings( "unused" )
	@OnClick( R.id.lm_sync_library )
	public void onClickSync() {
		syncLibrary();
	}

	@SuppressWarnings( "unused" )
	@OnClick( R.id.lm_edit_library )
	public void onClickEditLibrary() {
		if( mSelectedLibrary != Constants.NULL_ID ) {
			Library library = getLibrary( mSelectedLibrary );

			if( library != null ) {
				mEventBus.post( new EventLibraryEditRequest( mServer, library, LibraryEdit.EDIT_LIBRARY ));
			}
		}
	}

	@SuppressWarnings( "unused" )
	@OnClick( R.id.lm_create_library )
	public void onClickCreateLibrary() {
		mEventBus.post( new EventLibraryEditRequest( mServer, new Library(), LibraryEdit.CREATE_LIBRARY ));
	}

	private Library getLibrary( long libraryId ) {
		Library retValue = null;

		for( Library library : mLibraries ) {
			if( library.getLibraryId() == libraryId ) {
				retValue = library;

				break;
			}
		}

		return( retValue );
	}

	private void setSelectedLibrary( long libraryId ) {
		int position = -1;

		for( Library library : mLibraries ) {
			position++;

			if( library.getLibraryId() == libraryId ) {
				mSelectedLibrary = library.getLibraryId();

				break;
			}
		}

		mLibrarySelector.setSelection( position );
	}

	private void setSelectedDevice( int deviceId ) {
		int position = -1;

		for( AudioDevice d : mServer.getAudioDevices()) {
			position++;

			if( d.getDeviceId() == deviceId ) {
				break;
			}
		}

		mDeviceSelector.setSelection( position );
	}

	@Override
	public void onResume() {
		super.onResume();

		if( mServer != null ) {
			mApplicationState.setCurrentServer( mServer );

			if( mLibraries.size() == 0 ) {
				getLibraries();
			}
		}
	}

	@SuppressWarnings( "unused" )
	@OnClick( R.id.lm_close )
	public void onClickClose() {
		mEventBus.post( new EventLibraryManagementRequest() );
	}

	private void getLibraries() {
		AndroidObservable.fromFragment( this, mNoiseLibrary.getLibraries())
				.subscribe( new Action1<Library[]>() {
					            @Override
					            public void call( Library[] libraries ) {
						            updateLibraries( libraries );
					            }
				            }, new Action1<Throwable>() {
					            @Override
					            public void call( Throwable throwable ) {
						            Timber.e( throwable, "getLibraries" );
					            }
				            }
				);
	}

	private void selectLibrary( final Library library ) {
		if( library.getLibraryId() != mSelectedLibrary ) {
			AndroidObservable.fromFragment( this, mNoiseLibrary.selectLibrary( library ))
					.subscribe( new Action1<BaseServerResult>() {
						            @Override
						            public void call( BaseServerResult result ) {
							            if( result.Success ) {
								            setSelectedLibrary( library.getLibraryId());
								            mServer.updateLibrary( library.getLibraryId(), library.getLibraryName());
							            }
							            else {
								            Toast.makeText( getActivity(), "Could not select library", Toast.LENGTH_LONG ).show();
							            }
						            }
					            }, new Action1<Throwable>() {
						            @Override
						            public void call( Throwable throwable ) {
							            Timber.e( throwable, "selectLibrary" );
						            }
					            }
					);
		}
	}

	private void selectAudioDevice( final AudioDevice device ) {
		if(( device != null ) &&
		   ( device.getDeviceId() != mServer.getCurrentAudioDevice())) {
			AndroidObservable.fromFragment( this, mNoiseServer.setAudioDevice( device.getDeviceId()))
					.subscribe( new Action1<BaseServerResult>() {
						            @Override
						            public void call( BaseServerResult result ) {
							            if( result.Success ) {
								            setSelectedDevice( device.getDeviceId() );
								            mServer.updateAudioDevice( device.getDeviceId());

								            Toast.makeText( getActivity(), "Audio device has been changed.", Toast.LENGTH_LONG ).show();
							            }
							            else {
								            Toast.makeText( getActivity(), "Could not select audio device", Toast.LENGTH_LONG ).show();
							            }
						            }
					            }, new Action1<Throwable>() {
						            @Override
						            public void call( Throwable throwable ) {
							            Timber.e( throwable, "setAudioDevice" );
						            }
					            }
					);
		}
	}

	private void syncLibrary() {
		AndroidObservable.fromFragment( this, mNoiseLibrary.syncLibrary())
				.subscribe( new Action1<BaseServerResult>() {
					            @Override
					            public void call( BaseServerResult result ) {
						            if( result.Success ) {
							            Toast.makeText( getActivity(), "Library update has been started.", Toast.LENGTH_LONG ).show();
						            }
						            else {
							            Toast.makeText( getActivity(), "Could not start library update.", Toast.LENGTH_LONG ).show();
						            }
					            }
				            }, new Action1<Throwable>() {
					            @Override
					            public void call( Throwable throwable ) {
						            Timber.e( throwable, "syncLibrary" );
					            }
				            }
				);
	}

	private void updateLibraries( Library[] libraries ) {
		mLibraries.clear();
		mLibraries.addAll( Arrays.asList( libraries ));

		mLibraryAdapter.notifyDataSetChanged();
		setSelectedLibrary( mSelectedLibrary );
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
					views.LibraryName.setText( library.getLibraryName());
				}
			}

			return( retValue );
		}
	}

	protected class AudioDeviceAdapter extends BaseAdapter implements SpinnerAdapter {
		private final Context           mContext;
		private final LayoutInflater    mLayoutInflater;
		private final List<AudioDevice> mDeviceList;

		protected class ViewHolder {
			public ViewHolder( View view ) {
				ButterKnife.inject( this, view );
			}

			@InjectView( R.id.si_text )    TextView    DeviceName;
		}

		public AudioDeviceAdapter( Context context, List<AudioDevice> deviceList ) {
			mContext = context;
			mDeviceList = deviceList;

			mLayoutInflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		}

		@Override
		public int getCount() {
			return( mDeviceList.size());
		}

		@Override
		public Object getItem( int i ) {
			return( mDeviceList.get( i ));
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
				retValue = mLayoutInflater.inflate( R.layout.simple_spinner_item, parent, false );

				if( retValue != null ) {
					views = new ViewHolder( retValue );

					retValue.setTag( views );
				}
			}
			else {
				views = (ViewHolder)retValue.getTag();
			}

			if(( views != null ) &&
			   ( position < mDeviceList.size())) {
				AudioDevice device = (AudioDevice)getItem( position );

				if( device != null ) {
					views.DeviceName.setText( device.getDeviceName() );
				}
			}

			return( retValue );
		}
	}
}
