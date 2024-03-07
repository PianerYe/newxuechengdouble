package com.xuecheng.media.service;

import com.xuecheng.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yepianer
 * @date 2023/8/30 22:21
 * @project_name yepianerxuecheng
 * @description 任务处理
 */

public interface MediaFileProcessService {

    public List<MediaProcess> selectListByShardIndex(@Param("shardTotal") int shardTotal, @Param("shardIndex") int shardIndex, @Param("count") int count);

    /**
     * 开启一个任务
     * @param id 任务id
     * @return 更新记录数
     * */
    public boolean startTask(@Param("id") long id);

    public void saveProcessFinsihStatus(Long taskId,String status,String fileID,String url,String errorMsg);

    /**
     * @descript 获取待处理任务
     * @param shardTotal 分片总数
     * @param shardIndex 分片序号
     * @param count 获取记录数
     * @autor yepianer
     * @return MediaProcess
     * @date 2024/3/5
     * */
    public List<MediaProcess> getMediaProcessList(int shardIndex,int shardTotal,int count);
}
