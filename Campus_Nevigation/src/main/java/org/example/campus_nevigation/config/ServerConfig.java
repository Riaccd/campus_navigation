package org.example.campus_nevigation.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> containerCustomizer() {
        return factory -> {
            factory.addConnectorCustomizers(connector -> {//添加自定义配置到Tomcat连接器
                connector.setProperty("maxHttpHeaderSize", "65536");//设置请求头的最大大小
                connector.setMaxParameterCount(1000);//请求参数的最大数量为 1000
            });
        };
    }
}