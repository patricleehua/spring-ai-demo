package com.patriclee.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 聊天记录表
 * @TableName ai_message
 */
@TableName(value ="ai_message")
@Data
public class AiMessage implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 会话id
     */
    private Long aiSessionId;

    /**
     * 创建者id
     */
    private Long creatorId;

    /**
     * 编辑者Id
     */
    private Long editorId;

    /**
     * 消息类型(用户/ 助手/ 系统)
     */
    @TableField("`type`")
    private String type;

    /**
     * 消息内容
     */
    private String textContent;

    /**
     * 媒体内容如图片链接、语音链接
     */
    @TableField(value = "medias", typeHandler = JacksonTypeHandler.class)
    private List<Media> medias;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 编辑时间
     */
    private LocalDateTime editedTime;

    public static class Media {
        /**
         * 媒体类型
         */
        public String type;
        /**
         * 媒体url
         */
        public String data;
    }
    public static class MessageType{
        public static final String USER = "user";
        public static final String ASSISTANT = "assistant";
        public static final String SYSTEM = "system";
    }
}