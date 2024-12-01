package com.patriclee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.patriclee.domain.dto.AiMessageDTO;
import com.patriclee.domain.entity.AiMessage;
import com.patriclee.domain.mapper.AiMessageMapper;
import com.patriclee.service.AiMessageService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* @author leehua
* @description 针对表【ai_message(聊天记录表)】的数据库操作Service实现
* @createDate 2024-11-29 09:08:54
*/
@Service
public class AiMessageServiceImpl extends ServiceImpl<AiMessageMapper, AiMessage>
    implements AiMessageService {

    @Override
    public List<AiMessage> getaiMessageListBySessionId(String conversationId, int lastN) {
        AiMessageMapper aiMessageMapper = this.getBaseMapper();
        LambdaQueryWrapper<AiMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiMessage::getAiSessionId, conversationId);
        queryWrapper.orderBy(true, false, AiMessage::getCreateTime);
        queryWrapper.last("limit "+lastN);
        return aiMessageMapper.selectList(queryWrapper);
    }

    @Override
    public void deleteBySessionId(String conversationId) {
        AiMessageMapper baseMapper = this.getBaseMapper();

        int delete = baseMapper.delete(new LambdaQueryWrapper<AiMessage>().eq(AiMessage::getAiSessionId, conversationId));
    }

    @Override
    public void saveAiMessage(AiMessageDTO aiMessageDTO) {
        AiMessage aiMessage = convertToAiMessage(aiMessageDTO);
        this.save(aiMessage);
    }
    @Override
    public AiMessage convertToAiMessage(AiMessageDTO aiMessageDTO) {
        AiMessage aiMessage = new AiMessage();
        aiMessage.setAiSessionId(Long.valueOf(aiMessageDTO.getAiSessionId()));
        aiMessage.setCreatorId(Long.valueOf(aiMessageDTO.getCreatorId()));
        aiMessage.setEditorId(Long.valueOf(aiMessageDTO.getCreatorId()));
        aiMessage.setTextContent(aiMessageDTO.getTextContent());

        switch (aiMessageDTO.getType()) {
            case "user":
                aiMessage.setType(AiMessage.MessageType.USER);
                break;
            case "assistant":
                aiMessage.setType(AiMessage.MessageType.ASSISTANT);
                break;
            case "system":
                aiMessage.setType(AiMessage.MessageType.SYSTEM);
                break;
            default:
                throw new IllegalArgumentException("Unknown message type: " + aiMessageDTO.getType());
        }

        Map<String, String> medias = aiMessageDTO.getMedias();
        if (medias != null) {
            List<AiMessage.Media> mediaList = new ArrayList<>();
            for (Map.Entry<String, String> entry : medias.entrySet()) {
                AiMessage.Media media = new AiMessage.Media();
                media.type = entry.getKey();
                media.data = entry.getValue();
                mediaList.add(media);
            }
            aiMessage.setMedias(mediaList);
        }

        return aiMessage;
    }

}




