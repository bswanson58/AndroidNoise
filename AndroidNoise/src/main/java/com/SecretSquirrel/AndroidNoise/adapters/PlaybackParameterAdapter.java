package com.SecretSquirrel.AndroidNoise.adapters;

// Secret Squirrel Software - Created by Bswanson on 3/14/14.

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;
import com.SecretSquirrel.AndroidNoise.dto.StrategyParameter;

import java.util.List;

public class PlaybackParameterAdapter extends BaseAdapter implements SpinnerAdapter {
	private final LayoutInflater mLayoutInflater;
	private final List<StrategyParameter> mParameterList;

	public PlaybackParameterAdapter( Context context, List<StrategyParameter> parameters ) {
		mParameterList = parameters;

		mLayoutInflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
	}

	@Override
	public int getCount() {
		return( mParameterList.size());
	}

	@Override
	public Object getItem( int i ) {
		return( mParameterList.get( i ));
	}

	@Override
	public long getItemId( int i ) {
		return( i );
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		View        retValue = convertView;

		if( convertView == null ) {
			retValue = mLayoutInflater.inflate( R.layout.simple_spinner_item, parent, false );
		}

		if( retValue != null ) {
			StrategyParameter   parameter = (StrategyParameter)getItem( position );
			TextView textView = (TextView)retValue.findViewById( R.id.si_text );

			if( textView != null ) {
				textView.setText( parameter.getParameterTitle() );
			}
		}

		return( retValue );
	}
}
