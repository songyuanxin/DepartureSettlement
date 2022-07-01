package com.syx.service;

import com.syx.domain.ImportData;
import com.syx.domains.dto.ImportDataDto;
import com.syx.domains.vo.SendMsgRes;

import java.util.List;

/**
 * @author 宋远欣
 * @date 2022/5/12
 **/
public interface IImportDataService {
    /**
     * 发起流程
     * @param dataList 导入数据
     */
    SendMsgRes launchProcess(List<ImportDataDto> dataList, String isReturn);

    ImportData getLastImportDataByPernr(String quitPernr);
}
