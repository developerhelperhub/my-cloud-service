package com.developerhelperhub.ms.monitor.actuator.health;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthRepository extends MongoRepository<HealthEntity, String> {

}
