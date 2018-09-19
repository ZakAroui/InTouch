package com.ikazme.intouch;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class OnboardingActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        addSlide(firstFragment);
//        addSlide(secondFragment);
//        addSlide(thirdFragment);

        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle("slide 1");
        sliderPage1.setDescription("just trying things");
        sliderPage1.setImageDrawable(R.drawable.ic_image_gallery);
        sliderPage1.setBgColor(Color.parseColor("#B2C4DE"));
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("slide 2");
        sliderPage2.setDescription("just trying things again");
        sliderPage2.setImageDrawable(R.drawable.ic_image_gallery);
        sliderPage2.setBgColor(Color.parseColor("#CDE5F7"));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        showSkipButton(true);
        setProgressButtonEnabled(true);

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

}
