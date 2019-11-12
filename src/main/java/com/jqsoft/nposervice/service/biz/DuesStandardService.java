package com.jqsoft.nposervice.service.biz;

import com.jqsoft.nposervice.commons.utils.CommUtils;
import com.jqsoft.nposervice.commons.vo.RestVo;
import com.jqsoft.nposervice.entity.biz.DuesStandard;
import com.jqsoft.nposervice.mapper.biz.DuesStandardMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class DuesStandardService {

    @Resource
    private DuesStandardMapper duesStandardMapper;

    public RestVo saveOrUpdate(DuesStandard entity) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (StringUtils.isBlank(entity.getId())) {
            entity.setId(CommUtils.getUUID());

            entity.setCreateTime(now);
            entity.setUpdateTime(now);
            duesStandardMapper.insertSelective(entity);
            return RestVo.SUCCESS();
        } else {
            entity.setUpdateTime(now);
            duesStandardMapper.updateByPrimaryKeySelective(entity);
            return RestVo.SUCCESS();
        }
    }

    public List<DuesStandard> selectByType(Map<String, Object> params) {
        return duesStandardMapper.selectList(params);
    }

    public RestVo selectList(Map<String, Object> params) {
        List<DuesStandard> data = duesStandardMapper.selectList(params);
        if (data.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                DuesStandard entity = data.get(i);
                entity.setMoneyStr("￥" + entity.getMoney() + "元/年");
                //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                //entity.setCreateTimeStr(simpleDateFormat.format(entity.getCreateTime()));
            }
        }
        return RestVo.SUCCESS(data);
    }

    public RestVo selectInfo(String id) {
        DuesStandard entity = duesStandardMapper.selectByPrimaryKey(id);
        return RestVo.SUCCESS(entity);
    }

    public RestVo deleteById(String id) {
        duesStandardMapper.deleteByPrimaryKey(id);
        return RestVo.SUCCESS();
    }
}
