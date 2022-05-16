package com.syx.service.impl;

import com.syx.domain.SAPUserInfo;
import com.syx.domains.ImportData;
import com.syx.service.IStoreProcessService;
import org.springframework.stereotype.Service;

/**
 * @author 宋远欣
 * @date 2022/5/12
 **/
@Service
public class StoreProcessServiceImpl implements IStoreProcessService {

    /**
     * 发送至直接上级企业微信审核
     * @param importData
     * @return
     */
    @Override
    public int sendDirectly(ImportData importData) {
        //1、根据工号查询离职员工基本信息
        SAPUserInfo userInfo = new SAPUserInfo();
        importData.getPernr();

        //2、发送至直接上级企业微信

        return 0;
    }
}
