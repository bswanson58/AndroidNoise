package com.SecretSquirrel.AndroidNoise.ui;

// Secret Squirrel Software - Created by bswanson on 2/17/14.

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.Album;
import com.SecretSquirrel.AndroidNoise.dto.Track;
import com.SecretSquirrel.AndroidNoise.interfaces.INotificationManager;

import javax.inject.Inject;

public class NotificationManager implements INotificationManager {
	private final Context   mContext;
	private View            mToastView;
	private String          mTrackQueuedFormat;
	private String          mTrackListFormat;
	private String          mAlbumQueuedFormat;
	private String          mFailedTrackQueuedFormat;
	private String          mFailedTrackListFormat;
	private String          mFailedAlbumQueuedFormat;

	@Inject
	public NotificationManager( Context context ) {
		mContext = context;

		mTrackQueuedFormat = mContext.getString( R.string.track_queued_toast_format );
		mTrackListFormat = mContext.getString( R.string.track_list_queued_toast_format );

		mAlbumQueuedFormat = mContext.getString( R.string.album_queued_toast_format );
		mFailedTrackQueuedFormat = mContext.getString( R.string.track_queued_fail_toast_format );
		mFailedTrackListFormat =mContext.getString( R.string.track_list_queued_fail_toast_format );
		mFailedAlbumQueuedFormat = mContext.getString( R.string.album_queued_fail_toast_format );
	}

	@Override
	public void NotifyListQueued( int count ) {
		makeToastWithText( String.format( mTrackListFormat, count )).show();
	}

	@Override
	public void NotifyListQueued( String errorMessage ) {
		makeToastWithText( String.format( mFailedTrackListFormat, errorMessage )).show();
	}

	@Override
	public void NotifyItemQueued( String itemName ) {
		makeToastWithText( String.format( mTrackQueuedFormat, itemName )).show();
	}

	@Override
	public void NotifyItemQueued( String itemName, String errorMessage ) {
		makeToastWithText( String.format( mFailedTrackQueuedFormat, itemName, errorMessage )).show();
	}

	@Override
	public void NotifyItemQueued( Track track ) {
		makeToastWithText( String.format( mTrackQueuedFormat, track.getTrackName())).show();
	}

	@Override
	public void NotifyItemQueued( Track track, String errorMessage ) {
		makeToastWithText( String.format( mFailedTrackQueuedFormat, track.getTrackName(), errorMessage )).show();
	}

	@Override
	public void NotifyItemQueued( Album album ) {
		makeToastWithText( String.format( mAlbumQueuedFormat, album.getName())).show();
	}

	@Override
	public void NotifyItemQueued( Album album, String errorMessage ) {
		makeToastWithText( String.format( mFailedAlbumQueuedFormat, album.getName(), errorMessage )).show();
	}

	private Toast makeToastWithText( String text ) {
		Toast       retValue = makeToast();
		TextView    textView = (TextView)mToastView.findViewById( R.id.toast_text );

		textView.setText( text );

		return( retValue );
	}

	private Toast makeToast() {
		Toast   toast = new Toast( mContext );

		if( mToastView == null ) {
			LayoutInflater  inflater = LayoutInflater.from( mContext );

			mToastView = inflater.inflate( R.layout.toast_queue_request, null );
		}

		toast.setView( mToastView );
		toast.setDuration( Toast.LENGTH_LONG );
		toast.setGravity( Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 120 );

		return( toast );
	}
}
