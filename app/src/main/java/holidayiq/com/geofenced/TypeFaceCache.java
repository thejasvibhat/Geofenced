package holidayiq.com.geofenced;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * Created by Manoj on 23/10/2016.
 */

public final class TypeFaceCache {
    private static TypeFaceCache mTypefaceCacheinstance;
    private Context mContext;
    private HashMap<String, Typeface> mFontMap = new HashMap<String, Typeface>();

    /**
     * Constructor to set context
     *
     * @param context
     */
    private TypeFaceCache(Context context) {
        this.mContext = context;
    }

    /**
     * Creates the instance only one time and returns the same instance whenever
     * this method gets called
     *
     * @param context
     * @return instance
     */
    public static TypeFaceCache getInstance(Context context) {
        synchronized (TypeFaceCache.class) {
            if (mTypefaceCacheinstance == null)
                mTypefaceCacheinstance = new TypeFaceCache(context);
            return mTypefaceCacheinstance;
        }
    }

    /**
     * Returns the cached typeface (if cached); otherwise cache it by read the
     * ttf from the asset folder
     *
     * @param font
     * @return typeface
     */
    public Typeface getFontTypeface(String font) {
        Typeface typeface = mFontMap.get(font);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/" + font);

            mFontMap.put(font, typeface);
        }
        return typeface;
    }
}