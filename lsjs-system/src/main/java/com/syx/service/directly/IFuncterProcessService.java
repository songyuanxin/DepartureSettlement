package com.syx.service.directly;

import com.syx.domains.ImportDataInfo;
import com.syx.domains.dto.ImportDataDto;
import com.syx.domains.vo.SendMsgRes;

import java.util.List;
import java.util.Map;

/**
 * @author 宋远欣
 * @date 2022/5/12
 **/
public interface IFuncterProcessService {

    /**
     *发送至直接上级企业微信中审核
     * @param
     * @return
     */
    public SendMsgRes sendDirectly(Map<String,List<ImportDataInfo>> functionDirectMap, String isReturn);
}
