package my.carfix.carfix;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class KeyboardDetectorRelativeLayout extends RelativeLayout
{
    public interface IKeyboardChanged
    {
        void onKeyboardShown(int proposedheight);
        void onKeyboardHidden(int proposedheight);
    }

    private ArrayList<IKeyboardChanged> keyboardListener = new ArrayList<IKeyboardChanged>();

    public KeyboardDetectorRelativeLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public KeyboardDetectorRelativeLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public KeyboardDetectorRelativeLayout(Context context)
    {
        super(context);
    }

    public void addKeyboardStateChangedListener(IKeyboardChanged listener)
    {
        keyboardListener.add(listener);
    }

    public void removeKeyboardStateChangedListener(IKeyboardChanged listener)
    {
        keyboardListener.remove(listener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int proposedheight = MeasureSpec.getSize(heightMeasureSpec);
        final int actualHeight = getHeight();

        if (actualHeight > proposedheight) {
            notifyKeyboardShown(proposedheight);
        } else if (actualHeight < proposedheight) {
            notifyKeyboardHidden(proposedheight);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void notifyKeyboardHidden(int proposedheight)
    {
        for (IKeyboardChanged listener : keyboardListener)
        {
            listener.onKeyboardHidden(proposedheight);
        }
    }

    private void notifyKeyboardShown(int proposedheight)
    {
        for (IKeyboardChanged listener : keyboardListener)
        {
            listener.onKeyboardShown(proposedheight);
        }
    }
}
