package com.syx.mapper.lsjs;

import com.syx.domain.ImportData;
import com.syx.domains.dto.ApproveGetDto;
import org.apache.ibatis.annotations.Mapper;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

/**
 * @author 宋远欣
 * @date 2022/5/19
 **/
@Mapper
public interface ImportDataMapper {

    /**
     * 将导入数据写入数据库
     * @param importData
     * @return
     */
    int insertImportData(ImportData importData);

    /**
     * 根据离职员工工号查询人事发起离司结算时导入数据
     * @param quitPernr
     * @return
     */
    ImportData getImoprtDataByPernr(String quitPernr);

    int deleteImportData(String quitPernr);

    List<String> getImoprtDataByTime(ApproveGetDto approveGetDto);
}
