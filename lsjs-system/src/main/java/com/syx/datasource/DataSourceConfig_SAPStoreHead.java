package com.syx.datasource;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @author 宋远欣
 * @date 2022/5/18
 **/
@Configuration
@MapperScan(basePackages = "com.syx.mapper.SAPStoreHead", sqlSessionTemplateRef = "SAPStoreHeadSqlSessionTemplate")
public class DataSourceConfig_SAPStoreHead {

    @Bean(name = "SAPStoreHeadDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.sapstoreheaddb")
    public DataSource testDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "SAPStoreHeadSqlSessionFactory")
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("SAPStoreHeadDataSource") DataSource dataSource) throws Exception{
        MybatisSqlSessionFactoryBean sessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mappers/SAPStoreHead/*Mapper.xml"));

        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setLogImpl(StdOutImpl.class);
        sessionFactoryBean.setConfiguration(configuration);

        return sessionFactoryBean.getObject();
    }

    @Bean(name = "SAPStoreHeadTransactionManager")
    public DataSourceTransactionManager testTransactionManager(@Qualifier("SAPStoreHeadDataSource") DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "SAPStoreHeadSqlSessionTemplate")
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("SAPStoreHeadSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception{
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
