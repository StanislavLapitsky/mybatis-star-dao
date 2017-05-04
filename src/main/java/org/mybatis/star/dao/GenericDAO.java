package org.mybatis.star.dao;

import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The main class which can handle common CRUD operations. It wraps actual entity with DAOWrapper
 * providing additional methods to let MyBatis generic access to all properties/values as well as
 * metadata - resultMap
 * @author stanislav.lapitsky created 5/3/2017.
 */
public abstract class GenericDAO<T, PK> {
    //mapping key "insert" must be defined in mapping file
    public static final String MAPPING_INSERT = ".insert";
    //mapping key "update" must be defined in mapping file
    public static final String MAPPING_UPDATE = ".update";
    //mapping key "delete" must be defined in mapping file
    public static final String MAPPING_DELETE = ".delete";
    //mapping key "selectAll" must be defined in mapping file
    public static final String MAPPING_SELECT_ALL = ".selectAll";
    //mapping key "selectById" must be defined in mapping file
    public static final String MAPPING_SELECT_BY_ID = ".selectById";

    //MyBatis session
    private SqlSession session;
    //mapping name e.g. User from <mapper namespace="User">
    private String mappingName;
    //resultmap id from mapping e.g. User from <resultMap id="User"> by default it has the same name as mappingName
    private String defaultResultMapName;
    //resultmap metadata
    private List<ResultMapping> resultMappings;

    /**
     * Creates a new DAO instance with the session, mapping and default result map
     * @param session mybatis session
     * @param mappingName mapping name
     */
    public GenericDAO(SqlSession session, String mappingName) {
        this(session, mappingName, mappingName);
    }

    /**
     * Creates a new DAO instance with the session, mapping and default result map
     * @param session mybatis session
     * @param mappingName mapping name
     * @param defaultResultMap default resultmap name (used to get columns/properties relations)
     */
    public GenericDAO(SqlSession session, String mappingName, String defaultResultMap) {
        this.session = session;
        this.mappingName = mappingName;
        this.defaultResultMapName = defaultResultMap;
        initMappings();
    }

    /**
     * Gets result map metadata for the mapping and stores it to be used in the CRUD calls
     */
    private void initMappings() {
        ResultMap resultMap = session.getConfiguration().getResultMap(mappingName + "." + defaultResultMapName);
        this.resultMappings = resultMap.getResultMappings();
    }

    /**
     * Inserts an entity
     * @param entity entity instance
     * @return inserted rows count
     */
    public int insert(T entity) {
        return session.insert(mappingName + MAPPING_INSERT, new DAOWrapper(entity));
    }

    /**
     * Updates an entity
     * @param entity entity instance
     * @return updated rows count
     */
    public int update(T entity) {
        return session.update(mappingName + MAPPING_UPDATE, new DAOWrapper(entity));
    }

    /**
     * Deletes an entity
     * @param entity entity instance
     * @return deleted rows count
     */
    public int delete(T entity) {
        return session.delete(mappingName + MAPPING_DELETE, new DAOWrapper(entity));
    }

    /**
     * Gets all existing entities
     * @return list
     */
    public List<T> getAll() {
        return session.selectList(mappingName + MAPPING_SELECT_ALL);
    }

    /**
     * Gets one entity by Primary Key
     * @param pk the entity primary key
     * @return entity
     */
    public T getById(PK pk) {
        return session.selectOne(mappingName + MAPPING_SELECT_BY_ID, pk);
    }

    /**
     * The class wraps original entity and provides necessary method for common using.
     * It keeps entity itself (e.g. we set the entity ID after insert)
     * store list of columns and ID columns.
     */
    public class DAOWrapper {
        //CRUD entity
        private T entity;
        //Wrappers for ResultMapping. Need to introduce getIsId() method
        private List<ResultMappingWrapper> resultMappings = new ArrayList<>();

        private DAOWrapper(T entity) {
            this.entity = entity;
            resultMappings = GenericDAO.this.resultMappings.stream()
                    .map(item -> new ResultMappingWrapper(item))
                    .collect(Collectors.toList());        }

        public T getEntity() {
            return entity;
        }

        public List<ResultMappingWrapper> getResultMappings() {
            return resultMappings;
        }

    }

    /**
     * Wrapper class for ResultMapping. Need to introduce getIsId() method.
     * The rest is just delegated to actual ResultMapping
     */
    public static class ResultMappingWrapper {
        private ResultMapping source;

        private ResultMappingWrapper(ResultMapping source) {
            this.source = source;
        }

        public String getProperty() {
            return source.getProperty();
        }

        public String getColumn() {
            return source.getColumn();
        }

        public boolean getIsId() {
            return source.getFlags().contains(ResultFlag.ID);
        }
    }
}
