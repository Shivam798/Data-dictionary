package com.example.Data.dictionary.repository;

import com.example.Data.dictionary.model.MetaDataModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetaDataRepository extends
        MongoRepository<MetaDataModel,String> {

    MetaDataModel findByPrimaryKey(String dataId);

    boolean existsByPrimaryKey(String dataId);
}
