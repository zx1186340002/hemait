package com.sky.config;


import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OssConfiguration {

    @Bean
    @ConditionalOnMissingBean  //表示当没有Bean的时候再去创建。
    public AliOssUtil  aliOssUtil(AliOssProperties aliOssProperties){
       log.info("开始上传");
        return new AliOssUtil(aliOssProperties.getEndpoint()
                ,aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());

    }
}
