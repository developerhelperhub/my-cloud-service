package com.developerhelperhub.ms.id.monitor.actuator.info;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthRepository extends MongoRepository<HealthEntity, String> {

}
