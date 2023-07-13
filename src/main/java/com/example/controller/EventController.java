package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.Result;
import com.example.domain.CcEvent;
import com.example.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/event")
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping("/list")
    public Result<List<CcEvent>> list(CcEvent event){//使用分类的实体类封装传过来的参数
        LambdaQueryWrapper<CcEvent> lqw=new LambdaQueryWrapper<>();
        lqw.eq(event.getEventName()!=null, CcEvent::getEventName,event.getEventName());
        lqw.orderByDesc(CcEvent::getCreateTime);
        List<CcEvent> list=eventService.list(lqw);

        return Result.success(list);
    }

}
