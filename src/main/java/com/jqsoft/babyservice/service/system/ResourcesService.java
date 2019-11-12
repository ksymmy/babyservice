package com.jqsoft.babyservice.service.system;

import com.jqsoft.babyservice.commons.utils.RedisUtils;
import com.jqsoft.babyservice.mapper.system.ResourcesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.List;

@Service
public class ResourcesService {

	@Value("${value-expire}")
	private Integer valueExpire;

	@Autowired(required = false)
	public ResourcesMapper resourcesMapper;

	@Autowired
	public RedisUtils redisUtils;


	/**
	 * 暂时没有权限管理的功能，以后上线权限管理，需在redis记录权限是否变更标识，
	 * 如果已变更，根据用户获取资源需重新读取数据库，更新redis
	 * @param userId
	 * @return
	 */
	public List<String> getAllResourcesJumpByUserId(String userId) {
		/*String key = RedisKey.USER_RESOURCES_JUMP.getKey(userId);
		List<String> resourcesJumpList = redisUtils.getObjectList(key);
		if (CollectionUtils.isEmpty(resourcesJumpList)) {
			resourcesJumpList = resourcesMapper.getAllResourcesJumpByUserId(userId);
			if (!CollectionUtils.isEmpty(resourcesJumpList)) {
				redisUtils.addList(key, resourcesJumpList, valueExpire);
			}
		}
		return resourcesJumpList;*/
		return resourcesMapper.getAllResourcesJumpByUserId(userId);
	}

}