package com.xuecheng.system.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 数据字典
 * </p>
 *
 * @author itcast
 */
@Data
@Schema(description = "数据字典")
@TableName("dictionary")
public class Dictionary implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(title = "id标识")
    private Long id;

    /**
     * 数据字典名称
     */
    @Schema(title = "数据字典名称")
    private String name;

    /**
     * 数据字典代码
     */
    @Schema(title = "数据字典代码")
    private String code;

    /**
     * 数据字典项--json格式
     */
    @Schema(title = "数据字典项--json格式")
    private String itemValues;


}
