package com.syx.mapper.lsjs;

import com.syx.domain.Approve;
import com.syx.domains.dto.ImportDataGetDto;
import com.syx.domains.vo.ApproveDataRes;
import com.syx.domains.vo.ApproveGetRes;
import com.syx.domains.vo.QueryApproveRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
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
    List<QueryApproveRes> queryApproveByPernr(String pernr);

    /**
     * 根据审核人工号查询待自己审核的离职员工工号
     * @param reviewerPernr
     * @return
     */
    List<String> getAuditQuitPernr(String reviewerPernr);

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
     * 根据员工工号查询审核数据(人力资源中心查询审核数据)
     * @param
     * @return
     */
    ApproveGetRes getApproveDataByPernr(@Param("pernr") String pernr, @Param("importTime") String importTime);

    /**
     * 根据离职员工工号删除审核数据
     * @param quitPernr
     * @return
     */
    int deleteApproveDataByPernr(@Param("quitPernr") String quitPernr,@Param("importTime")String importTime);

    Timestamp getSendTimeByPernr(@Param("pernr")String pernr, @Param("approveContent") Integer approveContent);

    List<Integer> getApproveResultList(@Param("importTime")String importTime, @Param("quitPernr")String quitPernr);

    ApproveDataRes getApproveDataRes(@Param("importTime")String importTime, @Param("quitPernr")String quitPernr
            ,@Param("personScope")String personScope,@Param("importPernr")String importPernr);
}
