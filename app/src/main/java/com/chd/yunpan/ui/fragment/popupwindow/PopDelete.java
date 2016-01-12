package com.chd.yunpan.ui.fragment.popupwindow;

import android.widget.Toast;

import com.chd.proto.FileInfo0;
import com.chd.yunpan.ui.fragment.FileListFragment;

import java.util.ArrayList;
import java.util.List;

public class PopDelete {
	public PopDelete(FileListFragment fragment) {
		List<FileInfo0> delete = new ArrayList<FileInfo0>();
		for (int i = 0; i < fragment.getFilesListEntity().getCount(); i++) {
			if (fragment.getFilesListEntity().getList().get(i).isChecked()) {
				delete.add(fragment.getFilesListEntity().getList().get(i));
			}
		}
		if ( delete.size() > 0) {
			JDDialogPopupFromBottom d = new JDDialogPopupFromBottom(fragment);
		} else if (delete.size() == 0) {
			Toast.makeText(fragment.getActivity(), "请选择要删除的文件", Toast.LENGTH_SHORT).show();
		} 
	}
}
