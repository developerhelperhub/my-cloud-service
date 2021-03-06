package com.developerhelperhub.ms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.developerhelperhub.ms.entity.mongodb.InstanceEntity;

@Repository
public interface InstanceRepository extends MongoRepository<InstanceEntity, String> {

	public List<InstanceEntity> findByApplication(String application);

	public Optional<InstanceEntity> findByIdentifier(String identifier);
}
