package com.syx.controller;

import com.syx.domains.AjaxResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 宋远欣
 * @date 2022/5/16
 **/
@RestController
@RequestMapping("/logistics")
public class LogisticsController {

    /**
     * 工牌、工装审核点击【通过】按钮
     * @return
     */
    @PostMapping(path = "/tooling/adopt")
    public AjaxResult toolingAdopt(){
        return null;
    }

    /**
     * 工牌、工装审核点击【通过】按钮
     * @return
     */
    @PostMapping(path = "/tooling/noAdopt")
    public AjaxResult toolingNoAdopt(){
        return null;
    }
}
