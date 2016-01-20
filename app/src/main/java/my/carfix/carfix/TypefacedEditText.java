package my.carfix.carfix;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class TypefacedEditText extends EditText
{
    public TypefacedEditText(Context context)
    {
        super(context);
    }

    public TypefacedEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        if (attrs != null)
        {
            TypedArray styledAttrs = context.obtainStyledAttributes(attrs,R.styleable.TypefacedTextView);

            int typefaceCode = styledAttrs.getInt(R.styleable.TypefacedTextView_fontStyle, -1);

            styledAttrs.recycle();

            if (isInEditMode())
            {
                return;
            }

            Typeface typeface = TypefaceCache.get(context.getAssets(), typefaceCode);

            setTypeface(typeface);
        }
    }

    public TypefacedEditText(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }
}
