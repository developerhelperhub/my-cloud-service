package com.developerhelperhub.ms.id.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.developerhelperhub.ms.id.entity.mongodb.InstanceEntity;

@Repository
public interface InstanceRepository extends MongoRepository<InstanceEntity, String> {

	public List<InstanceEntity> findByApplication(String application);
}
