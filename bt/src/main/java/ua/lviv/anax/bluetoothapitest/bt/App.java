package ua.lviv.anax.bluetoothapitest.bt;

import android.app.Application;
import android.os.Handler;
import android.widget.Toast;

/**
 * @author yurii.ostrovskyi
 */
public class App extends Application {

    private static App sSelf;

    @Override
    public void onCreate() {
        super.onCreate();

        sSelf = this;
    }

    public static App self() {
        return sSelf;
    }

    public void showToast(final CharSequence message){
		Toast.makeText(App.this, message, Toast.LENGTH_SHORT).show();
    }
}
