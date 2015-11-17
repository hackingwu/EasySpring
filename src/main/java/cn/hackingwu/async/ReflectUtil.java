package cn.hackingwu.async;

/**
 * @author hackingwu.
 * @since 2015/9/18
 */
public class ReflectUtil {

    public static boolean isCasted(Class beCasted,Class toCast){
        if (isPrimitive(beCasted) || isPrimitive(toCast)){
            return beCasted.getSimpleName().subSequence(0,3).toString().equalsIgnoreCase(toCast.getSimpleName().substring(0,3).toString());
        }else{
            return beCasted.isAssignableFrom(toCast);
        }
    }

    public static boolean isPrimitive(Class clazz) {
        return clazz.isPrimitive()
                || clazz.equals(Integer.class)
                || clazz.equals(Long.class)
                || clazz.equals(Short.class)
                || clazz.equals(Double.class)
                || clazz.equals(Boolean.class)
                || clazz.equals(Character.class)
                || clazz.equals(Float.class)
                || clazz.equals(Byte.class);
    }


}
