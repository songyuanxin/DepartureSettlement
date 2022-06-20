package com.syx.mapper.lsjs;

import com.syx.domain.Approve;
import com.syx.domains.vo.QueryApproveRes;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 宋远欣
 * @date 2022/5/19
 **/
@Mapper
public interface ApproveMapper {

    /**
     * 根据工号查询最近一次审核表中的记录
     * @param pernr
     * @return
     */
    Approve getApproveLastByPernr(String pernr);

    /**
     * 企业微信发送消息后将离司结算写入审核表
     * @param approve
     * @return
     */
    int insertApprove(Approve approve);

    int updateApprove(Approve approve);

    List<QueryApproveRes> queryApproveByPernr(String pernr);

    List<String> getAuditQuitPernr(String reviewerPernr);

    String getAreaPernrByQuitPernr(String quitPernr);

    int deleteApproveByPernr(String quitPernr, String approveContent);

    List<Approve> getApproveByPernr(String pernr);
}
