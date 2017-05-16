package com.chd.record;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chd.record.model.AudioChannel;
import com.chd.record.model.AudioSampleRate;
import com.chd.record.model.AudioSource;
import com.cleveroad.audiovisualization.DbmHandler;
import com.cleveroad.audiovisualization.GLAudioVisualizationView;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import omrecorder.AudioChunk;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.Recorder;

/**
 * User: Liumj(liumengjie@365tang.cn)
 * Date: 2017-03-03
 * Time: 14:51
 * describe:
 */
public class AudioRecorderActivity extends AppCompatActivity
		implements PullTransport.OnAudioChunkPulledListener, MediaPlayer.OnCompletionListener {

	private String filePath;
	private AudioSource source;
	private AudioChannel channel;
	private AudioSampleRate sampleRate;
	private int color;
	private boolean autoStart;
	private boolean keepDisplayOn;

	private MediaPlayer player;
	private Recorder recorder;
	private VisualizerHandler visualizerHandler;

	private Timer timer;
	private MenuItem saveMenuItem;
	private int recorderSecondsElapsed;
	private int playerSecondsElapsed;
	private boolean isRecording;

	private RelativeLayout contentLayout;
	private GLAudioVisualizationView visualizerView;
	private TextView statusView;
	private TextView timerView;
	private ImageButton restartView;
	private ImageButton recordView;
	private ImageView delete;
	private ImageButton playView;
	private TextView titleView;
	private String title;
	private boolean exist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aar_activity_audio_recorder);

		if (savedInstanceState != null) {
			filePath = savedInstanceState.getString(AndroidAudioRecorder.EXTRA_FILE_PATH);
			title = savedInstanceState.getString(AndroidAudioRecorder.EXTRA_TITLE);
			source = (AudioSource) savedInstanceState.getSerializable(AndroidAudioRecorder.EXTRA_SOURCE);
			channel = (AudioChannel) savedInstanceState.getSerializable(AndroidAudioRecorder.EXTRA_CHANNEL);
			sampleRate = (AudioSampleRate) savedInstanceState.getSerializable(AndroidAudioRecorder.EXTRA_SAMPLE_RATE);
			color = savedInstanceState.getInt(AndroidAudioRecorder.EXTRA_COLOR);
			autoStart = savedInstanceState.getBoolean(AndroidAudioRecorder.EXTRA_AUTO_START);
			exist = savedInstanceState.getBoolean(AndroidAudioRecorder.EXTRA_EXIST, false);
			keepDisplayOn = savedInstanceState.getBoolean(AndroidAudioRecorder.EXTRA_KEEP_DISPLAY_ON);
		} else {
			filePath = getIntent().getStringExtra(AndroidAudioRecorder.EXTRA_FILE_PATH);
			title = getIntent().getStringExtra(AndroidAudioRecorder.EXTRA_TITLE);
			source = (AudioSource) getIntent().getSerializableExtra(AndroidAudioRecorder.EXTRA_SOURCE);
			channel = (AudioChannel) getIntent().getSerializableExtra(AndroidAudioRecorder.EXTRA_CHANNEL);
			sampleRate = (AudioSampleRate) getIntent().getSerializableExtra(AndroidAudioRecorder.EXTRA_SAMPLE_RATE);
			color = getIntent().getIntExtra(AndroidAudioRecorder.EXTRA_COLOR, Color.BLACK);
			exist = getIntent().getBooleanExtra(AndroidAudioRecorder.EXTRA_EXIST, false);
			autoStart = getIntent().getBooleanExtra(AndroidAudioRecorder.EXTRA_AUTO_START, false);
			keepDisplayOn = getIntent().getBooleanExtra(AndroidAudioRecorder.EXTRA_KEEP_DISPLAY_ON, false);
		}

		if (keepDisplayOn) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}

		if (getSupportActionBar() != null) {
			getSupportActionBar().setHomeButtonEnabled(true);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setElevation(0);
			View customView = getLayoutInflater().inflate(R.layout.custom_title, new LinearLayout(this), false);
			final TextView tv_title = (TextView) customView.findViewById(R.id.title);
			tv_title.setText(title);
			getSupportActionBar().setCustomView(customView);
			customView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MaterialDialog.Builder md = new MaterialDialog.Builder(AudioRecorderActivity.this);
					final EditText editText = new EditText(AudioRecorderActivity.this);
					md.title("修改文件名")
							.customView(editText, true)
							.positiveText("确定")
							.negativeText("取消")
							.onPositive(new MaterialDialog.SingleButtonCallback() {
								@Override
								public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
									tv_title.setText(editText.getText().toString());
								}
							}).onNegative(new MaterialDialog.SingleButtonCallback() {
						@Override
						public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

						}
					}).show();
				}
			});
			getSupportActionBar().setDisplayShowCustomEnabled(true);
			getSupportActionBar().setDisplayShowTitleEnabled(false);
			getSupportActionBar().setBackgroundDrawable(
					new ColorDrawable(Util.getDarkerColor(color)));
			getSupportActionBar().setHomeAsUpIndicator(
					ContextCompat.getDrawable(this, R.drawable.aar_ic_clear));
		}

		visualizerView = new GLAudioVisualizationView.Builder(this)
				.setLayersCount(1)
				.setWavesCount(6)
				.setWavesHeight(R.dimen.aar_wave_height)
				.setWavesFooterHeight(R.dimen.aar_footer_height)
				.setBubblesPerLayer(20)
				.setBubblesSize(R.dimen.aar_bubble_size)
				.setBubblesRandomizeSize(true)
				.setBackgroundColor(Util.getDarkerColor(color))
				.setLayerColors(new int[]{color})
				.build();

		contentLayout = (RelativeLayout) findViewById(R.id.content);
		statusView = (TextView) findViewById(R.id.status);
		timerView = (TextView) findViewById(R.id.timer);
		titleView = (TextView) findViewById(R.id.title);
		restartView = (ImageButton) findViewById(R.id.restart);
		recordView = (ImageButton) findViewById(R.id.record);
		playView = (ImageButton) findViewById(R.id.play);
		delete = (ImageView) findViewById(R.id.delete);
		delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//删除此文件
				stopRecording();
				Intent intent = new Intent();
				intent.putExtra("duration", timerView.getText().toString());
				intent.putExtra("title", titleView.getText().toString());
				intent.putExtra("delete", true);
				setResult(RESULT_OK, intent);
				finish();

			}
		});

		contentLayout.setBackgroundColor(Util.getDarkerColor(color));
		contentLayout.addView(visualizerView, 0);
		restartView.setVisibility(View.INVISIBLE);
		titleView.setText(title);
		titleView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {


			}
		});
		playView.setVisibility(View.INVISIBLE);

		if (exist) {
			//如果是已存在的
			startPlay();
		}

		if (Util.isBrightColor(color)) {
			ContextCompat.getDrawable(this, R.drawable.aar_ic_clear)
					.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
			ContextCompat.getDrawable(this, R.drawable.aar_ic_check)
					.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
			statusView.setTextColor(Color.BLACK);
			timerView.setTextColor(Color.BLACK);
			restartView.setColorFilter(Color.BLACK);
			recordView.setColorFilter(Color.BLACK);
			playView.setColorFilter(Color.BLACK);
			titleView.setTextColor(Color.BLACK);
		}
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if (autoStart && !isRecording&& !exist) {
			toggleRecording(null);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			visualizerView.onResume();
		} catch (Exception e) {
		}
	}

	@Override
	protected void onPause() {
		if(!exist){
		restartRecording(null);
		}
		try {
			visualizerView.onPause();
		} catch (Exception e) {
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		restartRecording(null);
		setResult(RESULT_CANCELED);
		try {
			visualizerView.release();
		} catch (Exception e) {
		}
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(AndroidAudioRecorder.EXTRA_FILE_PATH, filePath);
		outState.putInt(AndroidAudioRecorder.EXTRA_COLOR, color);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.aar_audio_recorder, menu);
		saveMenuItem = menu.findItem(R.id.action_save);
		if (saveMenuItem != null)
			saveMenuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.aar_ic_check));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int i = item.getItemId();
		if (i == android.R.id.home) {
			finish();
		} else if (i == R.id.action_save) {
			selectAudio();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onAudioChunkPulled(AudioChunk audioChunk) {
		float amplitude = isRecording ? (float) audioChunk.maxAmplitude() : 0f;
		visualizerHandler.onDataReceived(amplitude);
	}

	@Override
	public void onCompletion(MediaPlayer mediaPlayer) {
		stopPlaying();
	}

	private void selectAudio() {
		stopRecording();
		Intent intent = new Intent();
		intent.putExtra("duration", timerView.getText().toString());
		intent.putExtra("title", titleView.getText().toString());
		setResult(RESULT_OK, intent);
		finish();
	}

	public void toggleRecording(View v) {
		stopPlaying();
		Util.wait(100, new Runnable() {
			@Override
			public void run() {
				if (isRecording) {
					pauseRecording();
				} else {
					resumeRecording();
				}
			}
		});
	}

	public void togglePlaying(View v) {
		pauseRecording();
		Util.wait(100, new Runnable() {
			@Override
			public void run() {
				if (isPlaying()) {
					stopPlaying();
				} else {
					startPlaying();
				}
			}
		});
	}

	private void startPlay() {
		try {
			player = new MediaPlayer();
			player.setDataSource(filePath);
			player.prepare();
			player.start();
			if (visualizerView != null) {
				visualizerView.linkTo(DbmHandler.Factory.newVisualizerHandler(this, player));
				visualizerView.post(new Runnable() {
					@Override
					public void run() {
						player.setOnCompletionListener(AudioRecorderActivity.this);
					}
				});
			}
			if (saveMenuItem != null)
				saveMenuItem.setVisible(true);
			player.setOnCompletionListener(this);
			recordView.setVisibility(View.INVISIBLE);
			timerView.setText("00:00:00");
			playView.setVisibility(View.VISIBLE);
			statusView.setText(R.string.aar_playing);
			statusView.setVisibility(View.VISIBLE);
			restartView.setVisibility(View.VISIBLE);
			timerView.setVisibility(View.VISIBLE);
			playView.setImageResource(R.drawable.aar_ic_stop);
			playerSecondsElapsed = 0;
			startTimer();
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	public void restartRecording(View v) {
		if (exist) {
			if (player != null) {
				player.seekTo(0);
				if(!player.isPlaying()){
					player.start();
				}
				timerView.setText("00:00:00");
				recorderSecondsElapsed = 0;
				playerSecondsElapsed = 0;
				startTimer();
			}
			return;
		}
		if (isRecording) {
			stopRecording();
		} else if (isPlaying()) {
			stopPlaying();
		} else {
			visualizerHandler = new VisualizerHandler();
			visualizerView.linkTo(visualizerHandler);
			visualizerView.release();
			if (visualizerHandler != null) {
				visualizerHandler.stop();
			}
		}
		if (saveMenuItem != null)
			saveMenuItem.setVisible(false);
		statusView.setVisibility(View.INVISIBLE);
		restartView.setVisibility(View.INVISIBLE);
		playView.setVisibility(View.INVISIBLE);
		recordView.setImageResource(R.drawable.aar_ic_rec);
		timerView.setText("00:00:00");
		recorderSecondsElapsed = 0;
		playerSecondsElapsed = 0;
	}

	private void resumeRecording() {
		isRecording = true;
		if (saveMenuItem != null)
			saveMenuItem.setVisible(false);
		statusView.setText(R.string.aar_recording);
		statusView.setVisibility(View.VISIBLE);
		restartView.setVisibility(View.INVISIBLE);
		playView.setVisibility(View.INVISIBLE);
		recordView.setImageResource(R.drawable.aar_ic_pause);
		playView.setImageResource(R.drawable.aar_ic_play);

		visualizerHandler = new VisualizerHandler();
		visualizerView.linkTo(visualizerHandler);

		if (recorder == null) {
			timerView.setText("00:00:00");
			recorder = OmRecorder.wav(
					new PullTransport.Default(Util.getMic(source, channel, sampleRate), AudioRecorderActivity.this),
					new File(filePath));
		}

		recorder.resumeRecording();

		startTimer();
	}

	private void pauseRecording() {
		isRecording = false;
		if (!isFinishing()) {
			if (saveMenuItem != null)
				saveMenuItem.setVisible(true);
		}
		statusView.setText(R.string.aar_paused);
		statusView.setVisibility(View.VISIBLE);
		restartView.setVisibility(View.VISIBLE);
		playView.setVisibility(View.VISIBLE);
		recordView.setImageResource(R.drawable.aar_ic_rec);
		playView.setImageResource(R.drawable.aar_ic_play);

		visualizerView.release();
		if (visualizerHandler != null) {
			visualizerHandler.stop();
		}

		if (recorder != null) {
			recorder.pauseRecording();
		}

		stopTimer();
	}

	private void stopRecording() {
		visualizerView.release();
		if (visualizerHandler != null) {
			visualizerHandler.stop();
		}

		recorderSecondsElapsed = 0;
		if (recorder != null) {
			recorder.stopRecording();
			recorder = null;
		}

		stopTimer();
	}

	private void startPlaying() {
		try {
			stopRecording();
			player = new MediaPlayer();
			player.setDataSource(filePath);
			player.prepare();
			player.start();

			visualizerView.linkTo(DbmHandler.Factory.newVisualizerHandler(this, player));
			visualizerView.post(new Runnable() {
				@Override
				public void run() {
					player.setOnCompletionListener(AudioRecorderActivity.this);
				}
			});

			timerView.setText("00:00:00");
			statusView.setText(R.string.aar_playing);
			statusView.setVisibility(View.VISIBLE);
			playView.setImageResource(R.drawable.aar_ic_stop);

			playerSecondsElapsed = 0;
			startTimer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void stopPlaying() {
		statusView.setText("");
		statusView.setVisibility(View.INVISIBLE);
		playView.setImageResource(R.drawable.aar_ic_play);

		visualizerView.release();
		if (visualizerHandler != null) {
			visualizerHandler.stop();
		}

		if (player != null) {
			try {
				player.stop();
				player.reset();
			} catch (Exception e) {
			}
		}

		stopTimer();
	}

	private boolean isPlaying() {
		try {
			return player != null && player.isPlaying() && !isRecording;
		} catch (Exception e) {
			return false;
		}
	}

	private void startTimer() {
		stopTimer();
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				updateTimer();
			}
		}, 0, 1000);
	}

	private void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}

	private void updateTimer() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (isRecording) {
					recorderSecondsElapsed++;
					timerView.setText(Util.formatSeconds(recorderSecondsElapsed));
				} else if (isPlaying()) {
					playerSecondsElapsed++;
					timerView.setText(Util.formatSeconds(playerSecondsElapsed));
				}
			}
		});
	}
}
