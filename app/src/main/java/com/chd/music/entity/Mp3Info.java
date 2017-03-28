package com.chd.music.entity;

import com.chd.proto.FileInfo0;

/**
 * Created by lxp1 on 2015/11/20.
 */
public class Mp3Info extends FileInfo0 {
    private  long musicid;
    /*private  String title;
    private  String artist;
    private  String  album;
    private  long albumId;
    private  long duration;
    private  long size;
    private  String url;*/

    public Mp3Info()
    {}

    public long getMusicid() {
        return musicid;
    }

    /*public void setId(long id) {
        this.id = id;
    }*/

}