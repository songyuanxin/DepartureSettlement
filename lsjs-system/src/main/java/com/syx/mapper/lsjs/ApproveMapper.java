package com.syx.mapper.lsjs;

import com.syx.domain.Approve;
import com.syx.domains.dto.ApproveGetDto;
import com.syx.domains.vo.ApproveGetRes;
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

    ApproveGetRes getApproveDataByPernr(String pernr);

    int deleteApproveDataByPernr(String quitPernr);
}
