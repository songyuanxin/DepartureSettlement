package com.syx.domains.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author 宋远欣
 * @date 2022/3/21
 **/
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessTokenRes {
    @JsonProperty(value = "errcode")
    private int errcode;
    @JsonProperty(value = "errmsg")
    private String errmsg;
    @JsonProperty(value = "access_token")
    private String access_token;
    @JsonProperty(value = "expires_in")
    private int expires_in;
}
