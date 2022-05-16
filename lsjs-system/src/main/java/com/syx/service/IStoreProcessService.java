package com.syx.service;

import com.syx.domains.ImportData;

/**
 * @author 宋远欣
 * @date 2022/5/12
 **/
public interface IStoreProcessService {

    /**
     * 发送至直接上级企业微信中审核
     * @param importData
     * @return
     */
    public int sendDirectly(ImportData importData);
}
