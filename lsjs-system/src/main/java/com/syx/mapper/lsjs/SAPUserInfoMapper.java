package com.syx.mapper.lsjs;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syx.domain.SAPUserInfo;
import com.syx.domain.vo.AuditUserRes;
import com.syx.domains.vo.SearchUserInfoRes;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 宋远欣
 * @date 2022/5/19
 **/
@Mapper
public interface SAPUserInfoMapper extends BaseMapper<SAPUserInfo> {

    /**
     * 根据工号查询员工姓名
     * @param pernr
     * @return
     */
    String getUserNameByPernr(String pernr);

    /**
     * 根据工号查询员工基本信息
     * @param pernr
     * @return
     */
    SAPUserInfo getUserInfoByPernr(String pernr);

    String getDepartmentByPernr(String quitPernr);

    AuditUserRes getUserInfoByPernrList(String quitPernr);

    List<SearchUserInfoRes> searchUserInfoByPernr(String pernr);

    String getDuyuSystemByPernr(String quitPernr);

    String getLeaveDateByPernr(String pernr);

    List<SearchUserInfoRes> getUserPernrOrUserName(String pernrOrName);

    String getLeaveResonByQuitPernr(String pernr);
}
