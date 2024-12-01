package com.patriclee.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 会话记录表
 * @TableName ai_session
 */
@TableName(value ="ai_session")
@Data
public class AiSession implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 会话名称
     */
    private String name;

    /**
     * 
     */
    private Long creatorId;

    /**
     * 
     */
    private Long editorId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 编辑时间
     */
    private LocalDateTime editedTime;

}