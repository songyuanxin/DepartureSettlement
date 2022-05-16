package com.syx.service.impl;

import com.syx.domains.ImportData;
import com.syx.service.IFuncterProcessService;
import org.springframework.stereotype.Service;

/**
 * @author 宋远欣
 * @date 2022/5/12
 **/
@Service
public class FuncterProcessServiceImpl implements IFuncterProcessService {

    /**
     * 发送至直接上级企业微信中审核
     * @param importData
     * @return
     */
    @Override
    public int sendDirectly(ImportData importData) {
        return 0;
    }
}
