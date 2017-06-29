package com.bakerj.infinitecards.transformer;

import android.util.Log;
import android.view.View;

import com.bakerj.infinitecards.AnimationTransformer;
import com.bakerj.infinitecards.Consts;
import com.nineoldandroids.view.ViewHelper;

/**
 * @author BakerJ
 */
public class DefaultTransformerToFront implements AnimationTransformer {
    @Override
    public void transformAnimation(View view, float fraction, int cardWidth, int cardHeight,
                                   int fromPosition, int toPosition) {
        Log.d(Consts.TAG, " ===> DefaultTransformerToFront---transformAnimation");
        Log.d(Consts.TAG, " ======> fraction =" + fraction);
        Log.d(Consts.TAG, " ======> fromPosition =" + fromPosition + "   toPosition = " + toPosition);
        //跳几个位置，比如 4 -> 0 ，positionCount = 4，fromPosition = 4
        int positionCount = fromPosition - toPosition;
        float scale = (0.8f - 0.1f * fromPosition) + (0.1f * fraction * positionCount);//缩放，这里是一个变大的操作，从0.4到0.8
        ViewHelper.setScaleX(view, scale);
        ViewHelper.setScaleY(view, scale);
        ViewHelper.setRotationX(view, 180 * (1 - fraction));//旋转，也就是倒过来了
        if (fraction < 0.5) {//设置图片的显示
            ViewHelper.setTranslationY(view, -cardHeight * (0.8f - scale) * 0.5f - cardWidth * 0.02f
                    * fromPosition - cardHeight * fraction);
        } else {
            ViewHelper.setTranslationY(view, -cardHeight * (0.8f - scale) * 0.5f - cardWidth * (0.02f *
                    fromPosition - 0.02f * fraction * positionCount) - cardHeight * (1 - fraction));
        }
    }

    @Override
    public void transformInterpolatedAnimation(View view, float fraction, int cardWidth,
                                               int cardHeight, int fromPosition, int toPosition) {
    }
}
