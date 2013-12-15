package com.SecretSquirrel.AndroidNoise.activities;

import android.app.IntentService;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Artist;
import com.SecretSquirrel.AndroidNoise.interfaces.IApplicationState;
import com.SecretSquirrel.AndroidNoise.model.NoiseRemoteApplication;
import com.SecretSquirrel.AndroidNoise.services.NoiseRemoteApi;
import com.SecretSquirrel.AndroidNoise.services.ServiceResultReceiver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ArtistListActivity extends ActionBarActivity
								implements ServiceResultReceiver.Receiver {

	private ServiceResultReceiver   mServiceResultReceiver;
	private ListView                mArtistListView;
	private ArrayList<Artist>       mArtistList;
	private ArtistAdapter           mArtistListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView( R.layout.activity_artist_list);

        //if (savedInstanceState == null) {
        //    getSupportFragmentManager().beginTransaction()
        //            .add(R.id.container, new PlaceholderFragment())
        //            .commit();
        //}

	    initialize();
    }

	private void initialize() {
		mArtistList = new ArrayList<Artist>();
		mServiceResultReceiver = new ServiceResultReceiver( new Handler());
		mServiceResultReceiver.setReceiver( this );

		mArtistListAdapter = new ArtistAdapter( this, mArtistList );
		getArtistListView().setAdapter( mArtistListAdapter );

		if( getApplicationState().getIsConnected()) {
			loadArtistList();
		}
		else {
			Intent serverConnectIntent = new Intent( this, ServerConnectActivity.class );

			startActivity( serverConnectIntent );
		}

		getArtistListView().setOnItemClickListener( new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick( AdapterView<?> adapterView, View view, int i, long l ) {
				Artist  artist = mArtistList.get( i );

				if( artist != null ) {
					selectArtist( artist );
				}
			}
		} );
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
			ArrayList<Artist> artistList = resultData.getParcelableArrayList( NoiseRemoteApi.ArtistList );
			mArtistList.clear();
			mArtistList.addAll( artistList );

			Collections.sort( mArtistList, new Comparator<Artist>() {
				public int compare( Artist artist1, Artist artist2 ) {
					return( artist1.Name.compareToIgnoreCase( artist2.Name ));
				}
			} );

			mArtistListAdapter.notifyDataSetChanged();
		}
	}

	private ListView getArtistListView() {
		if( mArtistListView == null ) {
			mArtistListView = (ListView)findViewById( R.id.ArtistListView );
		}

		return( mArtistListView );
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

	private class ArtistAdapter extends ArrayAdapter<Artist> {
		private Context             mContext;
		private LayoutInflater      mLayoutInflater;
		private ArrayList<Artist>   mArtistList;

		private class ViewHolder {
			public TextView     NameTextView;
			public TextView     AlbumCountTextView;
			public TextView     GenreTextView;
		}

		public ArtistAdapter( Context context, ArrayList<Artist> artistList ) {
			super( context, R.layout.artist_list_item, artistList );
			mContext = context;
			mArtistList = artistList;

			mLayoutInflater = (LayoutInflater)mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View        retValue = convertView;
			ViewHolder  views = null;

			if( convertView == null ) {
				retValue = mLayoutInflater.inflate( R.layout.artist_list_item, parent, false );

				views = new ViewHolder();
				views.NameTextView = (TextView)retValue.findViewById( R.id.artist_list_item_name );
				views.AlbumCountTextView = (TextView)retValue.findViewById( R.id.artist_list_item_albumCount );
				views.GenreTextView = (TextView)retValue.findViewById( R.id.artist_list_item_genre );

				retValue.setTag( views );
			}
			else {
				views = (ViewHolder)retValue.getTag();
			}

			if(( views != null ) &&
			   ( position < mArtistList.size())) {
				Artist      artist = mArtistList.get( position );

				views.NameTextView.setText( artist.Name );
				views.AlbumCountTextView.setText( "Albums: " + artist.AlbumCount );
				views.GenreTextView.setText( artist.Genre );
			}

			return( retValue );
		}
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
            View rootView = inflater.inflate(R.layout.fragment_artist_list, container, false);
            return rootView;
        }
    }

}
