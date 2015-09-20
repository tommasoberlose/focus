package nego.reminders;

import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;

import nego.reminders.Slide.FirstSlide;
import nego.reminders.Slide.FourthSlide;
import nego.reminders.Slide.SecondSlide;
import nego.reminders.Slide.ThirdSlide;


public class Intro extends AppIntro2 {

    @Override
    public void init(Bundle savedInstanceState) {

        addSlide(new FirstSlide(), getApplicationContext());
        addSlide(new SecondSlide(), getApplicationContext());
        addSlide(new ThirdSlide(), getApplicationContext());
        addSlide(new FourthSlide(), getApplicationContext());

    }

    @Override
    public void onDonePressed() {
        finish();
    }
}
