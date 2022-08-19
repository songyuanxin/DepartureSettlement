package com.syx.mapper.lsjs;

import com.syx.domain.LsjsAuditSendLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 宋远欣
 * @date 2022/8/17
 **/
@Mapper
public interface LsjsAuditSendLogMapper {

    int insertAuditSendLog(LsjsAuditSendLog lsjsAuditSendLog);
}
