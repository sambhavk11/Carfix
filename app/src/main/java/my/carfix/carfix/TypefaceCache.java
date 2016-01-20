package my.carfix.carfix;

import java.util.Hashtable;

import android.content.res.AssetManager;
import android.graphics.Typeface;

public class TypefaceCache
{
    private static final String proxima_nova_normal = "fonts/ProximaNova_black.ttf";

    private static final String proxima_nova_bold = "fonts/ProximaNova_bold.ttf";

    private static final String proxima_nova_bolditalic = "fonts/ProximaNova_bolditalic.ttf";

    private static final String proxima_nova_extrabold = "fonts/ProximaNova_extrabold.ttf";

    private static final String proxima_nova_regular = "fonts/ProximaNova_regular.ttf";

    private static final String proxima_nova_reg_italic = "fonts/ProximaNova_regitalic.ttf";

    private static final String proxima_nova_regular_italic = "fonts/ProximaNova_regularitalic.ttf";

    private static final String helvetica_neue_light = "fonts/HelveticaNeue-Light.ttf";

	private static final Hashtable<String, Typeface> CACHE = new Hashtable<String, Typeface>();

	public static Typeface get(AssetManager manager, int typefaceCode)
    {
		synchronized (CACHE)
        {
			String typefaceName = getTypefaceName(typefaceCode);

			if (! CACHE.containsKey(typefaceName))
            {
				Typeface t = Typeface.createFromAsset(manager, typefaceName);
				CACHE.put(typefaceName, t);
			}
			return CACHE.get(typefaceName);
		}
	}

	private static String getTypefaceName(int typefaceCode)
    {
		switch (typefaceCode)
        {
    		case 0:
	    		return proxima_nova_normal;

		    case 1:
			    return proxima_nova_bold;

            case 2:
			    return proxima_nova_bolditalic;

            case 3:
			    return proxima_nova_extrabold;

            case 4:
			    return proxima_nova_regular;

            case 5:
			    return proxima_nova_reg_italic;

            case 6:
			    return proxima_nova_regular_italic;

            case 7:
                return helvetica_neue_light;

		default:
			return null;
		}
	}
}
