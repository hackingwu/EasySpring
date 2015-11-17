package cn.hackingwu.async;


import org.springframework.aop.support.AopUtils;
import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @author hackingwu.
 * @since 2015/9/18
 */
public class AsyncTask implements Callable {

    private Class clazz;
    private Object o;
    private String methodName;
    private Object[] args;
    private Method targetMethod;
    private InvocationHandler invocationHandler = new InvocationHandler() {
        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            return method.invoke(o,objects);
        }
    };
    @Override
    public Object call() throws Exception {
        if (AopUtils.isJdkDynamicProxy(o)){
            try{
                return invocationHandler.invoke(o,targetMethod,args);
            }catch (Throwable e){
                throw new Exception(e);
            }
        }else{
            return targetMethod.invoke(o,args);
        }
    }

    public AsyncTask(Class clazz, Object o, String methodName, Object... args) throws Exception {
        this.clazz = clazz;
        this.o = o;
        this.methodName = methodName;
        this.args = args;

//        if (!clazz.isAssignableFrom(o.getClass())) throw new ClassCastException();
        Method[] methods = clazz.getMethods();
        Method targetMethod = null;
        for (Method method : methods){
            if (method.getName().equals(methodName)){
                Class[] parameterTypes = method.getParameterTypes();
                if (method.getParameterTypes().length == args.length){
                    boolean flag = true;
                    for (int i = 0 ;i < parameterTypes.length;i++){
                        if (args[i] != null && !ReflectUtil.isCasted(parameterTypes[i],args[i].getClass())){
                            flag = false;
                            break;
                        }
                    }
                    if (flag){
                        targetMethod = method;
                        break;
                    }
                }
            }
        }
        if (targetMethod == null) {
            throw new NoSuchMethodException();
        }
        this.targetMethod = targetMethod;
    }




}
