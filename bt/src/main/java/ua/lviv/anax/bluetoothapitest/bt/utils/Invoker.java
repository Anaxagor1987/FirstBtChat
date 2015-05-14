package ua.lviv.anax.bluetoothapitest.bt.utils;

import android.os.Handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rostyslav.lesovyi
 */
public class Invoker<T> {

    public final T object;
    public final List<T> callbacks = new ArrayList<T>();

    private Invoker(T object) {
        this.object = object;
    }

    public static <T> Invoker<T> create(Class<T> cls) {
        HandlerInvoker<T> handler = new HandlerInvoker<T>();
        Invoker<T> invoker = new Invoker<T>(cls.cast(Proxy.newProxyInstance(cls.getClassLoader(), new Class<?>[]{ cls }, handler)));
        handler.mInvoker = invoker;
        return invoker;
    }

    static class HandlerInvoker<T> implements InvocationHandler {
        private Invoker<T> mInvoker;
        private Handler mHandler = new Handler();

        @Override
        public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (Object callback : mInvoker.callbacks.toArray()) {
                        try {
                            method.invoke(callback, args);
                        } catch (Exception ignored) {
                        }
                    }
                }
            });
            return null;
        }
    }
}
