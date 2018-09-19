package com.ikazme.intouch;

import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class OnboardingActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SliderPage sliderPage1 = createSliderPage(
                "Scan Business Cards on the Fly",
                "Share contacts seamlessly.",
                R.drawable.ic_image_gallery);
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 =  createSliderPage(
                "From Everywhere",
                "From camera or shared business cards, everything will populate.",
                R.drawable.ic_image_gallery);
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = createSliderPage(
                "All In One Place",
                "Don't worry, all your contacts are still at the same place.",
                R.drawable.ic_image_gallery);
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        setBarColor(Color.parseColor("#4ea740"));
        showSkipButton(false);
        setDoneTextTypeface("font/roboto_condensed_bold.ttf");
        setProgressButtonEnabled(true);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        setResult(RESULT_OK);
        finish();
    }

    private SliderPage createSliderPage(String title, String desc, @DrawableRes int image){
        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle(title);
        sliderPage.setDescription(desc);
        sliderPage.setImageDrawable(image);
        setSliderTitleDescStyles(sliderPage);

        return sliderPage;
    }

    private void setSliderTitleDescStyles(SliderPage sliderPage){

        sliderPage.setTitleTypeface("font/roboto_condensed_regular.ttf");
        sliderPage.setTitleColor(Color.parseColor("#44444C"));
        sliderPage.setDescTypeface("font/roboto_condensed_regular.ttf");
        sliderPage.setDescColor(Color.BLACK);
        sliderPage.setBgColor(Color.parseColor("#B8E1DE"));
    }

}
