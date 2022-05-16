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
@RequestMapping("/manager")
public class ManagerController {

    /**
     * 区域经理或地区经理审核时点击【通过】按钮
     * @return
     */
    @PostMapping(path = "/adopt")
    public AjaxResult managerAdopt(){
        return  null;
    }

    /**
     * 区域经理或地区经理审核时点击【退回】按钮
     * @return
     */
    @PostMapping(path = "/noAdopt")
    public AjaxResult managerNoAdopt(){
        return  null;
    }
}
