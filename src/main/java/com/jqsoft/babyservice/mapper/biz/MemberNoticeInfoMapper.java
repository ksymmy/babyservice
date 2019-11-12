/**
 * FileName: MemberNoticeInfoMapper
 * Author:   DR
 * Date:     2019/9/17 11:11
 * Description: 会员通知公告信息维护
 * History:
 */
package com.jqsoft.babyservice.mapper.biz;

import com.jqsoft.babyservice.entity.biz.InfoReadedUser;
import com.jqsoft.babyservice.entity.biz.NoticeInfo;
import com.jqsoft.babyservice.entity.system.FileEntity;
import net.jqsoft.persist.mybatisplus.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 〈会员通知公告信息维护〉
 *
 * @author DR
 * @create 2019/9/17
 * @since 1.0.0
 */
@Mapper
public interface MemberNoticeInfoMapper extends SuperMapper<NoticeInfo> {

    List<NoticeInfo> selectNoticeInfo(@Param("offset") Integer offset,
                                      @Param("size")Integer size,
                                      @Param("params")Map<String, Object> params);

    List<FileEntity> selectNoticeFileInfoByNoticeId(String noticeId);

    Integer updateReceiverState(InfoReadedUser infoReadedUser);
}
