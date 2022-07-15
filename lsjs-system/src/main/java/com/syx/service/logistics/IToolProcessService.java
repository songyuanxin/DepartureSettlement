package com.syx.service.logistics;

import com.syx.domains.vo.SendMsgRes;

/**
 * @author 宋远欣
 * @date 2022/5/20
 **/
public interface IToolProcessService {
    SendMsgRes sendtoolMsg(Integer launchId, String quitPernr, String userName);
}
