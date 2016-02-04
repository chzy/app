package com.chd.notepad.ui.item;

import java.util.ArrayList;

/**
 * @description
 * @FileName: com.chd.notepad.ui.item.NoteItem
 * @author: liumj
 * @date:2016-02-04 20:04
 * OS:Mac 10.10
 * Developer Kits:AndroidStudio 1.5
 */

public class NoteItem {
    private String title;
    private String content;
    private ArrayList<String> picList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getPicList() {
        return picList;
    }

    public void setPicList(ArrayList<String> picList) {
        this.picList = picList;
    }
}
