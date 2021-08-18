package com.jason.liu.mybatis.plus.support;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.jason.liu.mybatis.plus.support.annotation.IdGenName;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-05-28 14:30:46
 * @todo
 */
public class MybatisIdentifierGenerator implements IdentifierGenerator {

    private Map<Class<? extends Id>, String> ID_GEN_NAME_MAP = new ConcurrentHashMap<>();

    private final DistributedIdGenerator idGenerator;

    public MybatisIdentifierGenerator(DistributedIdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public Number nextId(Object entity) {
        Class<? extends Id> clazz = (Class<? extends Id>) entity.getClass();
        String idGenName;
        if (ID_GEN_NAME_MAP.containsKey(clazz)) {
            idGenName = ID_GEN_NAME_MAP.get(clazz);
        } else {
            IdGenName idGen = clazz.getAnnotation(IdGenName.class);
            if (null == idGen) {
                throw new IllegalArgumentException("cannot find idGen info for class " + clazz);
            }
            idGenName = idGen.value();
            ID_GEN_NAME_MAP.put(clazz, idGenName);
        }
        return idGenerator.nextId(idGenName);
    }
}
