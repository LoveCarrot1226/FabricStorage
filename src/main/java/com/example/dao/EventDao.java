package com.example.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.domain.CcEvent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EventDao extends BaseMapper<CcEvent> {
}
