package com.xuecheng.system.api;

import com.xuecheng.base.model.Result;
import com.xuecheng.system.model.po.Dictionary;
import com.xuecheng.system.service.DictionaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @program: xuecheng-plus-project
 * @description:
 * @author: 酷炫焦少
 * @create: 2024-11-30 18:48
 **/
@RestController
@RequestMapping("/dictionary")
@Tag(name = "字典接口")
public class DictionaryController {

    @Resource
    private DictionaryService dictionaryService;

    @Operation(summary = "查询所有字典数据")
    @GetMapping("/all")
    public List<Dictionary> all() {
        return dictionaryService.getAll();
    }

}
