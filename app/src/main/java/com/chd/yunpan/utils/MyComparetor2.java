package com.chd.yunpan.utils;

import java.util.Comparator;

import com.chd.yunpan.parse.entity.PhotoFileEntity;


public class MyComparetor2 implements Comparator {

	@Override
	public int compare(Object lhs, Object rhs) {
		PhotoFileEntity p1=(PhotoFileEntity) lhs;
		PhotoFileEntity p2=(PhotoFileEntity) rhs;
		return (TimeUtils.getFileDate(p2.getFile()).compareTo(TimeUtils.getFileDate(p1.getFile())));
	}


	

}
