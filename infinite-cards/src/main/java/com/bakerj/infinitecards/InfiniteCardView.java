package com.bakerj.infinitecards;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;

import com.bakerj.infinitecards.lib.R;

/**
 * @author BakerJ
 *         https://github.com/BakerJQ/InfiniteCards
 */
public class InfiniteCardView extends ViewGroup {
    //Three types of animation,三种动画类型
    public static final int ANIM_TYPE_FRONT = 0;//被选中的卡片通过自定义动效移至第一，其他的卡片通过通用动效补位
    public static final int ANIM_TYPE_SWITCH = 1;//选中的卡片和第一张卡片互换位置，并都是自定义动效
    public static final int ANIM_TYPE_FRONT_TO_LAST = 2;//第一张图片通过自定义动效移至最后，其他卡片通过通用动效补位
    //cardHeight / cardWidth = CARD_SIZE_RATIO
    private static final float CARD_SIZE_RATIO = 0.5f;
    //cardHeight / cardWidth = mCardRatio
    private float mCardRatio = CARD_SIZE_RATIO;
    //animation helper
    private CardAnimationHelper mAnimationHelper;
    //view adapter
    private BaseAdapter mAdapter;
    private int mCardWidth, mCardHeight;

    public InfiniteCardView(@NonNull Context context) {
        this(context, null);
    }

    public InfiniteCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfiniteCardView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d(Consts.TAG, " ===> InfiniteCardView creat");
        init(context, attrs);
        setClickable(true);
    }

    private void init(Context context, AttributeSet attrs) {
        Log.d(Consts.TAG, " ===> init");

//        animType : 动效展示类型
        //        front : 将点击的卡片切换到第一个
        //        switchPosition : 将点击的卡片和第一张卡片互换位置
        //        frontToLast : 将第一张卡片移到最后，后面的卡片往前移动一个
//        cardRatio : 卡片宽高比
//        animDuration : 卡片动效时间
//        animAddRemoveDelay : 卡片组切换时，添加与移出时，相邻卡片展示动效的间隔时间
//        animAddRemoveDuration : 卡片组切换时，添加与移出时，卡片动效时间

        //动画类型
        int animType = ANIM_TYPE_FRONT;
        //设置动画的执行和延迟时间
        int animDuration = CardAnimationHelper.ANIM_DURATION;
        int animAddRemoveDuration = CardAnimationHelper.ANIM_ADD_REMOVE_DURATION;
        int animAddRemoveDelay = CardAnimationHelper.ANIM_ADD_REMOVE_DELAY;
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.InfiniteCardView);
            animType = ta.getInt(R.styleable.InfiniteCardView_animType, ANIM_TYPE_FRONT);
            mCardRatio = ta.getFloat(R.styleable.InfiniteCardView_cardRatio, CARD_SIZE_RATIO);
            animDuration = ta.getInt(R.styleable.InfiniteCardView_animDuration, CardAnimationHelper.ANIM_DURATION);
            animAddRemoveDuration = ta.getInt(R.styleable.InfiniteCardView_animAddRemoveDuration,
                    CardAnimationHelper.ANIM_ADD_REMOVE_DURATION);
            animAddRemoveDelay = ta.getInt(R.styleable.InfiniteCardView_animAddRemoveDelay,
                    CardAnimationHelper.ANIM_ADD_REMOVE_DELAY);
            ta.recycle();
        }
        //初始化动画执行器
        mAnimationHelper = new CardAnimationHelper(animType, animDuration, this);
        mAnimationHelper.setAnimAddRemoveDuration(animAddRemoveDuration);
        mAnimationHelper.setAnimAddRemoveDelay(animAddRemoveDelay);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(Consts.TAG, " ===> onMeasure");
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY) {
            int childCount = getChildCount();
            int childWidth = 0, childHeight = 0;
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                childWidth = Math.max(childView.getMeasuredWidth(), childWidth);
                childHeight = Math.max(childView.getMeasuredHeight(), childHeight);
            }
            setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? sizeWidth : childWidth,
                    (heightMode == MeasureSpec.EXACTLY) ? sizeHeight : childHeight);
        } else {
            setMeasuredDimension(sizeWidth, sizeHeight);
        }
        if (mCardWidth == 0 || mCardHeight == 0) {
            setCardSize(true);
        }
    }

    private void setCardSize(boolean resetAdapter) {
        Log.d(Consts.TAG, " ===> setCardSize");
        mCardWidth = getMeasuredWidth();
        mCardHeight = (int) (mCardWidth * mCardRatio);
        mAnimationHelper.setCardSize(mCardWidth, mCardHeight);
        mAnimationHelper.initAdapterView(mAdapter, resetAdapter);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(Consts.TAG, " ===> onLayout");
        int childCount = getChildCount();
        int childWidth, childHeight;
        int childLeft, childTop, childRight, childBottom;
        int width = getWidth(), height = getHeight();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            childWidth = childView.getMeasuredWidth();
            childHeight = childView.getMeasuredHeight();
            childLeft = (width - childWidth) / 2;
            childTop = (height - childHeight) / 2;
            childRight = childLeft + childWidth;
            childBottom = childTop + childHeight;
            childView.layout(childLeft, childTop, childRight, childBottom);
        }
    }

    void addCardView(CardItem card) {
        addView(getCardView(card));
    }

    void addCardView(CardItem card, int position) {
        addView(getCardView(card), position);
    }

    private View getCardView(final CardItem card) {
        View view = card.view;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(mCardWidth,
                mCardHeight);
        view.setLayoutParams(layoutParams);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                bringCardToFront(card);
            }
        });
        return view;
    }

    private void bringCardToFront(CardItem card) {
        if (!isClickable()) {
            return;
        }
        mAnimationHelper.bringCardToFront(card);
    }

    /**
     * bring the specific position card to front
     * 把选中的card 带到前面
     *
     * @param position position
     */
    public void bringCardToFront(int position) {
        mAnimationHelper.bringCardToFront(position);
    }

    /**
     * set view adapter
     *
     * @param adapter adapter
     */
    public void setAdapter(BaseAdapter adapter) {
        Log.d(Consts.TAG, " ===> setAdapter");
        this.mAdapter = adapter;
        //注册adapter的观察者，内容发生变化时，执行onChanged方法
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                mAnimationHelper.notifyDataSetChanged(mAdapter);
            }
        });
        //第一次进入的时候，初始化helper，true表示需要重置
        mAnimationHelper.initAdapterView(adapter, true);
    }

    public void setTransformerToFront(AnimationTransformer toFrontTransformer) {
        mAnimationHelper.setTransformerToFront(toFrontTransformer);
    }

    public void setTransformerToBack(AnimationTransformer toBackTransformer) {
        mAnimationHelper.setTransformerToBack(toBackTransformer);
    }

    public void setCommonSwitchTransformer(AnimationTransformer commonTransformer) {
        mAnimationHelper.setCommonSwitchTransformer(commonTransformer);
    }

    public void setTransformerCommon(AnimationTransformer transformerCommon) {
        mAnimationHelper.setTransformerCommon(transformerCommon);
    }

    public void setZIndexTransformerToFront(ZIndexTransformer zIndexTransformerToFront) {
        mAnimationHelper.setZIndexTransformerToFront(zIndexTransformerToFront);
    }

    public void setZIndexTransformerToBack(ZIndexTransformer zIndexTransformerToBack) {
        mAnimationHelper.setZIndexTransformerToBack(zIndexTransformerToBack);
    }

    public void setZIndexTransformerCommon(ZIndexTransformer zIndexTransformerCommon) {
        mAnimationHelper.setZIndexTransformerCommon(zIndexTransformerCommon);
    }

    public void setAnimInterpolator(Interpolator animInterpolator) {
        mAnimationHelper.setAnimInterpolator(animInterpolator);
    }

    public void setAnimType(int animType) {
        Log.d(Consts.TAG, " ===> setAnimType    animType = " + animType);
        mAnimationHelper.setAnimType(animType);
    }

    void setTransformerAnimAdd(AnimationTransformer transformerAnimAdd) {
        mAnimationHelper.setTransformerAnimAdd(transformerAnimAdd);
    }

    void setTransformerAnimRemove(AnimationTransformer transformerAnimRemove) {
        mAnimationHelper.setTransformerAnimRemove(transformerAnimRemove);
    }

    void setAnimAddRemoveInterpolator(Interpolator animAddRemoveInterpolator) {
        mAnimationHelper.setAnimAddRemoveInterpolator(animAddRemoveInterpolator);
    }

    public void setCardSizeRatio(float cardSizeRatio) {
        this.mCardRatio = cardSizeRatio;
        setCardSize(false);
    }

    public boolean isAnimating() {
        return mAnimationHelper.isAnimating();
    }

    public void setCardAnimationListener(CardAnimationListener cardAnimationListener) {
        mAnimationHelper.setCardAnimationListener(cardAnimationListener);
    }

    public static interface CardAnimationListener {
        void onAnimationStart();

        void onAnimationEnd();
    }
}
