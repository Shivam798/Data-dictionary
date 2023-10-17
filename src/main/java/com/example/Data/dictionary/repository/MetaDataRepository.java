package com.example.Data.dictionary.repository;

import com.example.Data.dictionary.model.MetaDataModel;
import org.springframework.data.mongodb.repository.Meta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetaDataRepository extends
        MongoRepository<MetaDataModel,String> {


    MetaDataModel findMetaDataModelById(String id);
}
