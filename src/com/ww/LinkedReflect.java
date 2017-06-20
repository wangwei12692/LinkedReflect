package com.ww;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by mx
 */
public class LinkedReflect {
    private static Object mObj;
    private static Builder mBuilder;
    private static Class mClazz;
    private static HashMap<Class, Class> sMap;

    static {
        sMap = new HashMap<>();
        sMap.put(Integer.class, Integer.TYPE);
        sMap.put(Short.class, Short.TYPE);
        sMap.put(Long.class, Long.TYPE);
        sMap.put(Byte.class, Byte.TYPE);
        sMap.put(Float.class, Float.TYPE);
        sMap.put(Double.class, Double.TYPE);
        sMap.put(Boolean.class, Boolean.TYPE);
    }

    public static Builder with(Object obj) {
        clear();
        mObj = obj;
        if (obj != null) {
            mClazz = obj.getClass();
        }
        mBuilder = new Builder();
        return mBuilder;
    }

    private static void clazz(Class clazz) {
        mClazz = clazz;
    }

    private static void parent() {
        mClazz = mClazz.getSuperclass();
    }

    private static void field(String name) {
        Field field = null;
        try {
            if (mClazz == null)
                throw new IllegalArgumentException("try to get field:" + name + "on a null class");
            field = mClazz.getDeclaredField(name);
            field.setAccessible(true);
            //  update
            mObj = field.get(mObj);
            mClazz = mObj == null ? null : mObj.getClass();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }


    /**
     * 基本类型形参非包装类型 byte shrot int long float double boolean
     * 使用包装类型fun(Integer a,Boolean b) 考虑使用{@link LinkedReflect#method(String name, Object[] parmas, Class... parmasType)}
     *
     * @param name
     * @param parmas
     */
    private static void method(String name, Object... parmas) {
        Class[] prepareType = prepareTypes(parmas);
        method(name, parmas, prepareType);
    }

    /**
     * 基本类型的形参均为包装类型
     * 如果形参均为 fun(int a,boolean b)等非包装类型
     * 可以使用 {@link LinkedReflect#method(String name, Object[] parmas)}
     *
     * @param name
     * @param parmas
     */
    private static void method(String name, Object[] parmas, Class... parmasType) {

        try {
            Method method;
            if (mClazz == null)
                throw new IllegalArgumentException("try to get method:" + name + "(" + stringTypes(parmasType) + ") on a null class");
            if (null == parmasType || parmasType.length == 0) {
                method = mClazz.getDeclaredMethod(name);
            } else {
                method = mClazz.getDeclaredMethod(name, parmasType);
            }
            method.setAccessible(true);
            //  update
            mObj = method.invoke(mObj, parmas);
            mClazz = mObj == null ? null : mObj.getClass();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static <T> void peek(Value<T> value) {
        value.obj = (T) mObj;
    }


    private static Object get() {
        Object temp = mObj;
        clear();
        return temp;
    }
    private static void clear() {
        mObj = null;
        mBuilder = null;
        mClazz = null;
    }

    //for peek use
    public static class Value<T> {
        public T obj;

        private Value() {
        }

        public static Value obtain() {
            return new Value();
        }

        @Override
        public String toString() {
            throw new IllegalArgumentException("use Value.obj to get the peek value!");
        }
    }

    public final static class Builder {

        private Builder() {
        }

        public Builder clazz(Class clazz) {
            LinkedReflect.clazz(clazz);
            return this;
        }

        public Builder method(String name, Object[] parmas, Class... parmasType) {
            LinkedReflect.method(name, parmas, parmasType);
            return this;
        }

        public Builder method(String name, Object... parmas) {
            LinkedReflect.method(name, parmas);
            return this;
        }

        public Builder method(String name) {
            LinkedReflect.method(name);
            return this;
        }

        public <T> Builder peek(Value<T> value) {
            LinkedReflect.peek(value);
            return this;
        }
        public <T> Builder parent() {
            LinkedReflect.parent();
            return this;
        }

        public Object get() {
            return LinkedReflect.get();

        }

        public Builder field(String name) {
            LinkedReflect.field(name);
            return this;
        }

        public void clear() {
            LinkedReflect.clear();
        }
    }

    private static String stringTypes(Class[] parmasType) {
        String types = "";
        for (int i = 0; i < parmasType.length; i++) {
            if (parmasType[i] == null) {
                throw new IllegalArgumentException("params[" + i + "] can't be null");
            }
            types += parmasType[i].getSimpleName();
            if (i != parmasType.length - 1)
                types += " ,";
        }
        return types;
    }

    private static Class[] prepareTypes(Object[] params) {
        if (params == null) return new Class[]{};
        Class[] parmasType = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            Class<?> clazz;
            if (params[i] == null) {
                parmasType[i] = null;
                throw new IllegalArgumentException("params[" + i + "] can't be null");
            }
            clazz = params[i].getClass();
            parmasType[i] = sMap.get(clazz) == null ? clazz : sMap.get(clazz);
        }
        return parmasType;
    }
}
