package ysoserial;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class InvocationHandlerTests {
    public static void main(String[] args) throws Exception {
        // 被代理的对象
        Map map = new HashMap();
        // JDK 本身只支持动态代理接口
        // 创建 proxy object，参数为 ClassLoader、要代理的接口Class array、实际处理方法调用的 InvocationHandler
        Map proxy = (Map) Proxy.newProxyInstance(InvocationHandlerTests.class.getClassLoader(), new Class[]{Map.class}, new MyInvocationHandler(map));
        proxy.put("key", "value");
        proxy.get("key");
    }

    public static class MyInvocationHandler implements InvocationHandler {
        private Map map;

        public MyInvocationHandler(Map map) {
            this.map = map;
        }

        // 实际的方法调用都会变成调用 invoke 方法
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("method: " + method.getName() + " start");
            Object result = method.invoke(map, args);
            System.out.println("method: " + method.getName() + " finish");
            return result;
        }
    }
}
