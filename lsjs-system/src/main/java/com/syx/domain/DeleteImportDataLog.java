package com.syx.domain;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 宋远欣
 * @date 2022/7/15
 **/
@Data
public class DeleteImportDataLog {
    private Integer id;
    private String quitPernr;
    private Integer launchId;
    private String originatorPernr;
    private Timestamp deleteTime;
}
