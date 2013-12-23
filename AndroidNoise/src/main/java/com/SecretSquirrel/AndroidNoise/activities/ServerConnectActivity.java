package com.SecretSquirrel.AndroidNoise.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.ServerInformation;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;

import java.util.ArrayList;

public class ServerConnectActivity extends ActionBarActivity
								   implements ServiceResultReceiver.Receiver {
	private ServiceResultReceiver mServiceResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

	    // from: http://stackoverflow.com/questions/16283079/re-launch-of-activity-on-home-button-but-only-the-first-time?rq=1
	    if (!isTaskRoot()) {
		    // Android launched another instance of the root activity into an existing task
		    //  so just quietly finish and go away, dropping the user back into the activity
		    //  at the top of the stack (ie: the last state of this task)
		    finish();
		    return;
	    }

        setContentView( R.layout.activity_server_connect );

	    if( getApplicationState().getIsConnected()) {
		    launchArtistList();
	    }
	    else {
		    mServiceResultReceiver = new ServiceResultReceiver( new Handler());
		    mServiceResultReceiver.setReceiver( this );

		    getApplicationState().LocateServers( mServiceResultReceiver );
	    }
    }

	private IApplicationState getApplicationState() {
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
}
