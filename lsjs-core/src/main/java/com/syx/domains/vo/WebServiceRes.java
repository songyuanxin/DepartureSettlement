package com.syx.domains.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 宋远欣
 * @date 2022/5/25
 **/
@Data
public class WebServiceRes {
    private List<ItPDKK> itPDKKList;
    private List<ItRZLI> itRZLIList;
}
