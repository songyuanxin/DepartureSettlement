package com.syx.service;

import com.syx.domains.ImportData;

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
    public int launchProcess(List<ImportData> dataList);
}
