package com.bakerj.infinitecards.transformer;

import android.util.Log;

import com.bakerj.infinitecards.CardItem;
import com.bakerj.infinitecards.Consts;
import com.bakerj.infinitecards.ZIndexTransformer;

/**
 * @author BakerJ
 */
public class DefaultZIndexTransformerToFront implements ZIndexTransformer {
    @Override
    public void transformAnimation(CardItem card, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {
        Log.d(Consts.TAG, " ===> DefaultZIndexTransformerToFront----transformAnimation");
        Log.d(Consts.TAG, " ======> fraction =" + fraction);
        Log.d(Consts.TAG, " ======> fromPosition =" + fromPosition + "   toPosition = " + toPosition);
        if (fraction < 0.5f) {
            card.zIndex = 1f + 0.01f * fromPosition;
        } else {
            card.zIndex = 1f + 0.01f * toPosition;
        }
    }

    @Override
    public void transformInterpolatedAnimation(CardItem card, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {
        Log.d(Consts.TAG, " ===> DefaultZIndexTransformerToFront----transformInterpolatedAnimation");
    }
}
