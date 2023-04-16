package earth.terrarium.prometheus.common.utils;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeUtils {

    private static final Unsafe UNSAFE;

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to capture unsafe", e);
        }
    }

    public static boolean hasField(Object instance, String fieldName) {
        try {
            instance.getClass().getDeclaredField(fieldName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void setField(Object instance, String fieldName, Object value) {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            UNSAFE.putObject(instance, UNSAFE.objectFieldOffset(field), value);
        } catch (Exception e) {
            throw new RuntimeException("Unable to set field", e);
        }
    }
}
