package com.lockscreen.pattern;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.chd.strongbox.StrongBoxActivity;
import com.chd.yunpan.R;
import com.chd.yunpan.application.UILApplication;
import com.chd.yunpan.share.ShareUtils;
import com.chd.yunpan.ui.LoginActivity;
import com.lockscreen.view.LockPatternUtils;
import com.lockscreen.view.LockPatternView;
import com.lockscreen.view.LockPatternView.Cell;

import java.util.List;

public class UnlockGesturePasswordActivity extends Activity {
	private LockPatternView mLockPatternView;
	private int mFailedPatternAttemptsSinceLastTimeout = 0;
	private CountDownTimer mCountdownTimer = null;
	private Handler mHandler = new Handler();
	private TextView mHeadTextView;
	private Animation mShakeAnim;

	private Toast mToast;
	private TextView mForgetTextView;

	private void showToast(CharSequence message) {
		if (null == mToast) {
			mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			mToast.setText(message);
		}

		mToast.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gesturepassword_unlock);

		mLockPatternView = (LockPatternView) this
				.findViewById(R.id.gesturepwd_unlock_lockview);
		mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
		mLockPatternView.setTactileFeedbackEnabled(true);
		mHeadTextView = (TextView) findViewById(R.id.gesturepwd_unlock_text);
		mForgetTextView = (TextView) findViewById(R.id.gesturepwd_unlock_forget);
		mForgetTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=new Intent(UnlockGesturePasswordActivity.this,LoginActivity.class);
				ShareUtils shareUtils = new ShareUtils(UnlockGesturePasswordActivity.this);
				shareUtils.setLoginEntity(null);
				shareUtils.setURL("");
				shareUtils.setAutoLogin(false);
				shareUtils.setPwd("");
				intent.putExtra("Unlock", true);
				startActivity(intent);

				showToast("忘记手机密码需重新登录");
				UnlockGesturePasswordActivity.this.finish();

			}
		});

		mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!UILApplication.getInstance().getLockPatternUtils().savedPatternExists()) {
			startActivity(new Intent(this, GuideGesturePasswordActivity.class));
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCountdownTimer != null)
			mCountdownTimer.cancel();
	}
	private Runnable mClearPatternRunnable = new Runnable() {
		public void run() {
			mLockPatternView.clearPattern();
		}
	};

	protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

		public void onPatternStart() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
			patternInProgress();
		}

		public void onPatternCleared() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
		}

		public void onPatternDetected(List<LockPatternView.Cell> pattern) {
			if (pattern == null)
				return;
			if (UILApplication.getInstance().getLockPatternUtils().checkPattern(pattern)) {
				mLockPatternView
						.setDisplayMode(LockPatternView.DisplayMode.Correct);
				Intent intent = new Intent(UnlockGesturePasswordActivity.this,
StrongBoxActivity.class);
// 打开新的Activity
				intent.putExtra("unlock", true);
				startActivity(intent);
				
				showToast("解锁成功");
				finish();
			} else {
				mLockPatternView
						.setDisplayMode(LockPatternView.DisplayMode.Wrong);
				if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
					mFailedPatternAttemptsSinceLastTimeout++;
					int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT
							- mFailedPatternAttemptsSinceLastTimeout;
					if (retry >= 0) {
						if (retry == 0)
							showToast("您已5次输错密码，请30秒后再试");
						mHeadTextView.setText("密码错误，还可以再输入" + retry + "次");
						mHeadTextView.setTextColor(Color.RED);
						mHeadTextView.startAnimation(mShakeAnim);
					}

				}else{
					showToast("输入长度不够，请重试");
				}

				if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
					mHandler.postDelayed(attemptLockout, 2000);
				} else {
					mLockPatternView.postDelayed(mClearPatternRunnable, 2000);
				}
			}
		}

		public void onPatternCellAdded(List<Cell> pattern) {

		}

		private void patternInProgress() {
		}
	};
	Runnable attemptLockout = new Runnable() {

		@Override
		public void run() {
			mLockPatternView.clearPattern();
			mLockPatternView.setEnabled(false);
			mCountdownTimer = new CountDownTimer(
					LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS + 1, 1000) {

				@Override
				public void onTick(long millisUntilFinished) {
					int secondsRemaining = (int) (millisUntilFinished / 1000) - 1;
					if (secondsRemaining > 0) {
						mHeadTextView.setText(secondsRemaining + " 秒后重试");
					} else {
						mHeadTextView.setText("请绘制手势密码");
						mHeadTextView.setTextColor(Color.WHITE);
					}

				}

				@Override
				public void onFinish() {
					mLockPatternView.setEnabled(true);
					mFailedPatternAttemptsSinceLastTimeout = 0;
				}
			}.start();
		}
	};

}
