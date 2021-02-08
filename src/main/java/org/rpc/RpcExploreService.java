package org.rpc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jiankang Li
 * @ClassName RpcExploreService.java
 * @Description TODO
 * @createTime 2021年02月08日 11:03:00
 */
public class RpcExploreService {
    private Map<String, Object> objectMap = new HashMap<>();

    public void explore(String className, Object object) {
        objectMap.put(className, object);
    }

    public Object invoke(MethodParameter methodParameter) {
        Object object = objectMap.get(methodParameter.getClassName());
        if (object == null) {
            throw new RuntimeException("无对应执行类:" + methodParameter.getClassName());
        }
        Method method = null;
        try {
            method = object.getClass().getMethod(methodParameter.getMethodName(), methodParameter.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("无对应执行方法:" + methodParameter.getClassName() + ", 方法:" + methodParameter.getMethodName());
        }

        try {
            Object result = method.invoke(object, methodParameter.getArguments());

            System.out.println(methodParameter);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("invoke方法执行失败:" + e.getMessage());
        }
    }
}
