package com.patriclee.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.patriclee.domain.entity.AiMessage;
import org.apache.ibatis.annotations.Mapper;

/**
* @author leehua
* @description 针对表【ai_message(聊天记录表)】的数据库操作Mapper
* @createDate 2024-11-29 09:08:54
* @Entity generator.domain.AiMessage
*/
@Mapper
public interface AiMessageMapper extends BaseMapper<AiMessage> {

}




