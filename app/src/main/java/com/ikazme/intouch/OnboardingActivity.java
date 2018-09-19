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

        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle("slide 1");
        sliderPage1.setTitleColor(Color.BLACK);
        sliderPage1.setDescription("just trying things");
        sliderPage1.setDescColor(Color.BLACK);
        sliderPage1.setImageDrawable(R.drawable.ic_image_gallery);
        sliderPage1.setBgColor(Color.parseColor("#B8E1DE"));
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("slide 2");
        sliderPage2.setTitleColor(Color.BLACK);
        sliderPage2.setDescription("just trying things again");
        sliderPage2.setDescColor(Color.BLACK);
        sliderPage2.setImageDrawable(R.drawable.ic_image_gallery);
        sliderPage2.setBgColor(Color.parseColor("#B8E1DE"));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle("slide 2");
        sliderPage3.setTitleColor(Color.BLACK);
        sliderPage3.setDescription("just trying things again");
        sliderPage3.setDescColor(Color.BLACK);
        sliderPage3.setImageDrawable(R.drawable.ic_image_gallery);
        sliderPage3.setBgColor(Color.parseColor("#B8E1DE"));
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        setBarColor(Color.parseColor("#4ea740"));

        showSkipButton(false);
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
