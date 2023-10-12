package com.example.Data.dictionary.repository;

import com.example.Data.dictionary.model.MetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetaDataRepository extends
        JpaRepository<MetaData,Integer> {

    MetaData findByPrimaryKey(String dataId);
}
