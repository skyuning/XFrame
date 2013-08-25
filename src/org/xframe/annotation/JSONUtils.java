package org.xframe.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.text.TextUtils;

public class JSONUtils {

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    public static @interface JSONDict {
        public String name();

        public String defVal() default "";

        public Class<?> type() default void.class;
    }
    
    public static JSONObject java2JsonObject(Object javaObject, JSONObject jsonObject) {
        Class<?> clazz = javaObject.getClass();
        List<Field> fields = recursiveGetFields(clazz, false);
        for (Field field : fields) {
            JSONDict ann = field.getAnnotation(JSONDict.class);
            if (null == ann || TextUtils.isEmpty(ann.name()))
                continue;

            try {
                jsonObject.put(ann.name(), field.get(javaObject));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public static <T> T json2JavaObject(JSONObject jsonObject, T javaObject) {
        Class<?> clazz = javaObject.getClass();
        List<Field> fields = recursiveGetFields(clazz, false);
        for (Field field : fields) {
            JSONDict ann = field.getAnnotation(JSONDict.class);
            if (null == ann || TextUtils.isEmpty(ann.name()))
                continue;

            Object jsonValue = jsonObject.opt(ann.name());
            try {
                setJson2Field(javaObject, field, ann, jsonValue);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return javaObject;
    }

    private static void setJson2Field(Object object, Field field, JSONDict ann,
            Object value) throws JSONException, InstantiationException,
            IllegalAccessException {

        // check the value
        if (null == value) {
            String defVal = ann.defVal();
            if (TextUtils.isEmpty(defVal))
                return;
            value = new JSONTokener(defVal).nextValue();
            if (JSONObject.NULL == value)
                return;
        }

        // process the value
        if (value instanceof Double) // because java float and double value are
                                     // confusing
            value = Float.valueOf(value.toString());
        else if (value instanceof JSONObject) {
            value = json2JavaObject((JSONObject) value, field.getType()
                    .newInstance());
        } else if (value instanceof JSONArray) {
            JSONArray jaValue = (JSONArray) value;
            Class<?> itemType = ann.type();
            List<Object> list = new ArrayList<Object>();
            for (int i = 0; i < jaValue.length(); i++) {
                Object item = json2JavaObject(jaValue.getJSONObject(i),
                        itemType.newInstance());
                list.add(item);
            }
            value = list;
        }

        // set the value to field
        if (null != value) {
            field.setAccessible(true);
            field.set(object, value);
        }
    }

    private static List<Field> recursiveGetFields(Class<?> clazz,
            boolean includeStatic) {
        List<Field> fields = new ArrayList<Field>();
        for (Field field : clazz.getDeclaredFields()) {
            if (includeStatic || !Modifier.isStatic(field.getModifiers()))
                fields.add(field);
        }
        if ((clazz != Object.class) && (clazz.getSuperclass() != null)) {
            fields.addAll(recursiveGetFields(clazz.getSuperclass(),
                    includeStatic));
        }
        return fields;
    }
}