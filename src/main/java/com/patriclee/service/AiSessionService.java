package com.patriclee.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.patriclee.domain.AiSessionVo;
import com.patriclee.domain.dto.AiSessionDto;
import com.patriclee.domain.entity.AiSession;

import java.util.List;

/**
* @author leehua
* @description 针对表【ai_session(会话记录表)】的数据库操作Service
* @createDate 2024-11-29 09:19:40
*/
public interface AiSessionService extends IService<AiSession> {

    List<AiSessionVo> findSessionsByUserId(String userId);
    AiSessionVo findSessionBySessionId(String sessionId);

    boolean createSession(AiSessionDto aiSessionDto);
}
