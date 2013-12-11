package com.SecretSquirrel.AndroidNoise.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.model.ApplicationState;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;

import java.util.ArrayList;

public class ServerConnectActivity extends ActionBarActivity
								   implements ServiceResultReceiver.Receiver {
	private ServiceResultReceiver mServiceResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_server_connect);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

	    if( getApplicationState().getIsConnected()) {
		    launchArtistList();
	    }
	    else {
		    mServiceResultReceiver = new ServiceResultReceiver( new Handler());
		    mServiceResultReceiver.setReceiver( this );

		    getApplicationState().LocateServers( mServiceResultReceiver );
	    }
    }

	private ApplicationState getApplicationState() {
		NoiseRemoteApplication  application = (NoiseRemoteApplication)getApplication();

		return( application.getApplicationState());
	}

	private void launchArtistList() {
		Intent  artistListIntent = new Intent( this, ArtistListActivity.class );

		startActivity( artistListIntent );
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			ArrayList<ServerInformation>    serverList = resultData.getParcelableArrayList( NoiseRemoteApi.LocateServicesList );

			if(( serverList != null ) &&
			   ( serverList.size() == 1 )) {
				getApplicationState().SelectServer( serverList.get( 0 ));

				launchArtistList();
			}
			else {
				// Launch server selector.
			}
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.server_connect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_server_connect, container, false);
            return rootView;
        }
    }

}
