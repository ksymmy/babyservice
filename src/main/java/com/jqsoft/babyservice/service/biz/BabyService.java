package com.jqsoft.babyservice.service.biz;

import com.jqsoft.babyservice.mapper.biz.BabyInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BabyService {

    @Autowired(required = false)
    public BabyInfoMapper babyInfoMapper;


}
