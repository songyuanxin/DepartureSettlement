package com.syx.domains.dto;

import lombok.Data;

/**
 * @author 宋远欣
 * @date 2022/7/15
 **/
@Data
public class DeleteImportDataDto {
    //发起ID
    private Integer launchId;
    //离职员工工号
    private String quitPernr;
    //操作人Id
    private String userId;
}
