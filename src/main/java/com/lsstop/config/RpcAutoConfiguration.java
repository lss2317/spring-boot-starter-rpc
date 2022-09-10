package com.lsstop.config;

import com.lsstop.entity.URL;
import com.lsstop.enums.RpcErrorEnum;
import com.lsstop.exception.RpcException;
import com.lsstop.loadbalancer.LoadBalance;
import com.lsstop.properties.RpcConfigProperties;
import com.lsstop.proxy.RpcClientProxy;
import com.lsstop.registry.Consul.ConsulRegistry;
import com.lsstop.registry.Nacos.NacosRegistry;
import com.lsstop.registry.Redis.RedisRegistry;
import com.lsstop.registry.RegistryCenter;
import com.lsstop.serializable.CommonSerializer;
import com.lsstop.serializable.FastJsonSerializer;
import com.lsstop.serializable.JsonSerializer;
import com.lsstop.serializable.KryoSerializer;
import com.lsstop.spring.SpringBeanPostProcessor;
import com.lsstop.transport.netty.client.NettyClient;
import com.lsstop.transport.netty.server.NettyServer;
import com.lsstop.utils.ConsulUtil;
import com.lsstop.utils.NacosUtil;
import com.lsstop.utils.RedisUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author lss
 * @date 2022/09/07
 */
@Configuration
@EnableConfigurationProperties(RpcConfigProperties.class)
@ConditionalOnProperty(prefix = "spring.rpc", value = "enabled", matchIfMissing = true)
public class RpcAutoConfiguration implements ApplicationRunner {

    @Resource
    private RpcConfigProperties configProperties;

    @Bean
    public SpringBeanPostProcessor springBeanPostProcessor() {
        return new SpringBeanPostProcessor(new RpcClientProxy(nettyClient()));
    }

    public NettyClient nettyClient() {
        if (configProperties.getRegistryAddress() == null) {
            throw new RpcException("未设置注册中心地址: registryAddress");
        }
        LoadBalance balance = LoadBalance.getBalance(configProperties.getLoadbalancer());
        String[] split = configProperties.getRegistryAddress().toLowerCase().split("://");
        RegistryCenter center = null;
        if ("nacos".equals(split[0])) {
            NacosUtil.setNacos(split[1]);
            center = new NacosRegistry(balance);
        } else if ("consul".equals(split[0])) {
            String[] strings = split[1].split(":");
            ConsulUtil.setConsul(strings[0], Integer.parseInt(strings[1]));
            center = new ConsulRegistry(balance);
        } else if ("redis".equals(split[0])) {
            String[] strings = split[1].split(":");
            if (configProperties.getPassword() == null) {
                RedisUtil.setRedis(strings[0], Integer.parseInt(strings[1]));
            } else {
                RedisUtil.setRedis(strings[0], Integer.parseInt(strings[1]), configProperties.getPassword());
            }
            center = new RedisRegistry(balance);
        } else {
            throw new RpcException("注册中心设置错误");
        }
        CommonSerializer serializer = null;
        if ("fastjson".equals(configProperties.getSerializer())) {
            serializer = new FastJsonSerializer();
        } else if ("jackson".equals(configProperties.getSerializer())) {
            serializer = new JsonSerializer();
        } else if ("kryo".equals(configProperties.getSerializer())) {
            serializer = new KryoSerializer();
        } else {
            throw new RpcException("序列化方式设置错误: serializer");
        }
        if (configProperties.getRegistry()) {
            if (configProperties.getName() == null || configProperties.getName().trim().isEmpty()) {
                throw new RpcException(RpcErrorEnum.NOT_SETUP_SERVICE);
            }
            URL url = new URL(configProperties.getName(), configProperties.getHost(), configProperties.getPort(), configProperties.getWeight());
            center.registered(configProperties.getName(), url);
        }
        return new NettyClient(center, serializer);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //是否注册服务
        if (configProperties.getRegistry()) {
            NettyServer server = new NettyServer();
            server.start(configProperties.getHost(), configProperties.getPort());
        }
    }
}
