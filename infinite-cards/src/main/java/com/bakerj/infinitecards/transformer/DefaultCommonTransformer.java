package com.bakerj.infinitecards.transformer;

import android.view.View;

import com.bakerj.infinitecards.AnimationTransformer;
import com.nineoldandroids.view.ViewHelper;

/**
 * @author BakerJ
 */
public class DefaultCommonTransformer implements AnimationTransformer {
    @Override
    public void transformAnimation(View view, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {
        //需要跨越的卡片数量
        int positionCount = fromPosition - toPosition;
        //以0.8做为第一张的缩放尺寸，每向后一张缩小0.1
        //(0.8f - 0.1f * fromPosition) = 当前位置的缩放尺寸
        //(0.1f * fraction * positionCount) = 移动过程中需要改变的缩放尺寸
        float scale = (0.8f - 0.1f * fromPosition) + (0.1f * fraction * positionCount);
        ViewHelper.setScaleX(view, scale);
        ViewHelper.setScaleY(view, scale);
        //在Y方向的偏移量，每向后一张，向上偏移卡片宽度的0.02
        //-cardHeight * (0.8f - scale) * 0.5f 对卡片做整体居中处理
        ViewHelper.setTranslationY(view, -cardHeight * (0.8f - scale) * 0.5f - cardWidth * (0.02f *
                fromPosition - 0.02f * fraction * positionCount));
    }

    @Override
    public void transformInterpolatedAnimation(View view, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {

    }
}
