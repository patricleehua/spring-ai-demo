package com.patriclee.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.patriclee.domain.entity.AiSession;
import org.apache.ibatis.annotations.Mapper;

/**
* @author leehua
* @description 针对表【ai_session(会话记录表)】的数据库操作Mapper
* @createDate 2024-11-29 09:19:40
* @Entity generator.domain.AiSession
*/
@Mapper
public interface AiSessionMapper extends BaseMapper<AiSession> {

}




