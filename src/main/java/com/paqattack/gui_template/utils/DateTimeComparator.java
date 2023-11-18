package com.paqattack.gui_template.utils;

import com.paqattack.gui_template.data.ListEntry;

import java.util.Comparator;

public class DateTimeComparator implements Comparator<ListEntry> {
    @Override
    public int compare(ListEntry o1, ListEntry o2) {
        return o1.getTime().compareTo(o2.getTime());
    }
}

