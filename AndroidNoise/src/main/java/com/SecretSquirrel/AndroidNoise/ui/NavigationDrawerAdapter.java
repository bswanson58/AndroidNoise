package com.SecretSquirrel.AndroidNoise.ui;

// Secret Squirrel Software - Created by bswanson on 12/26/13.

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.SecretSquirrel.AndroidNoise.R;

public class NavigationDrawerAdapter extends ArrayAdapter<NavigationDrawerItem> {

	private LayoutInflater mInflater;

	public NavigationDrawerAdapter( Context context, int textViewResourceId, NavigationDrawerItem[] objects ) {
		super( context, textViewResourceId, objects );
		this.mInflater = LayoutInflater.from( context );
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent) {
		View                    view = null ;
		NavigationDrawerItem    menuItem = this.getItem( position );

		if ( menuItem.getType() == NavigationMenuItem.ITEM_TYPE ) {
			view = getItemView( convertView, parent, menuItem );
		}
		else {
			view = getSectionView( convertView, parent, menuItem );
		}

		return( view );
	}

	public View getItemView( View convertView, ViewGroup parentView, NavigationDrawerItem navDrawerItem ) {

		NavigationMenuItem  menuItem = (NavigationMenuItem) navDrawerItem ;
		NavMenuItemHolder   navMenuItemHolder = null;

		if( convertView == null ) {
			convertView = mInflater.inflate( R.layout.navigation_drawer_item, parentView, false);
			TextView labelView = (TextView) convertView
					.findViewById( R.id.navmenuitem_label );
			ImageView iconView = (ImageView) convertView
					.findViewById( R.id.navmenuitem_icon );

			navMenuItemHolder = new NavMenuItemHolder();
			navMenuItemHolder.labelView = labelView ;
			navMenuItemHolder.iconView = iconView ;

			convertView.setTag(navMenuItemHolder);
		}

		if( navMenuItemHolder == null ) {
			navMenuItemHolder = (NavMenuItemHolder) convertView.getTag();
		}

		navMenuItemHolder.labelView.setText(menuItem.getLabel());
		navMenuItemHolder.iconView.setImageResource(menuItem.getIcon());

		return( convertView );
	}

	public View getSectionView( View convertView, ViewGroup parentView, NavigationDrawerItem navDrawerItem ) {

		NavigationSectionItem   menuSection = (NavigationSectionItem) navDrawerItem ;
		NavMenuSectionHolder    navMenuItemHolder = null;

		if( convertView == null ) {
			convertView = mInflater.inflate( R.layout.navigation_section_item, parentView, false);
			TextView labelView = (TextView) convertView
					.findViewById( R.id.navmenusection_label );

			navMenuItemHolder = new NavMenuSectionHolder();
			navMenuItemHolder.labelView = labelView ;
			convertView.setTag(navMenuItemHolder);
		}

		if( navMenuItemHolder == null ) {
			navMenuItemHolder = (NavMenuSectionHolder) convertView.getTag();
		}

		navMenuItemHolder.labelView.setText(menuSection.getLabel());

		return( convertView );
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType( int position ) {
		return this.getItem(position).getType();
	}

	@Override
	public boolean isEnabled( int position ) {
		return getItem(position).isEnabled();
	}

	private static class NavMenuItemHolder {
		private TextView labelView;
		private ImageView iconView;
	}

	private class NavMenuSectionHolder {
		private TextView labelView;
	}
}