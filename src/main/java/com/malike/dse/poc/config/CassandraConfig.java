package com.malike.dse.poc.config;

import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.LatencyAwarePolicy;
import com.datastax.driver.core.policies.LoadBalancingPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy.ReplicaOrdering;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Configuration
@EnableCassandraRepositories
@Slf4j
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Value("${host}")
    private String HOST;
    private String keyspace = "sample_keyspace";


    @Override
    protected String getKeyspaceName() {
        return keyspace;
    }

    @Override
    protected List getKeyspaceCreations() {
        return Collections.singletonList(CreateKeyspaceSpecification
                .createKeyspace(keyspace).ifNotExists()
                .with(KeyspaceOption.DURABLE_WRITES, true)
                .withSimpleReplication());
    }


    @Override
    protected LoadBalancingPolicy getLoadBalancingPolicy() {
        return new TokenAwarePolicy(
                LatencyAwarePolicy.builder(
                        DCAwareRoundRobinPolicy.builder()
                                .withLocalDc("dc1")
                                .build()
                ).build(),
                ReplicaOrdering.NEUTRAL
        );
    }


    @Override
    protected List<String> getStartupScripts() {
        return Arrays.asList(
                "CREATE KEYSPACE IF NOT EXISTS " + keyspace + " WITH replication = {"
                        + " 'class': 'SimpleStrategy', "
                        + " 'replication_factor': '3' "
                        + "}"
                ,
                "CREATE TABLE IF NOT EXISTS " + keyspace
                        + ".attendees(id varchar PRIMARY KEY, name varchar) "
                        + "WITH default_time_to_live = 900;"

        );
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    protected String getContactPoints() {
        return HOST;
    }

    @Bean
    public CassandraClusterFactoryBean cluster() {
        CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
        cluster.setUsername(System.getenv("USERNAME"));
        cluster.setPassword(System.getenv("PASSWORD"));
        cluster.setContactPoints(getContactPoints());
        cluster.setStartupScripts(getStartupScripts());
        return cluster;
    }
}