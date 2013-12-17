package com.SecretSquirrel.AndroidNoise.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.interfaces.IViewListener;
import com.SecretSquirrel.AndroidNoise.interfaces.OnItemSelectedListener;
import com.SecretSquirrel.AndroidNoise.interfaces.OnQueueRequestListener;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;

public class ArtistActivity extends ActionBarActivity
							implements IViewListener {
	private OnItemSelectedListener  mOnItemSelectedListener;
	private OnQueueRequestListener  mOnQueueRequestListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

	    mOnItemSelectedListener = new DefaultItemSelectedListener();
	    mOnQueueRequestListener = new DefaultQueueRequestListener( getApplicationState());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.artist, menu);
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

	private IApplicationState getApplicationState() {
		NoiseRemoteApplication application = (NoiseRemoteApplication)getApplication();

		return( application.getApplicationState());
	}

	@Override
	public OnItemSelectedListener getItemSelectedListener() {
		return( mOnItemSelectedListener );
	}

	@Override
	public OnQueueRequestListener getQueueRequestListener() {
		return( mOnQueueRequestListener );
	}
}
