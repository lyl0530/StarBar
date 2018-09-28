package com.lyl.startest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
* Created by liuyaliang on 18-3-19.
*/

public class StarBar extends View {
    private int starDistance = 0;       //星星间距
    private int starCount = 5;          //星星个数
    private int starSize;               //星星高度大小，星星一般正方形，宽度等于高度
    private float starMark = 0.0F;      //评分星星
    private Bitmap starFillBitmap;      //亮星星 - Bitmap
    private Drawable starEmptyDrawable; //暗星星 - Drawable
    private OnStarChangeListener onStarChangeListener;//监听星星变化接口
    private Paint paint;                //绘制亮星星的画笔
    private boolean integerMark = false;
    private static final boolean DEFAULT_INTEGERMARK = false;
    private boolean displayR2l =  false;//亮星星从右开始显示
    private static final boolean DEFAULT_DISPLAYR2L = false;

    public StarBar(Context context){
        super(context);
    }
    public StarBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public StarBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public StarBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 初始化UI组件
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs){
        setClickable(false);

        //在 Android 自定义 View 的时候，需要使用 TypedArray 来获取 XML layout 中的属性值
        //使用完之后，需要调用 recycle() 方法将 TypedArray 回收。
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.Rating);
        this.starDistance = (int) mTypedArray.getDimension(R.styleable.Rating_starDistance, 0);
        this.starSize = (int) mTypedArray.getDimension(R.styleable.Rating_starSize, 20);
        this.starCount = mTypedArray.getInteger(R.styleable.Rating_starCount, 5);
        this.starEmptyDrawable = mTypedArray.getDrawable(R.styleable.Rating_starEmpty);
        this.starFillBitmap =  drawableToBitmap(mTypedArray.getDrawable(R.styleable.Rating_starFill));
        this.integerMark = mTypedArray.getBoolean(R.styleable.Rating_integerMark, DEFAULT_INTEGERMARK);
        this.displayR2l = mTypedArray.getBoolean(R.styleable.Rating_displayR2l, DEFAULT_DISPLAYR2L);
        mTypedArray.recycle();

        paint = new Paint();//准备画笔
        paint.setAntiAlias(true);//抗锯齿
        //把画笔的形状设置为亮星星的形状
        paint.setShader(new BitmapShader(starFillBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
    }

    public void setStarCount(int starCount){
        this.starCount = starCount;
    }

    public void setStarSize(int starSize) {
        this.starSize = starSize;
    }

    public void setStarDistance(int starDistance){
        this.starDistance = starDistance;
    }
    /**
     * 设置是否需要整数评分
     * @param integerMark
     */
    public void setIntegerMark(boolean integerMark){
        this.integerMark = integerMark;
    }

    /**
     * 设置显示的星星的分数
     *
     * @param mark
     */
    public void setStarMark(float mark){
        if (integerMark) {
            starMark = (int) Math.ceil(mark);
        }else {
            starMark = Math.round(mark * 10) * 1.0f / 10;
        }
        if (this.onStarChangeListener != null) {
            this.onStarChangeListener.onStarChange(starMark);  //调用监听接口
        }


        invalidate(); //触发View的重新绘制 onDraw，在主线程使用该方法
        //postInvalidate(); //主线程、子线程中都可使用该方法，也是用于重绘控件
    }

    /**
     * 获取显示星星的数目
     *
     * @return starMark
     */
    public float getStarMark(){
        return starMark;
    }


    /**
     * 定义星星点击的监听接口
     */
    public interface OnStarChangeListener {
        void onStarChange(float mark);
    }

    /**
     * 设置监听
     * @param onStarChangeListener
     */
    public void setOnStarChangeListener(OnStarChangeListener onStarChangeListener){
        this.onStarChangeListener = onStarChangeListener;
    }
/**
 * View的生命周期
 * 构造View() --> onFinishInflate() --> onAttachedToWindow() -->
 * onMeasure() --> onSizeChanged() --> onLayout() --> onDraw() --> onDetackedFromWindow()
 */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(starSize * starCount + starDistance * (starCount - 1), starSize);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (starFillBitmap == null || starEmptyDrawable == null) {
            return;
        }

        // 显示暗星星
        for (int i = 0;i < starCount;i++) {
            //设置暗星星的绘画范围
            starEmptyDrawable.setBounds((starDistance + starSize) * i, 0, (starDistance + starSize) * i + starSize, starSize);
            starEmptyDrawable.draw(canvas);//将自己画到画布上去
        }
        if (displayR2l) {
            // 显示亮星星 - 星星从右边显示
            if (integerMark && (starMark >= 1)) {
                canvas.translate((starDistance + starSize) * (starCount - 1), 0);
                canvas.drawRect(0, 0, starSize, starSize, paint);//画布用画笔把亮星星画出来
                for (int i = 0; i < (starMark-1); i++) {
                    canvas.translate((starDistance + starSize) * (-1), 0);
                    canvas.drawRect(0, 0, starSize, starSize, paint);
                }
            }
        } else {
            // 显示亮星星 - 星星从左边边显示
            if (starMark > 1) {
                canvas.drawRect(0, 0, starSize, starSize, paint);//画布用画笔把亮星星画出来
                if (starMark - (int) (starMark) == 0) {
                    for (int i = 1; i < starMark; i++) {
                        canvas.translate(starDistance + starSize, 0);
                        canvas.drawRect(0, 0, starSize, starSize, paint);
                    }
                } else {
                    for (int i = 1; i < starMark - 1; i++) {
                        canvas.translate(starDistance + starSize, 0);
                        canvas.drawRect(0, 0, starSize, starSize, paint);
                    }
                    canvas.translate(starDistance + starSize, 0);
                    canvas.drawRect(0, 0, starSize * (Math.round((starMark - (int) (starMark)) * 10) * 1.0f / 10), starSize, paint);
                }
            } else {
                canvas.drawRect(0, 0, starSize * starMark, starSize, paint);
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
//设置星星不可点击
        int x = (int) event.getX();
        if (x < 0) x = 0;
        if (x > getMeasuredWidth()) x = getMeasuredWidth();
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN: {
                setStarMark(x*1.0f / (getMeasuredWidth()*1.0f/starCount));
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                setStarMark(x*1.0f / (getMeasuredWidth()*1.0f/starCount));
                break;
            }
            case MotionEvent.ACTION_UP: {
                break;
            }
        }
        invalidate();
        return super.onTouchEvent(event);
    }

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable)
    {
        if (drawable == null)return null;
        Bitmap bitmap = Bitmap.createBitmap(starSize, starSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, starSize, starSize);
        drawable.draw(canvas);
        return bitmap;
    }
}

