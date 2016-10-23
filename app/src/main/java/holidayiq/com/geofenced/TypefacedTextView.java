package holidayiq.com.geofenced;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Manoj on 23/10/2016.
 */

public class TypefacedTextView extends TextView {

    public TypefacedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomTextviewFont(context, attrs);
    }

    private void setCustomTextviewFont(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TypefacedTextView);
        String customTextviewFont = a.getString(R.styleable.TypefacedTextView_customTextviewFont);
        setCustomTextviewFont(context, customTextviewFont);
        a.recycle();
    }

    private boolean setCustomTextviewFont(Context context, String customTextviewFont) {
        if (customTextviewFont == null || customTextviewFont.isEmpty()) {
            setTypeface(TypeFaceCache.getInstance(context).getFontTypeface("Ubuntu-Regular.ttf"));
        } else {
            setTypeface(TypeFaceCache.getInstance(context).getFontTypeface(customTextviewFont));
        }
        return true;
    }

}
