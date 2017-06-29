package com.bakerj.infinitecards;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.bakerj.infinitecards.transformer.DefaultCommonTransformer;
import com.bakerj.infinitecards.transformer.DefaultTransformerToBack;
import com.bakerj.infinitecards.transformer.DefaultTransformerToFront;
import com.bakerj.infinitecards.transformer.DefaultZIndexTransformerCommon;
import com.nineoldandroids.view.ViewHelper;

/**
 * blog：可自定义动效的卡片切换视图 | BakerJ
 * http://bakerjq.com/2017/05/28/20170528_InfiniteCard/
 */
public class MainActivity extends AppCompatActivity {
    private InfiniteCardView mCardView;
    private BaseAdapter mAdapter1, mAdapter2;
    private int[] resId = {R.mipmap.pic1, R.mipmap.pic2, R.mipmap.pic3, R.mipmap
            .pic4, R.mipmap.pic5};
    private boolean mIsAdapter1 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCardView = (InfiniteCardView) findViewById(R.id.view);
        mAdapter1 = new MyAdapter(resId);
        mAdapter2 = new MyAdapter(resId);
        mCardView.setAdapter(mAdapter1);
        mCardView.setCardAnimationListener(new InfiniteCardView.CardAnimationListener() {
            @Override
            public void onAnimationStart() {
                Toast.makeText(MainActivity.this, "Animation Start", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationEnd() {
                Toast.makeText(MainActivity.this, "Animation End", Toast.LENGTH_SHORT).show();
            }
        });
        initButton();
    }

    private void initButton() {
        findViewById(R.id.pre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Consts.TAG, " ===> pre-onClick");
                Log.d(Consts.TAG, " ======> mIsAdapter1 =" + mIsAdapter1);
                //如果是adapter1
                if (mIsAdapter1) {
                    setStyle2();//switch，互换
                    //第一个和最后一个互换
                    mCardView.bringCardToFront(mAdapter1.getCount() - 1);
                } else {
                    setStyle1();//front,移动到第一个来
                    mCardView.bringCardToFront(mAdapter2.getCount() - 1);
                }
            }
        });
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Consts.TAG, " ===> next-onClick");
                Log.d(Consts.TAG, " ======> mIsAdapter1 =" + mIsAdapter1);
                if (mIsAdapter1) {
                    setStyle2();//switch，互换
                    //第一个和第二个互换
                    mCardView.bringCardToFront(1);
                } else {
                    setStyle3();//last,移动到最后一个去
                    mCardView.bringCardToFront(1);
                }
            }
        });
        findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCardView.isAnimating()) {
                    return;
                }
                Log.d(Consts.TAG, " ===> change-onClick");
                Log.d(Consts.TAG, " ======> mIsAdapter1 =" + mIsAdapter1);
                //切换adapter
                mIsAdapter1 = !mIsAdapter1;
                if (mIsAdapter1) {
                    //从adapter2 -> adapter1
                    setStyle2();//switch，互换
                    mCardView.setAdapter(mAdapter1);
                } else {
                    //从adapter1 -> adapter2
                    setStyle1();//front,移动到第一个来
                    mCardView.setAdapter(mAdapter2);
                }
            }
        });
    }

    private void setStyle1() {
        Log.d(Consts.TAG, " ===> setStyle1");
        mCardView.setClickable(true);
        mCardView.setAnimType(InfiniteCardView.ANIM_TYPE_FRONT);
        mCardView.setAnimInterpolator(new LinearInterpolator());
        mCardView.setTransformerToFront(new DefaultTransformerToFront());
        mCardView.setTransformerToBack(new DefaultTransformerToBack());
        mCardView.setZIndexTransformerToBack(new DefaultZIndexTransformerCommon());
    }

    private void setStyle2() {
        Log.d(Consts.TAG, " ===> setStyle2");
        //设置可点击状态
        mCardView.setClickable(true);
        //设置动画类型
        mCardView.setAnimType(InfiniteCardView.ANIM_TYPE_SWITCH);
        //设置动画插值器
        //TODO 关于OvershootInterpolator
        mCardView.setAnimInterpolator(new OvershootInterpolator(-18));
        //设置 根据插值器的更新值来刷新动画的函数
        mCardView.setTransformerToFront(new DefaultTransformerToFront());
        mCardView.setTransformerToBack(new AnimationTransformer() {
            @Override
            public void transformAnimation(View view, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {
                int positionCount = fromPosition - toPosition;
                float scale = (0.8f - 0.1f * fromPosition) + (0.1f * fraction * positionCount);
                ViewHelper.setScaleX(view, scale);
                ViewHelper.setScaleY(view, scale);
                if (fraction < 0.5) {
                    ViewCompat.setRotationX(view, 180 * fraction);
                } else {
                    ViewCompat.setRotationX(view, 180 * (1 - fraction));
                }
            }

            @Override
            public void transformInterpolatedAnimation(View view, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {
                int positionCount = fromPosition - toPosition;
                float scale = (0.8f - 0.1f * fromPosition) + (0.1f * fraction * positionCount);
                ViewHelper.setTranslationY(view, -cardHeight * (0.8f - scale) * 0.5f - cardWidth * (0.02f *
                        fromPosition - 0.02f * fraction * positionCount));
            }
        });
        mCardView.setZIndexTransformerToBack(new ZIndexTransformer() {
            @Override
            public void transformAnimation(CardItem card, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {
                if (fraction < 0.4f) {
                    card.zIndex = 1f + 0.01f * fromPosition;
                } else {
                    card.zIndex = 1f + 0.01f * toPosition;
                }
            }

            @Override
            public void transformInterpolatedAnimation(CardItem card, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {

            }
        });
    }

    private void setStyle3() {
        Log.d(Consts.TAG, " ===> setStyle3");
        mCardView.setClickable(false);
        mCardView.setAnimType(InfiniteCardView.ANIM_TYPE_FRONT_TO_LAST);
        mCardView.setAnimInterpolator(new OvershootInterpolator(-8));
        mCardView.setTransformerToFront(new DefaultCommonTransformer());
        mCardView.setTransformerToBack(new AnimationTransformer() {
            @Override
            public void transformAnimation(View view, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {
                int positionCount = fromPosition - toPosition;
                float scale = (0.8f - 0.1f * fromPosition) + (0.1f * fraction * positionCount);
                ViewHelper.setScaleX(view, scale);
                ViewHelper.setScaleY(view, scale);
                if (fraction < 0.5) {
                    ViewCompat.setTranslationX(view, cardWidth * fraction * 1.5f);
                    ViewCompat.setRotationY(view, -45 * fraction);
                } else {
                    ViewCompat.setTranslationX(view, cardWidth * 1.5f * (1f - fraction));
                    ViewCompat.setRotationY(view, -45 * (1 - fraction));
                }
            }

            @Override
            public void transformInterpolatedAnimation(View view, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {
                int positionCount = fromPosition - toPosition;
                float scale = (0.8f - 0.1f * fromPosition) + (0.1f * fraction * positionCount);
                ViewHelper.setTranslationY(view, -cardHeight * (0.8f - scale) * 0.5f - cardWidth * (0.02f *
                        fromPosition - 0.02f * fraction * positionCount));
            }
        });
        mCardView.setZIndexTransformerToBack(new ZIndexTransformer() {
            @Override
            public void transformAnimation(CardItem card, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {
                if (fraction < 0.5f) {
                    card.zIndex = 1f + 0.01f * fromPosition;
                } else {
                    card.zIndex = 1f + 0.01f * toPosition;
                }
            }

            @Override
            public void transformInterpolatedAnimation(CardItem card, float fraction, int cardWidth, int cardHeight, int fromPosition, int toPosition) {

            }
        });
    }

    private static class MyAdapter extends BaseAdapter {
        private int[] resIds = {};

        MyAdapter(int[] resIds) {
            this.resIds = resIds;
        }

        @Override
        public int getCount() {
            return resIds.length;
        }

        @Override
        public Integer getItem(int position) {
            return resIds[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .item_card, parent, false);
            }
            convertView.setBackgroundResource(resIds[position]);
            return convertView;
        }
    }
}
