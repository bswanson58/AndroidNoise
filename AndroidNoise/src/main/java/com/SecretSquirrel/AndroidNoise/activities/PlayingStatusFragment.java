package com.SecretSquirrel.AndroidNoise.activities;

// Secret Squirrel Software - Created by bswanson on 2/26/14.

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.PlayQueueTrack;
import com.SecretSquirrel.AndroidNoise.events.EventQueueUpdated;
import com.SecretSquirrel.AndroidNoise.interfaces.IQueueStatus;
import com.SecretSquirrel.AndroidNoise.support.IocUtility;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class PlayingStatusFragment extends Fragment {
	@Inject	EventBus        mEventBus;
	@Inject	IQueueStatus    mQueueStatus;

	@InjectView( R.id.ps_status )	TextView    mStatusView;

	public static PlayingStatusFragment newInstance() {
		return( new PlayingStatusFragment());
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		IocUtility.inject( this );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View    myView = inflater.inflate( R.layout.fragment_playing_status, container, false );

		if( myView != null ) {
			ButterKnife.inject( this, myView );
		}

		return( myView );
	}

	@Override
	public void onResume() {
		super.onResume();

		updateDisplay();
		mEventBus.register( this );
	}

	@Override
	public void onPause() {
		super.onPause();

		mEventBus.unregister( this );
	}

	@SuppressWarnings( "unused" )
	public void onEvent( EventQueueUpdated args ) {
		updateDisplay();
	}

	private void updateDisplay() {
		PlayQueueTrack  currentTrack = mQueueStatus.getCurrentlyPlayingTrack();

		if( currentTrack != null ) {
			mStatusView.setText( String.format( "Now Playing: %s (%s/%s)",
					currentTrack.getTrackName(), currentTrack.getArtistName(), currentTrack.getAlbumName()));
		}
		else {
			mStatusView.setText( "Play Something!" );
		}
	}
}
