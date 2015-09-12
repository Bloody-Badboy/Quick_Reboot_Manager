package com.bloody.badboy;

import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;

public class MaterialRipple extends RelativeLayout
{

    private int WIDTH;
    private int HEIGHT;
    private int frameRate = 25;
    private int rippleDuration = 350;
    private int rippleAlpha = 90;
    private Handler canvasHandler;
    private float radiusMax = 0;
    private boolean animationRunning = false;
    private int timer = 0;
    private int timerEmpty = 0;
    private int durationEmpty = -1;
    private float x = -1;
    private float y = -1;
    private int zoomDuration;
    private float zoomScale;
    private ScaleAnimation scaleAnimation;
    private Boolean hasToZoom;
    private Boolean isCentered;
    private Integer rippleType;
    private Paint paint;
    private Bitmap originBitmap;
    private int rippleColor;
    private int ripplePadding;
    private GestureDetector gestureDetector;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run()
		{
            invalidate();
        }
    };

    private OnRippleCompleteListener onCompletionListener;


    public MaterialRipple(Context context)
	{
        super(context);
    }

    public MaterialRipple(Context context, AttributeSet attrs)
	{
        super(context, attrs);
        init(context, attrs);
    }

    public MaterialRipple(Context context, AttributeSet attrs, int defStyle)
	{
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(final Context context, final AttributeSet attrs)
	{
        if (isInEditMode())
            return;
		rippleColor = (setColorResId("ripplecolor"));
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaterialRipple);
        rippleType = typedArray.getInt(R.styleable.MaterialRipple_ripple_type, 0);
        hasToZoom = typedArray.getBoolean(R.styleable.MaterialRipple_ripple_zoom, false);
        isCentered = typedArray.getBoolean(R.styleable.MaterialRipple_ripple_centered, false);
        rippleDuration = typedArray.getInteger(R.styleable.MaterialRipple_ripple_rippleDuration, rippleDuration);
        frameRate = typedArray.getInteger(R.styleable.MaterialRipple_ripple_framerate, frameRate);
        rippleAlpha = typedArray.getInteger(R.styleable.MaterialRipple_ripple_alpha, rippleAlpha);
        ripplePadding = typedArray.getDimensionPixelSize(R.styleable.MaterialRipple_ripple_ripplePadding, 0);
        canvasHandler = new Handler();
        zoomScale = typedArray.getFloat(R.styleable.MaterialRipple_ripple_zoomScale, 1.03f);
        zoomDuration = typedArray.getInt(R.styleable.MaterialRipple_ripple_zoomDuration, 200);
        typedArray.recycle();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(rippleColor);
        paint.setAlpha(rippleAlpha);
        this.setWillNotDraw(false);

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
				@Override
				public void onLongPress(MotionEvent event)
				{
					super.onLongPress(event);
					animateRipple(event);
					sendClickEvent(true);
				}

				@Override
				public boolean onSingleTapConfirmed(MotionEvent e)
				{
					return true;
				}

				@Override
				public boolean onSingleTapUp(MotionEvent e)
				{
					return true;
				}
			});

        this.setDrawingCacheEnabled(true);
        this.setClickable(true);
    }

    @Override
    public void draw(Canvas canvas)
	{
        super.draw(canvas);
        if (animationRunning)
		{
            if (rippleDuration <= timer * frameRate)
			{
                animationRunning = false;
                timer = 0;
                durationEmpty = -1;
                timerEmpty = 0;
                canvas.restore();
                invalidate();
                if (onCompletionListener != null) onCompletionListener.onComplete(this);
                return;
            }
			else
                canvasHandler.postDelayed(runnable, frameRate);

            if (timer == 0)
                canvas.save();


            canvas.drawCircle(x, y, (radiusMax * (((float) timer * frameRate) / rippleDuration)), paint);

            paint.setColor(Color.parseColor("#ffff4444"));

            if (rippleType == 1 && originBitmap != null && (((float) timer * frameRate) / rippleDuration) > 0.4f)
			{
                if (durationEmpty == -1)
                    durationEmpty = rippleDuration - timer * frameRate;

                timerEmpty++;
                final Bitmap tmpBitmap = getCircleBitmap((int) ((radiusMax) * (((float) timerEmpty * frameRate) / (durationEmpty))));
                canvas.drawBitmap(tmpBitmap, 0, 0, paint);
                tmpBitmap.recycle();
            }

            paint.setColor(rippleColor);

            if (rippleType == 1)
			{
                if ((((float) timer * frameRate) / rippleDuration) > 0.6f)
                    paint.setAlpha((int) (rippleAlpha - ((rippleAlpha) * (((float) timerEmpty * frameRate) / (durationEmpty)))));
                else
                    paint.setAlpha(rippleAlpha);
            }
            else
                paint.setAlpha((int) (rippleAlpha - ((rippleAlpha) * (((float) timer * frameRate) / rippleDuration))));

            timer++;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
        super.onSizeChanged(w, h, oldw, oldh);
        WIDTH = w;
        HEIGHT = h;

        scaleAnimation = new ScaleAnimation(1.0f, zoomScale, 1.0f, zoomScale, w / 2, h / 2);
        scaleAnimation.setDuration(zoomDuration);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setRepeatCount(1);
    }

    public void animateRipple(MotionEvent event)
	{
        createAnimation(event.getX(), event.getY());
    }

    public void animateRipple(final float x, final float y)
	{
        createAnimation(x, y);
    }

    private void createAnimation(final float x, final float y)
	{
        if (this.isEnabled() && !animationRunning)
		{
            if (hasToZoom)
                this.startAnimation(scaleAnimation);

            radiusMax = Math.max(WIDTH, HEIGHT);

            if (rippleType != 2)
                radiusMax /= 2;

            radiusMax -= ripplePadding;

            if (isCentered || rippleType == 1)
			{
                this.x = getMeasuredWidth() / 2;
                this.y = getMeasuredHeight() / 2;
            }
			else
			{
                this.x = x;
                this.y = y;
            }

            animationRunning = true;

            if (rippleType == 1 && originBitmap == null)
                originBitmap = getDrawingCache(true);

            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
	{
        if (gestureDetector.onTouchEvent(event))
		{
            animateRipple(event);
            sendClickEvent(false);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
	{
        this.onTouchEvent(event);
        return super.onInterceptTouchEvent(event);
    }

    /**
     * Send a click event if parent view is a Listview instance
     *
     * @param isLongClick Is the event a long click ?
     */
    private void sendClickEvent(final Boolean isLongClick)
	{
        if (getParent() instanceof AdapterView)
		{
            final AdapterView adapterView = (AdapterView) getParent();
            final int position = adapterView.getPositionForView(this);
            final long id = adapterView.getItemIdAtPosition(position);
            if (isLongClick)
			{
                if (adapterView.getOnItemLongClickListener() != null)
                    adapterView.getOnItemLongClickListener().onItemLongClick(adapterView, this, position, id);
            }
			else
			{
                if (adapterView.getOnItemClickListener() != null)
                    adapterView.getOnItemClickListener().onItemClick(adapterView, this, position, id);
            }
        }
    }

    private Bitmap getCircleBitmap(final int radius)
	{
        final Bitmap output = Bitmap.createBitmap(originBitmap.getWidth(), originBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect((int)(x - radius), (int)(y - radius), (int)(x + radius), (int)(y + radius));

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(x, y, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(originBitmap, rect, rect, paint);

        return output;
    }

	public int setColorResId(String name)
	{
		return getContext().getResources().getIdentifier(name, "color", getContext().getPackageName());
	}

	public void setRippleColor(int rippleColor)
	{
		this.rippleColor = getResources().getColor(rippleColor);
	}

	public int getRippleColor()
	{
		return rippleColor;
	}

    public RippleType getRippleType()
    {
        return RippleType.values()[rippleType];
    }

    public void setRippleType(final RippleType rippleType)
    {
        this.rippleType = rippleType.ordinal();
    }

    public Boolean isCentered()
    {
        return isCentered;
    }

    public void setCentered(final Boolean isCentered)
    {
        this.isCentered = isCentered;
    }

    public int getRipplePadding()
    {
        return ripplePadding;
    }

    public void setRipplePadding(int ripplePadding)
    {
        this.ripplePadding = ripplePadding;
    }

    public Boolean isZooming()
    {
        return hasToZoom;
    }

    public void setZooming(Boolean hasToZoom)
    {
        this.hasToZoom = hasToZoom;
    }

    public float getZoomScale()
    {
        return zoomScale;
    }

    public void setZoomScale(float zoomScale)
    {
        this.zoomScale = zoomScale;
    }

    public int getZoomDuration()
    {
        return zoomDuration;
    }

    public void setZoomDuration(int zoomDuration)
    {
        this.zoomDuration = zoomDuration;
    }

    public int getRippleDuration()
    {
        return rippleDuration;
    }

    public void setRippleDuration(int rippleDuration)
    {
        this.rippleDuration = rippleDuration;
    }

    public int getFrameRate()
    {
        return frameRate;
    }

    public void setFrameRate(int frameRate)
    {
        this.frameRate = frameRate;
    }

    public int getRippleAlpha()
    {
        return rippleAlpha;
    }

    public void setRippleAlpha(int rippleAlpha)
    {
        this.rippleAlpha = rippleAlpha;
    }

    public void setOnRippleCompleteListener(OnRippleCompleteListener listener)
	{
        this.onCompletionListener = listener;
    }

    public interface OnRippleCompleteListener
	{
        void onComplete(MaterialRipple rippleView);
    }

    public enum RippleType
	{
        SIMPLE(0),
        DOUBLE(1),
        RECTANGLE(2);

        int type;

        RippleType(int type)
        {
            this.type = type;
        }
    }
}
