package com.syx.service.impl;

import com.syx.domain.SAPStoreHead;
import com.syx.mapper.SAPStoreHead.SAPStoreHeadMapper;
import com.syx.service.ISAPStoreHeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 宋远欣
 * @date 2022/5/26
 **/
@Service
public class SAPStoreHeadServiceImpl implements ISAPStoreHeadService {

    @Autowired
    private SAPStoreHeadMapper sapStoreHeadMapper;

    /**
     * 根据门店编码查询所属地区
     * @param storeId
     * @return
     */
    @Override
    public SAPStoreHead getSAPStoreHeadByStoreId(String storeId) {
        return sapStoreHeadMapper.getSAPStoreHeadByStoreId(storeId);
    }

    /**
     * 根据门店编码查询门店名称
     * @param storeId
     * @return
     */
    @Override
    public String getStoreNameByStoreId(String storeId) {
        return sapStoreHeadMapper.getSAPStoreNameByStoreId(storeId);
    }
}
