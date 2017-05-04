package org.mybatis.star.dao;

import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author stanislav.lapitsky created 5/3/2017.
 */
public abstract class GenericDAO<T, PK> {
    private SqlSession session;
    private String mappingName;
    private String defaultResultMapName;
    private List<ResultMapping> resultMappings;

    public GenericDAO(SqlSession session, String mappingName) {
        this(session, mappingName, mappingName);
    }

    public GenericDAO(SqlSession session, String mappingName, String defaultResultMap) {
        this.session = session;
        this.mappingName = mappingName;
        this.defaultResultMapName = defaultResultMap;
        initMappings();
    }

    private void initMappings() {
        ResultMap resultMap = session.getConfiguration().getResultMap(mappingName + "." + defaultResultMapName);
        this.resultMappings = resultMap.getResultMappings();
    }

    public int insert(T entity) {
        return session.insert(mappingName + ".insert", new DAOWrapper(entity));
    }

    public int update(T entity) {
        return session.update(mappingName + ".update", new DAOWrapper(entity));
    }

    public int delete(T entity) {
        return session.delete(mappingName + ".delete", new DAOWrapper(entity));
    }

    public List<T> getAll() {
        return session.selectList(mappingName + ".selectAll");
    }

    public T getById(PK pk) {
        return session.selectOne(mappingName + ".selectById", pk);
    }

    public class DAOWrapper {
        private T entity;
        private Map<String, Object> valuesMap;
        private List<ResultMapping> columnsResultMappings = new ArrayList<>();
        private List<ResultMapping> idsResultMappings = new ArrayList<>();

        private DAOWrapper(T entity) {
            this.entity = entity;
            init();
        }

        private void init() {
            valuesMap = new LinkedHashMap<>();
            MetaObject metaObject = session.getConfiguration().newMetaObject(entity);
            for (ResultMapping mapping : resultMappings) {
                if (mapping.getFlags().contains(ResultFlag.ID)) {
                    idsResultMappings.add(mapping);
                } else {
                    columnsResultMappings.add(mapping);
                }
                String property = mapping.getProperty();
                Object value = metaObject.getValue(property);
                valuesMap.put(property, value);
            }
        }

        public T getEntity() {
            return entity;
        }

        public Map<String, Object> getValuesMap() {
            return valuesMap;
        }

        public List<ResultMapping> getResultMappings() {
            return resultMappings;
        }

        public List<ResultMapping> getColumnsResultMappings() {
            return columnsResultMappings;
        }

        public List<ResultMapping> getIdsResultMappings() {
            return idsResultMappings;
        }
    }
}
