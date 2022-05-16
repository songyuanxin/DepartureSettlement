package com.syx.domains.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author 宋远欣
 * @date 2022/3/1
 **/
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendMsgRes {
    @JsonProperty(value = "errcode")
    private int errcode;
    @JsonProperty(value = "errmsg")
    private String errmsg;
    @JsonProperty(value = "invaliduser")
    private String invaliduser;
    @JsonProperty(value = "invalidparty")
    private String invalidparty;
    @JsonProperty(value = "invalidtag")
    private String invalidtag;
    @JsonProperty(value = "msgid")
    private String msgid;
    @JsonProperty(value = "response_code")
    private String response_code;
}
