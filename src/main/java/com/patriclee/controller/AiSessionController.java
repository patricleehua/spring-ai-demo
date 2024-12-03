package com.patriclee.controller;

import com.patriclee.domain.vo.AiSessionVo;
import com.patriclee.domain.dto.AiSessionDto;
import com.patriclee.service.AiSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/session")
public class AiSessionController{
    private final AiSessionService aiSessionService;
    public AiSessionController(com.patriclee.service.AiSessionService aiSessionService) {
        this.aiSessionService = aiSessionService;
    }

    /**
     * 根据会话id查询会话信息
     * @param id 会话id
     * @return 会话信息
     */
    @GetMapping("/{id}")
    public AiSessionVo findById(@PathVariable String id) {
        return aiSessionService.findSessionBySessionId(id);
    }
    /**
     * 查询当前登录用户的会话
     *
     * @return 会话列表
     */
    @GetMapping("/user/{userId}")
    public List<AiSessionVo> findSessionsByUserId(@PathVariable String userId) {
        return aiSessionService.findSessionsByUserId(userId);
    }

    @PostMapping("/user/createSession")
    public boolean createSession(@RequestBody AiSessionDto aiSessionDto){
        return aiSessionService.createSession(aiSessionDto);
    }
    @DeleteMapping("/{id}")
    public boolean deleteSession(@PathVariable String id) {
        //todo 删除聊天对话
        return aiSessionService.removeById(id);
    }
}
