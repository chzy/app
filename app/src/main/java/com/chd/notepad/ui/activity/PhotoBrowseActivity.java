package com.chd.notepad.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.chd.yunpan.R;
import com.chd.yunpan.utils.Base64Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * 图片浏览界面
 * Created by hmf on 2015/11/20.
 */
public class PhotoBrowseActivity extends Activity implements View.OnClickListener {

    private View rootView;
    private Button photo_del;
    private ViewPager vp_photo;
    ArrayList<String> photoPath;
    private ArrayList<ImageView> imgList = new ArrayList<>();
    private PhotoAdapter photoAdapter;
    private int curretPosition = 0;
    private int selectPosition = 0;
    private List<Integer> mDelPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mDelPosition == null) {
            mDelPosition = new ArrayList<>();
        }
        onAfterOnCreate(savedInstanceState);
    }

    protected void onAfterOnCreate(Bundle savedInstanceState) {
        rootView = View.inflate(this, R.layout.activity_photo_show, null);
        setContentView(rootView);
        photo_del = (Button) findViewById(R.id.photo_del);
        vp_photo = (ViewPager) findViewById(R.id.vp_photo);
        photo_del.setOnClickListener(this);
        try {
            photoPath = getIntent().getStringArrayListExtra("PhotoPath");
            selectPosition = getIntent().getIntExtra("PhotoPosition", 0);
            //Log.e("TAG",photoPath.toString());
            for (int i = 0; i < photoPath.size(); i++) {
                ImageView photo = new ImageView(this);
//                File file = new File(photoPath.get(i));
//                Uri uri = null;
//                if (file.exists()) {
//                    Log.e("TAG","photo");
//                    uri = Uri.fromFile(file);
//                }
//                Picasso.with(this).load(uri).resize(500,500).error(R.drawable.load_failure).into(photo);
                if (photoPath.get(i).startsWith("file")) {
                    Picasso.with(this)
                            .load(photoPath.get(i))
                            .into(photo);
                } else {
                    Bitmap bitmap = Base64Utils.base64ToBitmap(photoPath.get(i));
                    photo.setImageBitmap(bitmap);
                }
                imgList.add(photo);
            }

            photoAdapter = new PhotoAdapter(this, imgList);
            vp_photo.setOffscreenPageLimit(1);
            vp_photo.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    curretPosition = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            vp_photo.setAdapter(photoAdapter);
            vp_photo.setCurrentItem(selectPosition);
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra("imgList", photoPath);
        setResult(RESULT_OK, data);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.photo_del) {
            // imgList.remove(curretPosition);
            if (imgList.size() == 1) {
                imgList.clear();
                Intent data = new Intent();
                data.putExtra("imgList", imgList);
                setResult(996, data);
                finish();
            } else {
                try {
                    // Log.e("TAG",curretPosition+"当前页面");
                    imgList.remove(curretPosition);
                    photoPath.remove(curretPosition);
                    //vp_photo.removeView(imgList.get(curretPosition));
                    photoAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    imgList.clear();
                    finish();
                }
            }
        }
    }

    class PhotoAdapter extends PagerAdapter {
        private ArrayList<ImageView> list;
        private Context context;

        public PhotoAdapter(Context context, ArrayList<ImageView> list) {
            super();
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public int getItemPosition(Object object) {

            return POSITION_NONE;
        }

        //滑动切换的时候销毁当前的组件
        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView((View) object);
        }

        //每次滑动的时候生成的组件
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
