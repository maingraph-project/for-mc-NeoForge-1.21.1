package ltd.opens.mg.mc.core.blueprint.engine;

import java.util.UUID;

/**
 * 转换引擎 - 负责蓝图系统中各种数据类型的安全转换
 */
public class TypeConverter {

    public static String toString(Object value) {
        if (value == null) return "";
        return String.valueOf(value);
    }

    public static double toDouble(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Number) return ((Number) value).doubleValue();
        if (value instanceof Boolean) return (Boolean) value ? 1.0 : 0.0;
        try {
            String s = String.valueOf(value);
            if (s.isEmpty()) return 0.0;
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public static boolean toBoolean(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).doubleValue() != 0;
        String s = String.valueOf(value).toLowerCase();
        return s.equals("true") || s.equals("1") || s.equals("yes");
    }

    public static int toInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Number) return ((Number) value).intValue();
        return (int) Math.round(toDouble(value));
    }

    public static Object cast(Object value, String targetType) {
        if (value == null) return null;
        if (targetType == null) return value;
        
        targetType = targetType.toUpperCase();
        
        switch (targetType) {
            case "STRING":
                return toString(value);
                
            case "FLOAT":
                return toDouble(value);
                
            case "BOOLEAN":
                return toBoolean(value);
                
            case "INT":
                return toInt(value);
                
            case "UUID":
                try {
                    String s = toString(value);
                    if (s.isEmpty()) return "";
                    return UUID.fromString(s).toString();
                } catch (Exception e) {
                    return "";
                }

            case "LIST":
                return toString(value);

            default:
                return value;
        }
    }
}
