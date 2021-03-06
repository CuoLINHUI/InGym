package com.stefan.ingym.ui.fragment.mine.memorandum.util;


import com.stefan.ingym.ui.fragment.mine.memorandum.model.Note;

import java.util.Comparator;

/**
 * 比较工具类
 * 排序
 */
public class ComparatorUtil implements Comparator<Note> {

    @Override
    public int compare(Note o1, Note o2) {
        return  o1.compareTo(o2);
    }

}
