package com.xuecheng.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.base.constant.SystemConstant;
import com.xuecheng.system.mapper.DictionaryMapper;
import com.xuecheng.system.model.po.Dictionary;
import com.xuecheng.system.service.DictionaryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author itcast
 */
@Slf4j
@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {

    @Resource
    private DictionaryMapper dictionaryMapper;

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public List<Dictionary> getAll() {
        String obj = (String) redisTemplate.opsForValue().get(SystemConstant.SYS_DICT);
        if (StrUtil.isNotBlank(obj)) {
            return JSON.parseArray(obj, Dictionary.class);
        }
        List<Dictionary> dictionaries = dictionaryMapper.selectList(Wrappers.<Dictionary>lambdaQuery());
        if (CollectionUtil.isEmpty(dictionaries)) {
            return Collections.emptyList();
        }
        redisTemplate.opsForValue().set(SystemConstant.SYS_DICT, JSON.toJSONString(dictionaries), 1L, TimeUnit.HOURS);
        return dictionaries;
    }

}
