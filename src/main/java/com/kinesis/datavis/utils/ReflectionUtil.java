package com.kinesis.datavis.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by eugennekhai on 01/09/16.
 */
public class ReflectionUtil {
    public static String getValue(Object obj, String name) {
        try {
            Method method = obj.getClass().getMethod(name);
            return (String) method.invoke(obj);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static double getDValue(Object obj, String name) {
        try {
            Method method = obj.getClass().getMethod(name);
            return (Double) method.invoke(obj);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return 0d;
    }

    public static void setValue(Object javaBean, String name, String val) {
        try {
            BeanInfo bi = Introspector.getBeanInfo(javaBean.getClass());
            PropertyDescriptor pds[] = bi.getPropertyDescriptors();
            for (PropertyDescriptor pd : pds) {
                if (pd.getName().equals(name)) {
                    Method setter = pd.getWriteMethod();
                    if (setter != null) {
                        setter.invoke(javaBean, new Object[] {val} );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
