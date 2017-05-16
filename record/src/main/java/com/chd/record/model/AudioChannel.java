package com.chd.record.model;

import android.media.AudioFormat;

/**
 * User: Liumj(liumengjie@365tang.cn)
 * Date: 2017-03-03
 * Time: 14:49
 * describe:
 */
public enum  AudioChannel {

	STEREO,
	MONO;

	public int getChannel(){
		switch (this){
			case MONO:
				return AudioFormat.CHANNEL_IN_MONO;
			default:
				return AudioFormat.CHANNEL_IN_STEREO;
		}
	}
}
