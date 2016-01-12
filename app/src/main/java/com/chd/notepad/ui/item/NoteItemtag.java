package com.chd.notepad.ui.item;

/**
 * Created by lxp1 on 2015/10/28.
 */
public class NoteItemtag
{
    public String content;
    public long time;
    public String title;
    protected Integer hashcode;
    public Integer syncstate;
    public int  id;

    public NoteItemtag()
    {
        hashcode=id;
    }
    @Override
    public boolean equals(Object o) {
        return this.id>((NoteItemtag)o).id;
    }
}