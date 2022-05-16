package com.syx.service.impl;

import com.syx.domains.ImportData;
import com.syx.service.IFuncterProcessService;
import com.syx.service.IImportDataService;
import com.syx.service.IStoreProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 宋远欣
 * @date 2022/5/12
 **/
@Service
public class ImportDataServiceImpl implements IImportDataService {

    @Autowired
    private IStoreProcessService storeProcessService;

    @Autowired
    private IFuncterProcessService functerProcessService;

    /**
     * 发起离司结算流程
     * @param dataList 导入数据
     */
    @Override
    public int launchProcess(List<ImportData> dataList) {

        for (ImportData importData:dataList){
            if (importData.getScope().equals("门店")){
                //走门店员工离司结算流程
                int sendResult = storeProcessService.sendDirectly(importData);
            }else if (importData.getScope().equals("职能") || importData.getScope().equals("配送中心") || importData.getScope().equals("体检中心")){
                //走职能员工离司结算流程
                functerProcessService.sendDirectly(importData);
            }else {
                return 1;
            }

        }
        return 0;
    }
}
