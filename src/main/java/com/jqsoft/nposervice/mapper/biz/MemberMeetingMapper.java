/**
 * FileName: MemberMeetingMapper
 * Author:   DR
 * Date:     2019/9/18 19:20
 * Description: 会员会议管理
 * History:
 */
package com.jqsoft.nposervice.mapper.biz;

import com.jqsoft.nposervice.entity.biz.MeetingInfo;
import com.jqsoft.nposervice.entity.biz.NoticeInfo;
import com.jqsoft.nposervice.entity.biz.ParticipantsCheck;
import net.jqsoft.persist.mybatisplus.mapper.SuperMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 〈会员会议管理〉
 *
 * @author DR
 * @create 2019/9/18
 * @since 1.0.0
 */
@Mapper
public interface MemberMeetingMapper extends SuperMapper<MeetingInfo> {

    List<MeetingInfo> getMeetingInfo(@Param("offset") Integer offset,
                                     @Param("size")Integer size,
                                     @Param("params")Map<String, Object> params);

    List<ParticipantsCheck> getParticipantsInfo(Map<String, Object> params);

    Integer setMeetingState(ParticipantsCheck participantsCheck);

    Integer deleteMeetingState(ParticipantsCheck participantsCheck);

    MeetingInfo selectMeetingById(@Param("meetingId") String meetingId,
                                  @Param("userId") String userId);

    ParticipantsCheck selectParticipantsCheck(@Param("meetingId")String meetingId,
                                              @Param("userId")String userId);

    void updateParticipantsCheck(@Param("state")Byte state,
                                 @Param("id")String id);
}
