package com.patriclee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.patriclee.domain.AiSessionVo;
import com.patriclee.domain.dto.AiSessionDto;
import com.patriclee.domain.entity.AiMessage;
import com.patriclee.domain.entity.AiSession;
import com.patriclee.domain.mapper.AiSessionMapper;
import com.patriclee.service.AiMessageService;
import com.patriclee.service.AiSessionService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author leehua
* @description 针对表【ai_session(会话记录表)】的数据库操作Service实现
* @createDate 2024-11-29 09:19:40
*/
@Service
public class AiSessionServiceImpl extends ServiceImpl<AiSessionMapper, AiSession>
    implements AiSessionService {
    @Resource
    private AiMessageService aiMessageService;

    @Override
    public List<AiSessionVo> findSessionsByUserId(String userId) {
        List<AiSession> aiSessions = this.baseMapper.selectList(new LambdaQueryWrapper<AiSession>().eq(AiSession::getCreatorId, userId));
        return aiSessions.stream().map(this::covertAiSessionToVo).toList();
    }

    @Override
    public AiSessionVo findSessionBySessionId(String sessionId){
        AiSession aiSession = this.baseMapper.selectById(sessionId);
        return covertAiSessionToVo(aiSession);
    }

    @Override
    public boolean createSession(AiSessionDto aiSessionDto) {
        AiSession aiSession = new AiSession();
        aiSession.setCreatorId(Long.valueOf(aiSessionDto.getUserId()));
        aiSession.setEditorId(Long.valueOf(aiSessionDto.getUserId()));
        aiSession.setName(aiSessionDto.getName());
        int insert = this.baseMapper.insert(aiSession);
        return insert>0;
    }

    private AiSessionVo covertAiSessionToVo(AiSession aiSession){
        AiSessionVo aiSessionVo = new AiSessionVo();
        BeanUtils.copyProperties(aiSession, aiSessionVo);
        aiSessionVo.setId(String.valueOf(aiSession.getId()));
        aiSessionVo.setCreatorId(String.valueOf(aiSession.getCreatorId()));
        long count = aiMessageService.count(new LambdaQueryWrapper<AiMessage>().eq(AiMessage::getAiSessionId, aiSession.getId()));
        aiSessionVo.setNum((int) count);
        return aiSessionVo;
    }
}




