package com.developerhelperhub.ms.id.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.developerhelperhub.ms.id.entity.UserEntity;

@Repository
public interface UserRepository extends MongoRepository<UserEntity, String>{

}
