package com.jqsoft.babyservice.service.system;

import com.jqsoft.babyservice.commons.utils.RedisUtils;
import com.jqsoft.babyservice.commons.constant.RedisKey;
import com.jqsoft.babyservice.commons.constant.ResultMsg;
import com.jqsoft.babyservice.commons.vo.RestVo;
import com.jqsoft.babyservice.entity.system.Dict;
import com.jqsoft.babyservice.mapper.system.DictMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.List;

@Service
public class DictService {

    @Autowired
    RedisUtils redisUtils;

    @Autowired(required = false)
    DictMapper dictMapper;

    /**
     * 根据字典类型获取字典信息
     * @param code
     * @return
     */
    public RestVo getDictListByCode(String code){
        if (StringUtils.isBlank(code)) {
            return  RestVo.FAIL(ResultMsg.NOT_PARAM);
        }
        String key = RedisKey.DICT_TYLE.getKey(code);
        List<Dict> dictList = redisUtils.getObjectList(key);
        if (CollectionUtils.isEmpty(dictList)) {
            dictList = dictMapper.getDictListByCode(code);
            if (!CollectionUtils.isEmpty(dictList)) {
                redisUtils.addList(key, dictList, RedisUtils.valueExpire);
            }
        }
        return RestVo.SUCCESS(dictList);
    }

    public Dict queryByID(String id) {
        String key = RedisKey.DICT_TYLE.getKey(id);
        List<Dict> dictList = redisUtils.getObjectList(key);
        if (CollectionUtils.isEmpty(dictList)) {
            dictList = dictMapper.getDictListById(id);
            if (!CollectionUtils.isEmpty(dictList)) {
                redisUtils.addList(key, dictList, RedisUtils.valueExpire);
            }
        }
        return dictList.get(0);
    }


	/*public List<Dict> selectDictList(Map<String, Object> params) {
		String pId = StringTools.toString(params.get("pId"));
		return this.selectList(this.buildQueryWrapper()
						.eq(StringTools.notEmpty(pId),"pid", pId)
						.orderBy("sort"));
	}

	public Page<Dict> selectDictPage(PageRequest<Dict> pageRequest){
		// 单表查询  使用默认的查询方法，可以不写Mapper.xml
		// currentPage(pageRequest.getCurrentPage())的含义为：设定分页查询的当前页
		// pageSize(pageRequest.getPageSize())的含义为：设定每页显示的条目
		// eq("pid", pId) 的含义为：字段 pid 等于 pId
		// orderBy("sort") 的含义为：根据sort字段按正序排序
		String pId = StringTools.toString(pageRequest.getCondition("pId"));
		return this.selectPage(this.buildQueryWrapper()
									 .currentPage(pageRequest.getCurrentPage())
									 .pageSize(pageRequest.getPageSize())
									 .eq("pid", pId)
									 .orderBy("sort"));
		// 多表关联查询，需要在Mapper.xml写查询SQL
		// 参见：UserRoleService中的selectRoleListByUser方法
	}

	public int selectDictCount(String id, String code) {
		return this.selectCount(this.buildQueryWrapper()
								      .eq("code", code)
								      .ne(StringTools.notEmpty(id),"id", id));
	}

	public Dict selectDictByCode(String code) {
		return this.selectOne(this.buildQueryWrapper().eq("code", code));
	}*/
}