# spring-boot-starter-rpc

### 使用方式

**1、先打包`rpc-core`项目**

**项目地址：**[Rpc-Framework](https://github.com/lss2317/rpc-framework)

**2、下载项目，打包到本地，引入项目**

```xml
<dependency>
   <groupId>com.lsstop</groupId>
   <artifactId>spring-boot-starter-rpc</artifactId>
   <version>1.0-RELEASE</version>
</dependency>
```

### 服务端

**1、定义接口**

```java
public interface Hello {

    String hello(String name);
}
```

**2、实现接口，并标注`@RpcService`注解，表明这是一个rpc服务**

```java
@RpcService
public class HelloImpl implements Hello {
    @Override
    public String hello(String name) {
        return "测试：" + name;
    }
}
```

**3、配置**

```yaml
spring:
  rpc:
    registryAddress: nacos://127:0:0:1:8848  #注册中心地址
    name: DEMO  #注册服务名称
    port: 9000  #服务端口
    registry: true #是否注册服务，默认为true
    weight: 9 #权重
```

**4、启动服务，并标注`@RpcServiceScan`扫描服务，默认为启动类包**

```java
@RpcServiceScan
@SpringBootApplication
public class BootServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootServerApplication.class, args);
    }

}
```

### 消费端

**1、定义一个和服务端一样的接口，并标注`@RpcClient`注解，注解value为服务端注册名称**

```java
@RpcClient(value = "DEMO")
public interface Hello {
    String hello(String name);
}
```

**2、yaml配置**

```yaml
spring:
  rpc:
    registryAddress: nacos://127:0:0:1:8848 #注册中心地址
    registry: false  #是否注册服务，false为消费端
```

**3、引用时标注`@RpcResource`注解，即可调用**

```java
@RestController
public class TestController {

    @RpcResource
    private Hello hello;


    @GetMapping("hello")
    public String hello(String name){
        return hello.hello(name);
    }
}
```
### 配置参考

```properties
spring.rpc.registryAddress="nacos://127.0.0.1:8848"  #注册中心地址
spring.rpc.name="DEMO"                               #注册名称
spring.rpc.host="127.0.0.1"                          #启动ip
spring.rpc.port=8080                                 #启动端口
spring.rpc.weight=1                                  #权重
spring.rpc.serializer="kryo"                         #序列化方式
spring.rpc.loadbalancer="roundRobin"                 #负载均衡算法
spring.rpc.password="123456"                         #redis注册中心时redis密码
spring.rpc.registry=false                            #是否注册服务(默认为true，注册服务)
```

### 配置选择

- [x] serializer
  - fastjson
  - jackson
  - kryo

- [x] registryAddress
  - nacos://127.0.0.1:8848    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//nacos注册中心示例
  - redis://127.0.0.1:6379    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//redis注册中心示例(redis如有设置密码还需配置password)
  - consul://127.0.0.1:8500   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;//consul注册中心示例

- [x] loadbalancer
  - random &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  随机算法
  - roundRobin     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;   轮询
  - weightRandom    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;加权随机
  - weightRoundRobin    &nbsp;&nbsp;加权轮询

