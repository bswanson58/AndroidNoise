package com.SecretSquirrel.AndroidNoise.support;

// Secret Squirrel Software - Created by bswanson on 2/7/14.

import android.app.Activity;
import android.app.Service;
import android.support.v4.app.Fragment;
import android.content.Context;

// from: https://gist.github.com/imminent/5222625

public class IocUtility {
	// Block instantiation of the IocUtility class.
	private IocUtility() { }

	/**
	 * <p>An {@link android.app.Application} that wants to inject dependencies from an
	 * {@link dagger.ObjectGraph object graph} must implement {@link ObjectGraphApplication}.</p>
	 */
	public interface ObjectGraphApplication {
		void inject( Object dependent );
	}

	public static void inject( Activity activity ) {
		((ObjectGraphApplication)activity.getApplication()).inject( activity );
	}

	public static void inject( Fragment fragment ) {
		final Activity activity = fragment.getActivity();

		if( activity == null ) {
			throw new IllegalStateException( "Attempting to get Activity before it has been attached to " + fragment.getClass().getName());
		}

		((ObjectGraphApplication) activity.getApplication()).inject( fragment );
	}

	public static void inject( Service service ) {
		((ObjectGraphApplication)service.getApplication()).inject( service );
	}

	public static void inject( Context context, Object object ) {
		((ObjectGraphApplication)context.getApplicationContext()).inject( object );
	}
}
