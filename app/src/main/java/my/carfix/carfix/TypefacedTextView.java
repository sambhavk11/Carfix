package my.carfix.carfix;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TypefacedTextView extends TextView
{
	public TypefacedTextView(Context context)
    {
		super(context);
	}

	public TypefacedTextView(Context context, AttributeSet attrs)
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

	public TypefacedTextView(Context context, AttributeSet attrs, int defStyleAttr)
    {
		super(context, attrs, defStyleAttr);
	}
}
