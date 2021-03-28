package com.developerhelperhub.ms.monitor.application;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstanceRepository extends MongoRepository<InstanceEntity, String> {

	public List<InstanceEntity> findByApplication(String application);
}
