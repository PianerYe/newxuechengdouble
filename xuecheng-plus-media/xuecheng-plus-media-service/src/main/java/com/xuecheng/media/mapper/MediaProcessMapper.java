package com.xuecheng.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {

    //selectListByShardIndex media_process
    /**
     *  @Select("select * from media_process t where t.id % #{shardTotal}
     *  = #{shardIndex} and (t.status = '1' or t.status = '3')
     *  and t.fail_count < 3 limit #{count}")
     * */
    @Select("select * from media_process t where t.id % #{shardTotal} = #{shardIndex} and (t.status = '1' or t.status = '3') and t.fail_count < 3 limit #{count}")
    List<MediaProcess> selectListByShardIndex(@Param("shardTotal") int shardTotal, @Param("shardIndex") int shardIndex, @Param("count") int count);

    @Update("UPDATE media_process t SET t.`status`= 4 WHERE (t.`status`= 1 OR t.`status` = 3) AND t.`fail_count`<3 and t.`id` = #{id} ")
    int startTask(@Param("id") long id);

}