package ru.cproject.vesnaandroid.helpers;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.qozix.tileview.graphics.BitmapProvider;
import com.qozix.tileview.tiles.Tile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;

import ru.cproject.vesnaandroid.activities.map.MapActivity;

/**
 * Created by Владислав on 27.12.16.
 */

public class ExternalStorageBitmapProviderAssets implements BitmapProvider {

    private static final BitmapFactory.Options OPTIONS = new BitmapFactory.Options();

    static {
        OPTIONS.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    @Override
    public Bitmap getBitmap(Tile tile, Context context) {
        Object data = tile.getData();
        if( data instanceof String ) {
            String unformattedFileName = (String) tile.getData();
            String formattedFileName = String.format( Locale.US, unformattedFileName, tile.getColumn(), tile.getRow());
            try {
                File file = new File(formattedFileName);
                InputStream inputStream = new FileInputStream(file);
                if( inputStream != null ) {
                    try {
                        return BitmapFactory.decodeStream( inputStream, null, OPTIONS );
                    } catch( OutOfMemoryError | Exception e ) {
                        // this is probably an out of memory error - you can try sleeping (this method won't be called in the UI thread) or try again (or give up)
                    }
                }
            } catch( Exception e ) {
                // this is probably an IOException, meaning the file can't be found
            }
        }
        return null;
    }
}
