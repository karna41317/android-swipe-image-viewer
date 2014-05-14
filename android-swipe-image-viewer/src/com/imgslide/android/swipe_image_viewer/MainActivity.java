
/*
Notice how I show an overscroll grow when the user reaches the start or the end of the list of images. 
I make it visible immediately, and then use an animation to fade it away.
Right now all the logic is in MainActivity. 
I thought about encapsulating the viewer code into a custom view and bundling it into a jar file, (Not needed so much , could help if some one need)
*/


package com.imgslide.android.swipe_image_viewer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        ImagePagerAdapter adapter = new ImagePagerAdapter();
        viewPager.setAdapter(adapter);
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private int[] mImages = new int[]{
            R.drawable.chiang_mai,
            R.drawable.himeji,
            R.drawable.petronas_twin_tower,
            R.drawable.ulm
        };

        @Override
        public int getCount() {
            return mImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Context context = MainActivity.this;
            ImageView imageView = new ImageView(context);
            int padding = context.getResources().getDimensionPixelSize(
                    R.dimen.padding_medium);
            imageView.setPadding(padding, padding, padding, padding);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setImageResource(mImages[position]);
            ((ViewPager) container).addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }
    }

    private class SwipeListener extends SimpleOnGestureListener {

        private static final int SWIPE_MIN_DISTANCE = 75;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                    return false;
                }
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    moveNextOrPrevious(1);
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    moveNextOrPrevious(-1);
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }

    private void moveNextOrPrevious(int delta) {
        int nextImagePos = mCurrentPosition + delta;
        if (nextImagePos < 0) {
            mOverscrollLeft.setVisibility(View.VISIBLE);
            mOverscrollLeft.startAnimation(mOverscrollLeftFadeOut);
            return;
        }
        if (nextImagePos >= mImages.length) {
            mOverscrollRight.setVisibility(View.VISIBLE);
            mOverscrollRight.startAnimation(mOverscrollRightFadeOut);
            return;
        }

        mImageSwitcher.setInAnimation(delta > 0 ? mSlideInRight : mSlideInLeft);
        mImageSwitcher.setOutAnimation(delta > 0 ? mSlideOutLeft : mSlideOutRight);
        mCurrentPosition = nextImagePos;
        mImageSwitcher.setImageResource(mImages[mCurrentPosition]);
    }

}
