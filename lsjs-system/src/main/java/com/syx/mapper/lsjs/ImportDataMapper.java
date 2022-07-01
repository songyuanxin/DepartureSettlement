package com.syx.mapper.lsjs;

import com.syx.domain.ImportData;
import com.syx.domains.dto.ApproveGetDto;
import com.syx.domains.dto.ImportDataGetDto;
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
    ImportData getLastImoprtDataByPernr(String quitPernr);

    int deleteImportData(String quitPernr,String importTime);

    List<ImportData> getImoprtDataByTime(ApproveGetDto approveGetDto);

    ImportData getImoprtDataByTimeAndPernr(ImportDataGetDto importDataGetDto);

    List<ImportData> getImoprtDataByImportTime(String importTime);

    List<ImportData> getImportDataList();

    List<ImportData> getImoprtDataByPernr(String quitPernr);

    List<ImportData> getImportDataByImportPernr(String ImportPernr);
}
