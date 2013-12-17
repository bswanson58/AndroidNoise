package com.SecretSquirrel.AndroidNoise.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentManager;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;

import java.util.ArrayList;

public class ArtistListActivity extends ActionBarActivity
								implements ServiceResultReceiver.Receiver {

	private ServiceResultReceiver   mServiceResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView( R.layout.activity_artist_list);

        //if (savedInstanceState == null) {
        //    getSupportFragmentManager().beginTransaction()
        //            .add(R.id.container, new PlaceholderFragment())
        //            .commit();
        //}

	    mServiceResultReceiver = new ServiceResultReceiver( new Handler());
	    mServiceResultReceiver.setReceiver( this );

	    if( getApplicationState().getIsConnected()) {
		    loadArtistList();
	    }
	    else {
		    Intent serverConnectIntent = new Intent( this, ServerConnectActivity.class );

		    startActivity( serverConnectIntent );
	    }
    }

	private void selectArtist( Artist artist ) {
		Intent  launchIntent = new Intent( this, ArtistActivity.class );

		startActivity( launchIntent );
	}

	private IApplicationState getApplicationState() {
		NoiseRemoteApplication application = (NoiseRemoteApplication)getApplication();

		return( application.getApplicationState());
	}

	private void loadArtistList() {
		getApplicationState().getDataClient().GetArtistList( mServiceResultReceiver );
	}

	@Override
	public void onReceiveResult( int resultCode, Bundle resultData ) {
		if( resultCode == NoiseRemoteApi.RemoteResultSuccess ) {
			FragmentManager     fm = getSupportFragmentManager();
			ArtistListFragment  artistListFragment = (ArtistListFragment)fm.findFragmentById( R.id.fragment_artist_list );
			ArrayList<Artist>   artistList = resultData.getParcelableArrayList( NoiseRemoteApi.ArtistList );

			artistListFragment.setArtistList( artistList );
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.artist_list, menu);
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
