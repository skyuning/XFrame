package org.xframe.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

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

        Field[] fields = recursiveGetFields(clazz, false).toArray(new Field[0]);
        for (Field field : fields) {
            ViewInject ann = field.getAnnotation(ViewInject.class);
            if (ann == null)
                continue;

            View v = view.findViewById(ann.id());
            field.setAccessible(true);
            field.set(viewHolder, v);

            String clickListener = ann.clickListener();
            if (! clickListener.equals("")) {
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
            
            if (v instanceof TextView) {
                final TextView tv = (TextView) v;
                tv.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.toString().equals("null"))
                            tv.setText("");
                    }
                    
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    
                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            }
        }
    }

    private static List<Field> recursiveGetFields(Class<?> clazz, boolean includeStatic) {
        List<Field> fields = new ArrayList<Field>();
        for (Field field : clazz.getDeclaredFields()) {
            if (includeStatic || ! Modifier.isStatic(field.getModifiers()))
                fields.add(field);
        }
        if ((clazz != Object.class) && (clazz.getSuperclass() != null)) {
            fields.addAll(recursiveGetFields(clazz.getSuperclass(),
                    includeStatic));
        }
        return fields;
    }
}
