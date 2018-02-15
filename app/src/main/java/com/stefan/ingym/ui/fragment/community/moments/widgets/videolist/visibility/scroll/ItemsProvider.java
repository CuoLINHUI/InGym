package com.stefan.ingym.ui.fragment.community.moments.widgets.videolist.visibility.scroll;


import com.stefan.ingym.ui.fragment.community.moments.widgets.videolist.visibility.items.ListItem;

public interface ItemsProvider {

    ListItem getListItem(int position);

    int listItemSize();

}
