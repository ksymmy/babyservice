/**
 * FileName: OrgNoticeMapper
 * Author:   DR
 * Date:     2019/9/21 15:35
 * Description: 组织通知公告
 * History:
 */
package com.jqsoft.nposervice.mapper.biz;

import com.jqsoft.nposervice.entity.biz.NoticeInfo;
import com.jqsoft.nposervice.entity.system.FileEntity;
import net.jqsoft.persist.mybatisplus.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
/**
 * 〈组织通知公告〉
 * @author DR
 * @create 2019/9/21
 * @since 1.0.0
 */
@Mapper
public interface OrgNoticeMapper extends SuperMapper<NoticeInfo> {

    List<NoticeInfo> selectNoticeById(@Param("offset") Integer offset,
                                      @Param("size") Integer size,
                                      @Param("param") Map<String, Object> param,
                                      @Param("orgId") String orgId);

    Integer publishNoticeById(Map<String, Object> params);

    Integer saveNotice(NoticeInfo noticeInfo);

    Integer updateNotice(NoticeInfo noticeInfo);

    Integer deleteNotice(String id);

    Integer deleteFile(String id);
}
