package com.github.vanroy.hooksfoundry.domain.ports;

import com.github.vanroy.hooksfoundry.domain.models.Rule;

import java.util.Collection;

public interface RuleRepository {

    Collection<Rule> findAll();
}
