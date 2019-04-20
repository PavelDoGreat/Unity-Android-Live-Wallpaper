package change.packagename.here;

import android.annotation.TargetApi;
import android.app.WallpaperColors;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowInsets;
import android.content.Context;

import com.unity3d.player.UnityPlayer;

class MyUnityPlayer extends UnityPlayer
{
    public MyUnityPlayer (Context var)
    {
        super(var);
    }
}

public class WallpaperActivity extends WallpaperService
{
    MyUnityPlayer mUnityPlayer;
    int mVisibleSurfaces = 0;

    @Override public void onCreate ()
    {
        super.onCreate();
        mUnityPlayer = new MyUnityPlayer(getApplicationContext());
    }

    @Override public void onDestroy ()
    {
        mUnityPlayer.quit();
        super.onDestroy();
    }

    @Override public Engine onCreateEngine ()
    {
        return new MyEngine();
    }

    @Override public void onLowMemory ()
    {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    @Override public void onTrimMemory(int level)
    {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL)
            mUnityPlayer.lowMemory();
    }

    @Override public void onConfigurationChanged (Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    void Log (String message)
    {
        Log.d("LiveWallpaper", message);
    }

    class MyEngine extends Engine
    {
        SurfaceHolder mHolder;
        boolean isPreview = false;

        @Override public void onCreate (SurfaceHolder holder)
        {
            Log("Create");
            super.onCreate(holder);
            isPreview = isPreview();
            setTouchEventsEnabled(true);
            setOffsetNotificationsEnabled(false);
            mUnityPlayer.UnitySendMessage("AppController", "TriggerIsWallpaper", isPreview ? "true" : "false");
        }

        @Override public void onDestroy ()
        {
            Log("Destroy");
            super.onDestroy();
        }

        @Override public void onApplyWindowInsets (WindowInsets insets)
        {
            super.onApplyWindowInsets(insets);
            int insetTop = 40;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                insetTop = insets.getStableInsetTop();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                insetTop += 140;
            mUnityPlayer.UnitySendMessage("AppController", "ReceiveWindowInset", Integer.toString(insetTop));
        }

        @TargetApi(Build.VERSION_CODES.O_MR1)
        @Override public WallpaperColors onComputeColors ()
        {
            Color color = Color.valueOf(Color.BLACK);
            return new WallpaperColors(color, color, color);
        }

        @Override public void onSurfaceCreated (SurfaceHolder holder)
        {
            Log("SurfaceCreated");
            super.onSurfaceCreated(holder);
            mHolder = holder;
        }

        @Override public void onSurfaceChanged (SurfaceHolder holder, int format, int width, int height)
        {
            Log("SurfaceChanged, width: " + width + ", height: " + height);
            super.onSurfaceChanged(holder, format, width, height);
            mHolder = holder;
            mUnityPlayer.displayChanged(0, mHolder.getSurface());
        }

        @Override public void onVisibilityChanged (boolean visible)
        {
            Log("VisibilityChanged, isPreview: " + isPreview + ", visible: " + visible);
            super.onVisibilityChanged(visible);

            if (visible)
            {
                mVisibleSurfaces++;
                if (mHolder != null)
                    mUnityPlayer.displayChanged(0, mHolder.getSurface());
                mUnityPlayer.windowFocusChanged(true);
                mUnityPlayer.resume();
                mUnityPlayer.UnitySendMessage("AppController", "TriggerVisible", isPreview ? "true" : "false");
                return;
            }

            mVisibleSurfaces--;
            mVisibleSurfaces = Math.max(mVisibleSurfaces, 0);
            if (mVisibleSurfaces == 0)
            {
                mUnityPlayer.displayChanged(0, null);
                mUnityPlayer.windowFocusChanged(false);
                mUnityPlayer.pause();
            }
        }

        @Override public void onSurfaceDestroyed (SurfaceHolder holder)
        {
            Log("SurfaceDestroyed");
            super.onSurfaceDestroyed(holder);
        }

        @Override public void onTouchEvent (MotionEvent event)
        {
            super.onTouchEvent(event);
            mUnityPlayer.injectEvent(event);
        }
    }
}