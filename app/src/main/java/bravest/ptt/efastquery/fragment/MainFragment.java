package bravest.ptt.efastquery.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.utils.PLog;

/**
 * Created by root on 2/13/17.
 */

public class MainFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.seal_start);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView imageView = (ImageView) view;
                Drawable drawable = imageView.getDrawable();
                PLog.log("out");
                PLog.log(drawable);
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).start();
                    PLog.log("click");
                }



                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, "scaleX", 1, 0.5f);
//
                ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(imageView, "scaleY", 1, 0.5f);

                ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(imageView, "translationY",imageView.getY(),imageView.getY() - 400);
//                objectAnimator.setDuration(3000).setRepeatCount(1);
//                objectAnimator.setInterpolator(new LinearInterpolator());

                AnimatorSet set = new AnimatorSet();
                set.play(objectAnimator).with(objectAnimator2).with(objectAnimator3);
                set.setDuration(1000).start();
//                set.playTogether(objectAnimator, objectAnimator2);
//                set.start();
            }
        });

        return view;
    }
}
