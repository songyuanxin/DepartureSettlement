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
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @author 宋远欣
 * @date 2022/5/18
 *
 * 创建多数据源的过程是：首先创建DataSource，
 * 注入到SqlSessionFactory中，再创建事务，
 * 将SqlSessionFactory注入到创建的SqlSessionTemplate中，
 * 最后将SqlSessionTemplate注入到对应的Mapper包路径下。
 * 其中需要指定分库的Mapper包路径。
 **/
@Configuration
@MapperScan(basePackages = "com.syx.mapper.lsjs", sqlSessionTemplateRef = "lsjsSqlSessionTemplate")
public class DataSourceConfig_lsjs {

    @Bean(name = "LsjsDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.lsjsdb")
    @Primary
    public DataSource testDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "LsjsSqlSessionFactory")
    @Primary
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("LsjsDataSource") DataSource dataSource) throws Exception{
        MybatisSqlSessionFactoryBean sessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mappers/lsjs/*Mapper.xml"));

        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setLogImpl(StdOutImpl.class);
        sessionFactoryBean.setConfiguration(configuration);

        return sessionFactoryBean.getObject();
    }

    @Bean(name = "LsjsTransactionManager")
    @Primary
    public DataSourceTransactionManager testTransactionManager(@Qualifier("LsjsDataSource") DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "lsjsSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("LsjsSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception{
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
