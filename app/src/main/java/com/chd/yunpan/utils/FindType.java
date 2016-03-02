package com.chd.yunpan.utils;

import com.chd.yunpan.R;

public class FindType {

	public static int findDir(String path) {
		if ("音乐".equals(path)) {
			return R.drawable.f_music;
		} else if ("电影".equals(path)) {
			return R.drawable.f_video;
		} else if ("图片".equals(path)) {
			return R.drawable.f_img;
		} else if ("书籍".equals(path)) {
			return R.drawable.f_book;
		} else {
			return R.drawable.cloud_dir_icon;
		}
	}

	public static int findImage(String path) {

		int len=path.length();
		if (len<4)
			return R.drawable.ic_type_unknow;
		String ext=path.substring(path.length()-4);
		if (!ext.startsWith("."))
			return R.drawable.ic_type_unknow;

		int[] r = { R.drawable.ft_aac, R.drawable.ft_ac3, R.drawable.ft_ai,
				R.drawable.ft_aif, R.drawable.ft_aifc, R.drawable.ft_aiff,
				R.drawable.ft_amr, R.drawable.ft_ani, R.drawable.ft_ape,
				R.drawable.ft_apk, R.drawable.ft_asf, R.drawable.ft_au,
				R.drawable.ft_avi, R.drawable.ft_bat, R.drawable.ft_bin,
				R.drawable.ft_bmp, R.drawable.ft_bup, R.drawable.ft_cab,
				R.drawable.ft_cal, R.drawable.ft_cat, R.drawable.ft_cd,
				R.drawable.ft_cur, R.drawable.ft_dat, R.drawable.ft_dcr,
				R.drawable.ft_der, R.drawable.ft_dic, R.drawable.ft_dll,
				R.drawable.ft_doc, R.drawable.ft_docx, R.drawable.ft_dvd,
				R.drawable.ft_dwg, R.drawable.ft_dwt, R.drawable.ft_epub,
				R.drawable.ft_fla, R.drawable.ft_flac, R.drawable.ft_flv,
				R.drawable.ft_fon, R.drawable.ft_font, R.drawable.ft_gif,
				R.drawable.ft_gp, R.drawable.ft_hlp, R.drawable.ft_hst,
				R.drawable.ft_html, R.drawable.ft_ico, R.drawable.ft_ifo,
				R.drawable.ft_inf, R.drawable.ft_ini, R.drawable.ft_iso,
				R.drawable.ft_java, R.drawable.ft_jif, R.drawable.ft_jpg,
				R.drawable.ft_log, R.drawable.ft_m4a, R.drawable.ft_mid,
				R.drawable.ft_mkv, R.drawable.ft_mmf, R.drawable.ft_mmm,
				R.drawable.ft_mod, R.drawable.ft_mov, R.drawable.ft_mp2,
				R.drawable.ft_mp2v, R.drawable.ft_mp3, R.drawable.ft_mp4,
				R.drawable.ft_mpeg, R.drawable.ft_mpg, R.drawable.ft_msp,
				R.drawable.ft_ogg, R.drawable.ft_pdf, R.drawable.ft_png,
				R.drawable.ft_ppt, R.drawable.ft_pptx, R.drawable.ft_psd,
				R.drawable.ft_ra, R.drawable.ft_ram, R.drawable.ft_rar,
				R.drawable.ft_reg, R.drawable.ft_rm, R.drawable.ft_rmvb,
				R.drawable.ft_rtf, R.drawable.ft_swf, R.drawable.ft_theme,
				R.drawable.ft_tiff, R.drawable.ft_tlb, R.drawable.ft_ttf,
				R.drawable.ft_txt, R.drawable.ft_vob, R.drawable.ft_wav,
				R.drawable.ft_wma, R.drawable.ft_wmv, R.drawable.ft_wpl,
				R.drawable.ft_wri, R.drawable.ft_xls, R.drawable.ft_xlsx,
				R.drawable.ft_xml, R.drawable.ft_xsl, R.drawable.ft_zip };
		String[] ss = { ".aac", ".ac3", ".ai", ".aif", ".aifc", ".aiff",
				".amr", ".ani", ".ape", ".apk", ".asf", ".au", ".avi", ".bat",
				".bin", ".bmp", ".bup", ".cab", ".cal", ".cat", ".cd", ".cur",
				".dat", ".dcr", ".der", ".dic", ".dll", ".doc", ".docx",
				".dvd", ".dwg", ".dwt", ".epub", ".fla", ".flac", ".flv",
				".fon", ".font", ".gif", ".gp", ".hlp", ".hst", ".html",
				".ico", ".ifo", ".inf", ".ini", ".iso", ".java", ".jif",
				".jpg", ".log", ".m4a", ".mid", ".mkv", ".mmf", ".mmm", ".mod",
				".mov", ".mp2", ".mp2v", ".mp3", ".mp4", ".mpeg", ".mpg",
				".msp", ".ogg", ".pdf", ".png", ".ppt", ".pptx", ".psd", ".ra",
				".ram", ".rar", ".reg", ".rm", ".rmvb", ".rtf", ".swf",
				".theme", ".tiff", ".tlb", ".ttf", ".txt", ".vob", ".wav",
				".wma", ".wmv", ".wpl", ".wri", ".xls", ".xlsx", ".xml",
				".xsl", ".zip" };
		for (int i = 0; i < ss.length; i++) {
			
			/*if (path!=null&&path.toLowerCase().endsWith(ss[i])) {
				return r[i];
			}*/
			if (ext!=null&&ext.toLowerCase().endsWith(ss[i])) {
				return r[i];
			}

		}
		return R.drawable.ic_type_unknow;
	}
}
