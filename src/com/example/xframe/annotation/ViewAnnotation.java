package com.example.xframe.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.view.View;
import android.view.View.OnClickListener;

public class ViewAnnotation {
    
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    public static @interface ViewInject {
        public int id();

        public String clickListener() default "";
    }
    
    public static void bind(View view, final Object viewHolder) {
        try {
            _bind(view, viewHolder);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private static void _bind(View view, final Object viewHolder)
            throws IllegalArgumentException, IllegalAccessException,
            NoSuchMethodException {

        Class<?> clazz = viewHolder.getClass();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            ViewInject ann = field.getAnnotation(ViewInject.class);
            if (ann == null)
                continue;

            View v = view.findViewById(ann.id());
            field.setAccessible(true);
            field.set(viewHolder, v);

            String clickListener = ann.clickListener();
            if (!clickListener.equals("")) {
                final Method method = clazz.getDeclaredMethod(clickListener, (Class<?>[]) null);
                method.setAccessible(true);
                v.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            method.invoke(viewHolder, (Object[]) null);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
}
