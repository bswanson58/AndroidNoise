package com.SecretSquirrel.AndroidNoise;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.dto.ServerVersion;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteClient;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;

public class MainActivity extends ActionBarActivity
						  implements ServiceResultReceiver.Receiver {
	private ServiceResultReceiver   mServiceResultReceiver;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		if( savedInstanceState == null ) {
			getSupportFragmentManager().beginTransaction().add( R.id.container, new PlaceholderFragment() ).commit();
		}

		mServiceResultReceiver = new ServiceResultReceiver( new Handler());
		mServiceResultReceiver.setReceiver( this );

		NoiseRemoteClient   client = new NoiseRemoteClient( this, mServiceResultReceiver, "http://10.1.1.139:88/Noise" );
		client.getServerVersion();
	}


	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.main, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch( item.getItemId() ) {
			case R.id.action_settings:
				return true;
		}
		return super.onOptionsItemSelected( item );
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			ServerVersion version = resultData.getParcelable( NoiseRemoteApi.RemoteResultVersion );

			if(( version != null ) &&
			   ( version.Major > 1 )) {

			}
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
			return inflater.inflate( R.layout.fragment_main, container, false );
		}
	}

}
