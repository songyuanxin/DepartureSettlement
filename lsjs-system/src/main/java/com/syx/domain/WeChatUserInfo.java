package com.syx.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author weiran-lsx
 * @date 2022/6/29 14:24
 */

@Data
@NoArgsConstructor
public class WeChatUserInfo {

    private Integer errcode;

    private String errmsg;

    private String CorpId;

    private String UserId;

    private String DeviceId;
}
