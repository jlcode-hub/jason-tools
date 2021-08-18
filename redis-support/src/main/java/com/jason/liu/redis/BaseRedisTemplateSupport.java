package com.jason.liu.redis;

import com.jason.liu.redis.support.KeyPrefixHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.*;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-06-25 14:07:25
 * @todo
 */
@Slf4j
public class BaseRedisTemplateSupport<Value> implements InitializingBean {

    @Autowired
    private RedisTemplateSupportFactory redisTemplateSupportFactory;

    private RedisTemplate<String, Value> redisTemplate;

    private final String id;

    private final Boolean isPrimary;

    public BaseRedisTemplateSupport(RedisTemplate<String, Value> redisTemplate,
                                    String id,
                                    Boolean isPrimary) {
        this.redisTemplate = redisTemplate;
        this.id = id;
        this.isPrimary = isPrimary;
    }


    String getId() {
        return id;
    }

    Boolean getIsPrimary() {
        return this.isPrimary;
    }

    public RedisTemplate<String, Value> getTemplate() {
        return this.redisTemplate;
    }

    public HashOperations<String, String, Value> hashOperations() {
        return getTemplate().opsForHash();
    }

    public ValueOperations<String, Value> valueOperations() {
        return getTemplate().opsForValue();
    }

    public ListOperations<String, Value> listOperations() {
        return getTemplate().opsForList();
    }

    public SetOperations<String, Value> setOperations() {
        return getTemplate().opsForSet();
    }

    public ZSetOperations<String, Value> zSetOperations() {
        return getTemplate().opsForZSet();
    }

    /**
     * 一次性去除前缀方法
     *
     * @return
     */
    public <Support extends BaseRedisTemplateSupport<Value>> Support withoutPrefixConsumer(Consumer<Support> invoker) {
        KeyPrefixHolder.withoutPrefix(this.redisTemplate);
        try {
            invoker.accept((Support) this);
        } finally {
            KeyPrefixHolder.clear(this.redisTemplate);
        }
        return (Support) this;
    }

    public <Support extends BaseRedisTemplateSupport<Value>, T> T withoutPrefix(Function<Support, T> function) {
        KeyPrefixHolder.withoutPrefix(this.redisTemplate);
        try {
            return function.apply((Support) this);
        } finally {
            KeyPrefixHolder.clear(this.redisTemplate);
        }
    }

    /**
     * 判断key是否存在
     *
     * @param key
     */
    public boolean hasKey(String key) {
        return getTemplate().hasKey(key);
    }


    /**
     * 获取DB的keys数量
     */
    public Long size() {
        return getTemplate().execute(RedisConnection::dbSize);
    }


    /**
     * 删除key
     *
     * @param key
     */
    public <Redis extends BaseRedisTemplateSupport<Value>> Redis delete(String key) {
        getTemplate().delete(key);
        return (Redis) this;
    }

    /**
     * 获取过期时间
     *
     * @param key
     * @return
     */
    public long ttl(String key) {
        return getTemplate().getExpire(key);
    }

    /**
     * 获取指定pattern的key
     *
     * @param pattern
     * @return
     */
    public Set<String> keys(String pattern) {
        return getTemplate().keys(pattern);
    }

    /**
     * 删除多个key
     *
     * @param keys
     */
    public <Redis extends BaseRedisTemplateSupport<Value>> Redis delete(Set<String> keys) {
        getTemplate().delete(keys);
        return (Redis) this;
    }

    /**
     * 模糊删除
     *
     * @param pattern
     */
    public <Redis extends BaseRedisTemplateSupport<Value>> Redis deleteByPatter(String pattern) {
        Set<String> keys = this.keys(pattern);
        if (!CollectionUtils.isEmpty(keys)) {
            this.delete(keys);
        }
        return (Redis) this;
    }

    public <Redis extends BaseRedisTemplateSupport<Value>> Redis expire(String key, long expire) {
        return this.expire(key, expire, TimeUnit.SECONDS);
    }

    public <Redis extends BaseRedisTemplateSupport<Value>> Redis expire(String key, long expire, TimeUnit unit) {
        getTemplate().expire(key, expire, unit);
        return (Redis) this;
    }

    //---------------------------------------------------------------------
    // ValueOperations -> Redis String/Value 操作
    //---------------------------------------------------------------------

    /**
     * 设置key-value值,传入时间单位
     */
    public long incValue(String key) {
        return incValue(key, 1);
    }

    /**
     * 设置key-value值,传入时间单位
     */
    public long incValue(String key, long val) {
        return valueOperations().increment(key, val);
    }

    /**
     * 设置key-value值
     */
    public <Redis extends BaseRedisTemplateSupport<Value>> Redis addValue(String key, Value value, long expire) {
        valueOperations().set(key, value);
        expire(key, expire);
        return (Redis) this;
    }

    /**
     * 设置key-value值,传入时间单位
     */
    public <Redis extends BaseRedisTemplateSupport<Value>> Redis addValue(String key, Value value, long expire, TimeUnit timeUnit) {
        valueOperations().set(key, value, expire, timeUnit);
        return (Redis) this;
    }

    /**
     * 设置key-value值, 无过期时间
     */
    public <Redis extends BaseRedisTemplateSupport<Value>> Redis addValue(String key, Value value) {
        addValue(key, value, 30, TimeUnit.DAYS);
        return (Redis) this;
    }

    /**
     * 设置key-value值, 如果不存在
     * SETNX
     */
    public <Redis extends BaseRedisTemplateSupport<Value>> Boolean addValueIfAbsent(String key, Value value) {
        return addValueIfAbsent(key, value, 30, TimeUnit.DAYS);
    }

    /**
     * 设置key-value值, 无过期时间
     */
    public <Redis extends BaseRedisTemplateSupport<Value>> Boolean addValueIfAbsent(String key, Value value, long expire, TimeUnit timeUnit) {
        return valueOperations().setIfAbsent(key, value, expire, timeUnit);
    }

    /**
     * 获取key的值
     */
    public Value getValue(String key) {
        return valueOperations().get(key);
    }

    /**
     * 获取并删除key的值
     */
    public Value getAndDelete(String key) {
        if (this.hasKey(key)) {
            Value obj = valueOperations().get(key);
            this.delete(key);
            return obj;
        } else {
            return null;
        }
    }

    /**
     * 获取key的值的类型
     */
    public DataType getValueType(String key) {
        return getTemplate().type(key);
    }

    /**
     * 获取指定类型的值
     *
     * @param key
     * @return
     */
    public <T> T getValue(String key, Class<T> tClass) {
        Value object = this.getValue(key);
        if (null == object) {
            return null;
        }
        if (tClass.isInstance(object)) {
            return (T) object;
        } else if (tClass == Long.class && object instanceof Integer) {
            return (T) (Long.valueOf(((Integer) object).longValue()));
        } else {
            log.warn("the object type for key {} is not instance of Class {}", key, tClass.getSimpleName());
            return null;
        }
    }

    //---------------------------------------------------------------------
    // HashOperations -> Redis Redis Hash 操作
    //---------------------------------------------------------------------

    /**
     * 判断指定key的hashKey是否存在
     *
     * @param key
     * @param hashKey
     * @return
     */
    public boolean hasKey(String key, String hashKey) {
        return hashOperations().hasKey(key, hashKey);
    }


    /**
     * 向redis 中添加内容
     *
     * @param key     保存key
     * @param hashKey hashKey
     * @param data    保存对象 data
     */
    public <Redis extends BaseRedisTemplateSupport<Value>> Redis addHashValue(String key, String hashKey, Value data) {
        hashOperations().put(key, hashKey, data);
        return (Redis) this;
    }

    /**
     * 向redis 中添加内容
     *
     * @param key     保存key
     * @param hashKey hashKey
     * @param data    保存对象 data
     * @param expire  过期时间    -1：表示不过期
     */
    public <Redis extends BaseRedisTemplateSupport<Value>> Redis addHashValue(String key, String hashKey, Value data, long expire) {
        hashOperations().put(key, hashKey, data);
        expire(key, expire);
        return (Redis) this;
    }

    /**
     * Hash 添加数据
     *
     * @param key key
     * @param map data
     */
    public <Redis extends BaseRedisTemplateSupport<Value>> Redis addAllHashValue(String key, Map<String, Value> map) {
        hashOperations().putAll(key, map);
        return (Redis) this;
    }

    /**
     * Hash 添加数据
     *
     * @param key key
     * @param map data
     */
    public <Redis extends BaseRedisTemplateSupport<Value>> Redis addAllHashValue(String key, Map<String, Value> map, long expire) {
        hashOperations().putAll(key, map);
        expire(key, expire);
        return (Redis) this;
    }

    /**
     * Hash 添加数据
     *
     * @param key key
     * @param map data
     */
    public <Redis extends BaseRedisTemplateSupport<Value>> Redis addAllHashValue(String key, Map<String, Value> map, long expire, TimeUnit unit) {
        hashOperations().putAll(key, map);
        expire(key, expire, unit);
        return (Redis) this;
    }

    /**
     * 删除hash key
     *
     * @param key     key
     * @param hashKey hashKey
     */
    public long deleteHashValue(String key, String hashKey) {
        return hashOperations().delete(key, hashKey);
    }

    /**
     * 获取数据
     */
    public Value getHashValue(String key, String hashKey) {
        return hashOperations().get(key, hashKey);
    }

    /**
     * 获取数据
     */
    public Map<String, Value> getHash(String key) {
        return hashOperations().entries(key);
    }

    /**
     * 批量获取数据
     */
    public List<Value> getHashAllValue(String key) {
        return hashOperations().values(key);
    }

    /**
     * 批量获取指定hashKey的数据
     */
    public List<Value> getHashMultiValue(String key, List<String> hashKeys) {
        return hashOperations().multiGet(key, hashKeys);
    }

    /**
     * 获取hash数量
     */
    @Deprecated
    public Long getHashCount(String key) {
        return hashOperations().size(key);
    }

    /**
     * 获取hash数量
     */
    public Long hSize(String key) {
        return hashOperations().size(key);
    }


    /**
     * hash值处理
     */
    public Long incHashValue(String key, String hashKey, long value) {
        return hashOperations().increment(key, hashKey, value);
    }

    //---------------------------------------------------------------------
    // ZSetOperations -> Redis Sort Set 操作
    //---------------------------------------------------------------------

    /**
     * 设置zset值
     */
    public boolean addZSetValue(String key, Value member, long score) {
        return zSetOperations().add(key, member, score);
    }

    /**
     * 批量设置zset值
     */
    public long addZSetValue(String key, Set<ZSetOperations.TypedTuple<Value>> tuples) {
        return zSetOperations().add(key, tuples);
    }

    /**
     * 设置zset值
     */
    public boolean addZSetValue(String key, Value member, double score) {
        return zSetOperations().add(key, member, score);
    }

    /**
     * 批量设置zset值
     */
    public long addBatchZSetValue(String key, Set<ZSetOperations.TypedTuple<Value>> tuples) {
        return zSetOperations().add(key, tuples);
    }

    /**
     * 自增zset值
     */
    public <Redis extends BaseRedisTemplateSupport<Value>> Redis incZSetValue(String key, Value member, long delta) {
        zSetOperations().incrementScore(key, member, delta);
        return (Redis) this;
    }

    /**
     * ø
     * 获取zset成员分数
     */
    public long getZSetScore(String key, Value member) {
        Double score = zSetOperations().score(key, member);
        if (score == null) {
            return 0;
        } else {
            return score.longValue();
        }
    }

    /**
     * 获取zset长度
     */
    @Deprecated
    public long getZSetSize(String key) {
        Long size = zSetOperations().zCard(key);
        if (null == size) {
            return 0;
        } else {
            return size;
        }
    }

    /**
     * 获取zset长度
     */
    public long zSize(String key) {
        Long size = zSetOperations().zCard(key);
        if (null == size) {
            return 0;
        } else {
            return size;
        }
    }

    /**
     * 删除指定区间的值
     */
    public long removeZRange(String key, long start, long end) {
        Long removeSize = zSetOperations().removeRange(key, start, end);
        if (null == removeSize) {
            return 0;
        } else {
            return removeSize;
        }
    }

    /**
     * 删除指定区间的值
     */
    public long removeZValues(String key, Value... members) {
        if (null == members || members.length == 0) {
            return 0;
        }
        Long removeSize = zSetOperations().remove(key, members);
        if (null == removeSize) {
            return 0;
        } else {
            return removeSize;
        }
    }

    /**
     * 删除分值的数据
     */
    public long removeRangeByScore(String key, double min, double max) {
        Long removeSize = zSetOperations().removeRangeByScore(key, min, max);
        if (null == removeSize) {
            return 0;
        } else {
            return removeSize;
        }
    }

    /**
     * 获取两个有序集合的并集
     */
    public long zUnionStore(String descKey, String... srcKeys) {
        return zUnionStore(descKey, new int[]{1, 1}, srcKeys);
    }

    /**
     * 获取两个有序集合的并集
     */
    public long zUnionStore(String descKey, int[] weight, String... srcKeys) {
        return zUnionStore(descKey, RedisZSetCommands.Aggregate.SUM, weight, srcKeys);
    }

    /**
     * 获取两个有序集合的并集
     */
    public long zUnionStore(String descKey, RedisZSetCommands.Aggregate aggregate, int[] weight, String... srcKeys) {
        if (StringUtils.isBlank(descKey) || srcKeys.length == 0) {
            return 0;
        }
        byte[] descByt = descKey.getBytes();
        byte[][] srcByts = new byte[srcKeys.length][];
        for (int i = 0; i < srcKeys.length; i++) {
            srcByts[i] = srcKeys[i].getBytes();
        }
        Long count = getTemplate().execute(connection -> connection.zUnionStore(descByt, aggregate, weight, srcByts), true);
        if (null == count) {
            return 0;
        } else {
            return count;
        }
    }

    /**
     * 获取两个有序集合的差集
     */
    public long zDiffSet(String descKey, String srcKey1, String srcKey2) {
        long count = zUnionStore(descKey, RedisZSetCommands.Aggregate.MIN, new int[]{1, 0}, srcKey1, srcKey2);
        if (count <= 0) {
            return 0;
        }
        long remCount = removeRangeByScore(descKey, 0, 0);
        return count - remCount;
    }

    /**
     * 获取有序集 key 中成员
     */
    public Set<ZSetOperations.TypedTuple<Value>> getZSetRank(String key, long start, long end) {
        return zSetOperations().rangeWithScores(key, start, end);
    }

    /**
     * 获取有序集 key 中成员，指定区间
     */
    public Set<Value> getZSetRange(String key, long start, long end) {
        return zSetOperations().range(key, start, end);
    }

    /**
     * 获取分数值最小的
     *
     * @param key
     * @return
     */
    public Value getZSetMinScore(String key) {
        Set<Value> set = getZSetRange(key, 0, 0);
        if (CollectionUtils.isEmpty(set)) {
            return null;
        }
        return set.iterator().next();
    }

    /**
     * 获取有序集 key 中成员，按分数从高到低排序
     */
    public Set<Value> getZSetReverseRange(String key, long start, long end) {
        return zSetOperations().reverseRange(key, start, end);
    }


    /**
     * 获取有序集 key 中成员，按指定分数区间，按分数从低到高排序
     */
    public Set<Value> getZSetRangeByScore(String key, long start, long end) {
        return zSetOperations().rangeByScore(key, start, end);
    }

    /**
     * 获取有序集 key 中成员，按指定分数区间，按分数从高到低排序
     */
    public Set<Value> getZSetReverseRangeByScore(String key, long start, long end) {
        return zSetOperations().reverseRangeByScore(key, start, end);
    }

    /**
     * 获取有序集 key 中成员，按指定分数区间，按分数从低到高排序
     */
    public Set<ZSetOperations.TypedTuple<Value>> getZSetRangeByScoreWithScore(String key, long start, long end) {
        return zSetOperations().rangeByScoreWithScores(key, start, end);
    }

    /**
     * 获取有序集 key 中成员，按指定分数区间，按分数从低到高排序
     */
    public Set<ZSetOperations.TypedTuple<Value>> getZSetRangeByScoreWithScore(String key, long start, long end, long offset, long count) {
        return zSetOperations().rangeByScoreWithScores(key, start, end, offset, count);
    }

    /**
     * 获取有序集 key 中成员，按指定分数区间，按分数从高到低排序
     */
    public Set<ZSetOperations.TypedTuple<Value>> getZSetReverseRangeByScoreWithScore(String key, long start, long end) {
        return zSetOperations().reverseRangeByScoreWithScores(key, start, end);
    }

    /**
     * 获取有序集 key 中成员，按指定分数区间，按分数从高到低排序
     */
    public Set<Value> getZSetReverseRangeByScore(String key, long start, long end, long offset, long count) {
        return zSetOperations().reverseRangeByScore(key, start, end, offset, count);
    }

    /**
     * 获取有序集 key 中成员，按指定分数区间，按分数从高到低排序
     */
    public Set<ZSetOperations.TypedTuple<Value>> getZSetReverseRangeByScoreWithScore(String key, long start, long end, long offset, long count) {
        return zSetOperations().reverseRangeByScoreWithScores(key, start, end, offset, count);
    }

    /**
     * 获取有序集 key 中成员按分数从高到低排序的排名
     */
    public long getZRveRank(String key, String member) {
        Long range = zSetOperations().reverseRank(key, member);
        if (null == range) {
            return 0L;
        } else {
            return range;
        }
    }

    /**
     * 迭代所有元素
     *
     * @param key
     * @return
     */
    public Cursor<ZSetOperations.TypedTuple<Value>> scanZSet(String key) {
        return zSetOperations().scan(key, ScanOptions.NONE);
    }

    //---------------------------------------------------------------------
    // listOperations -> Redis List() 操作
    //---------------------------------------------------------------------

    /**
     * 添加list列表
     */
    public <Redis extends BaseRedisTemplateSupport<Value>> Redis addListValue(String key, Value list) {
        listOperations().leftPush(key, list);
        return (Redis) this;
    }

    public <Redis extends BaseRedisTemplateSupport<Value>> Redis addListAll(String key, Collection<Value> list) {
        listOperations().leftPushAll(key, list);
        return (Redis) this;
    }

    /**
     * 获取指定Key对应的list
     */
    public Value getListValue(String key) {
        return listOperations().leftPop(key);
    }

    /**
     * 获取指定Key对应的list的长度
     */
    @Deprecated
    public Long getListLength(String key) {
        Long length = listOperations().size(key);
        return null == length ? 0 : length;
    }

    /**
     * 获取指定Key对应的list的长度
     */
    public Long lSize(String key) {
        Long length = listOperations().size(key);
        return null == length ? 0 : length;
    }

    public Value getHead(String key) {
        return listOperations().leftPop(key);
    }

    public Value getTail(String key) {
        return listOperations().rightPop(key);
    }

    public <Redis extends BaseRedisTemplateSupport<Value>> Redis addHead(String key, Value value) {
        listOperations().leftPush(key, value);
        return (Redis) this;
    }

    public <Redis extends BaseRedisTemplateSupport<Value>> Redis addTail(String key, Value value) {
        listOperations().rightPush(key, value);
        return (Redis) this;
    }

    public List<Value> getListAll(String key) {
        return this.getListRange(key, 0, -1);
    }

    public List<Value> getListRange(String key, int start, int end) {
        return listOperations().range(key, start, end);
    }

    public <Redis extends BaseRedisTemplateSupport<Value>> Redis trimList(String key, int start, int end) {
        listOperations().trim(key, start, end);
        return (Redis) this;
    }

    public <Redis extends BaseRedisTemplateSupport<Value>> Redis fixList(String key, int length) {
        listOperations().trim(key, 0, length);
        return (Redis) this;
    }

    public Value getAndRemLeft(String key) {
        return listOperations().leftPop(key);
    }

    public Value getAndRemRight(String key) {
        return listOperations().rightPop(key);
    }


    //---------------------------------------------------------------------
    // setOperations -> Redis Set() 操作
    //---------------------------------------------------------------------

    /**
     * 添加Set集合集合
     */
    public <Redis extends BaseRedisTemplateSupport<Value>> Redis addSetValue(String key, Value list) {
        setOperations().add(key, list);
        return (Redis) this;
    }

    public <Redis extends BaseRedisTemplateSupport<Value>> Redis addSetValue(String key, Value... values) {
        if (null != values && values.length > 0) {
            setOperations().add(key, values);
        }
        return (Redis) this;
    }

    /**
     * 是否包含
     */
    public Long sSize(String key) {
        return setOperations().size(key);
    }

    /**
     * 是否包含
     */
    public Boolean sIsMember(String key, Value val) {
        return setOperations().isMember(key, val);
    }


    /**
     * 获取并移除指定key的值
     */
    public Value popSetValue(String key) {
        return setOperations().pop(key);
    }

    /**
     * 获取set的所有值
     */
    public Set<Value> sMembers(String key) {
        return setOperations().members(key);
    }

    /**
     * 获取集合 key 中的指定个数个随机成员
     */
    public List<Value> sRandomMembers(String key, long count) {
        return getTemplate().opsForSet().randomMembers(key, count);
    }

    /**
     * 获取集合 key 中的一个随机成员
     */
    public Value sRandomMember(String key) {
        return getTemplate().opsForSet().randomMember(key);
    }

    public static String buildKey(String... objects) {
        if (objects.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (String o : objects) {
            if (null == o) {
                continue;
            }
            builder.append(o).append(":");
        }
        builder.delete(builder.length() - 1, builder.length());
        return builder.toString();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        redisTemplateSupportFactory.registerTemplate(this);
    }
}
