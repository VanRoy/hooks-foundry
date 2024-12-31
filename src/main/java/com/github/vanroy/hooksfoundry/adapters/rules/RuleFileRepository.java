package com.github.vanroy.hooksfoundry.adapters.rules;

import com.github.vanroy.hooksfoundry.domain.models.Rule;
import com.github.vanroy.hooksfoundry.domain.ports.RuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.Yaml;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class RuleFileRepository implements RuleRepository {

    private final Yaml y = new Yaml();
    private final ResourceLoader resourceLoader;

    @Override
    @SneakyThrows
    public Collection<Rule> findAll() {

        var resource = resourceLoader.getResource("classpath:rules/orders.yaml");

        var rule = y.loadAs(resource.getInputStream(), RuleEntity.class);

        return Stream.of(toRule(rule)).toList();
    }

    private Rule toRule(RuleEntity entity) {
        return Rule.builder()
                .id(UUID.randomUUID())
                .tenantIdPath(entity.getTenantIdPath())
                .eventType(entity.getEventType())
                .eventTypePath(entity.getEventTypePath())
                .pattern(entity.getPattern())
                .transformer(entity.getTransformer() == null ? null :
                        Rule.Transformer.builder()
                                .variables(entity.getTransformer().getVariables())
                                .template(entity.getTransformer().getTemplate())
                                .build()
                )
                .build();
    }
}
