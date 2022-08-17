package com.syx.mapper.lsjs;

import com.syx.domain.Approve;
import com.syx.domains.vo.ApproveDataRes;
import com.syx.domains.vo.ApproveGetRes;
import com.syx.domains.vo.QueryApproveRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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

    /**
     * 更新审核数据
     * @param approve
     * @return
     */
    int updateApprove(Approve approve);

    /**
     * 官网查询审核结果
     * @param pernr
     * @return
     */
    List<QueryApproveRes> queryApproveByPernr(@Param("pernr") String pernr, @Param("launchId") Integer launchId);

    /**
     * 根据审核人工号查询待自己审核的离职员工工号
     * @param reviewerPernr
     * @return
     */
    List<String> getAuditQuitPernr(@Param("reviewerPernr") String reviewerPernr, @Param("approveContent") Integer approveContent);

    /**
     * 根据离职员工工号查询所属地区经理工号
     * @param quitPernr
     * @return
     */
    String getAreaPernrByQuitPernr(String quitPernr);

    /**
     * 根据离职员工工号和审核内容删除审核表中审核数据(区域经理或地区经理退回时)
     * @param quitPernr
     * @param approveContent
     * @return
     */
    int deleteApproveByPernr(String quitPernr, String approveContent);

    /**
     * 根据员工工号查询审核数据
     * @param pernr
     * @return
     */
    List<Approve> getApproveByPernr(String pernr);

    /**
     * 离司结算审核数据导出报表查询存储过程
     * @param TableName
     * @param launchs
     * @return
     */
    List<ApproveGetRes> getApproveDataByLQ(String TableName,List<String> launchs);

    /**
     * 根据离职员工工号删除审核数据
     * @param quitPernr
     * @return
     */
    int deleteApproveDataByPernr(@Param("quitPernr") String quitPernr, @Param("launchId") Integer launchId);

    /**
     * 根据离职员工工号和审核人工号查询审核ID和发起ID
     * @param quitPernr
     * @param reviewerPernr
     * @return
     */
    Approve getApproveByReviewAndQuitPernr(@Param("quitPernr")String quitPernr, @Param("reviewerPernr")String reviewerPernr);

    /**
     * 离司结算流程监控报表查询存储过程
     * @param name
     * @param launchs
     * @return
     */
    List<ApproveDataRes> getApproveDataResBySqlServer(String name,List<String> launchs);
}
