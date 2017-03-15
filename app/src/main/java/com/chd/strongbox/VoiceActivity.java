package com.chd.strongbox;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chd.base.UILActivity;
import com.chd.record.AndroidAudioRecorder;
import com.chd.record.model.AudioChannel;
import com.chd.record.model.AudioSampleRate;
import com.chd.record.model.AudioSource;
import com.chd.strongbox.adapter.VoiceAdapter;
import com.chd.strongbox.domain.VoiceEntity;
import com.chd.yunpan.R;
import com.chd.yunpan.utils.TimeUtils;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * User: Liumj(liumengjie@365tang.cn)
 * Date: 2017-03-03
 * Time: 13:41
 * describe:
 */
public class VoiceActivity extends UILActivity {


	@BindView(R.id.rv_voice_content)
	RecyclerView rvVoiceContent;
	@BindView(R.id.tv_voice_time)
	TextView tvVoiceTime;
	@BindView(R.id.iv_voice_status)
	ImageView ivVoiceStatus;

	@BindView(R.id.iv_left)
	ImageView ivLeft;

	@BindView(R.id.tv_center)
	TextView tvCenter;


	VoiceAdapter adapter = null;
	private List<VoiceEntity> entities;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice);
		ButterKnife.bind(this);
		tvCenter.setText("录音");
		entities = new ArrayList<>();
		adapter = new VoiceAdapter(entities);

		rvVoiceContent.setLayoutManager(new LinearLayoutManager(this));
		rvVoiceContent.setAdapter(adapter);
		rvVoiceContent.addItemDecoration(
				new HorizontalDividerItemDecoration.Builder(this)
						.color(Color.parseColor("#d5d5d5"))
						.size(1)
						.build());
		rvVoiceContent.addOnItemTouchListener(new OnItemClickListener() {
			@Override
			public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
				//点击事件
				pos = position;
				VoiceEntity voiceEntity = entities.get(position);
				String date = voiceEntity.getDate();
				String time = voiceEntity.getTime();
				String title = voiceEntity.getTitle();
				String filePath = entities.get(position).getFilePath();
				int color = Color.parseColor("#f8b82d");
				int requestCode = 1;
				AndroidAudioRecorder.with(VoiceActivity.this)
						// Required
						.setFilePath(filePath)
						.setTitle(title)
						.setColor(color)
						.setRequestCode(requestCode)
						// Optional
						.setSource(AudioSource.MIC)
						.setChannel(AudioChannel.STEREO)
						.setSampleRate(AudioSampleRate.HZ_48000)
						.setAutoStart(true)
						.setExist(true)
						.setKeepDisplayOn(true)
						// Start recording
						.record();

			}
		});
	}

	int pos = -1;

	long time = 0L;
	String filePath = "";

	@OnClick({R.id.iv_left, R.id.iv_voice_status})
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_left:
				onBackPressed();
				break;
			case R.id.iv_voice_status:
				time = System.currentTimeMillis();
				filePath = getCacheDir() + "/" + time + "_audio.wav";
				int color = Color.parseColor("#f8b82d");
				int requestCode = 0;
				AndroidAudioRecorder.with(this)
						// Required
						.setFilePath(filePath)
						.setTitle("新录音" + (entities.size() + 1))
						.setColor(color)
						.setRequestCode(requestCode)
						// Optional
						.setSource(AudioSource.MIC)
						.setChannel(AudioChannel.STEREO)
						.setSampleRate(AudioSampleRate.HZ_48000)
						.setAutoStart(true)
						.setKeepDisplayOn(true)
						// Start recording
						.record();
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK == resultCode) {
			if (requestCode == 0) {
				//添加时候
				// Great! User has recorded and saved the audio file
				boolean delete = data.getBooleanExtra("delete", false);
				if (delete) {
					//删除

				} else {
					String time = TimeUtils.getTime(this.time, "yyyy-MM-dd HH:mm");
					VoiceEntity entity = new VoiceEntity();
					entity.setTitle(data.getStringExtra("title"));
					entity.setDate(time.split(" ")[0]);
					entity.setTime(time.split(" ")[1]);
					entity.setFilePath(filePath);
					entity.setDuration(data.getStringExtra("duration"));
					entities.add(entity);
					Collections.reverse(entities);
					adapter.notifyDataSetChanged();
				}

			} else if (requestCode == 1) {
				//点击已添加的进入
				boolean delete = data.getBooleanExtra("delete", false);
				if (delete) {
					//删除
					entities.remove(pos);
				} else {
					VoiceEntity entity = entities.get(pos);
					entity.setTitle(data.getStringExtra("title"));
				}
				adapter.notifyDataSetChanged();
			}
		}
	}
}
