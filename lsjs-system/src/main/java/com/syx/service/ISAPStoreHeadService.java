package com.syx.service;

import com.syx.domain.SAPStoreHead;

/**
 * @author 宋远欣
 * @date 2022/5/26
 **/
public interface ISAPStoreHeadService {

    SAPStoreHead getSAPStoreHeadByStoreId(String storeId);

    String getStoreNameByStoreId(String storeId);
}
