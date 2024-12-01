package com.xuecheng.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.system.model.po.Dictionary;

import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author itcast
 * @since 2024-11-30
 */
public interface DictionaryService extends IService<Dictionary> {

    /**
     * 查询字典数据
     *
     * @return
     */
    List<Dictionary> getAll();

}
