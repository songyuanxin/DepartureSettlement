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
@RequestMapping("/directly")
public class DirectlyController {

    /**
     * 直接上级审核职能部门员工时点击【通过】按钮
     * @return
     */
    @PostMapping(path = "/function/adopt")
    public AjaxResult functionAdopt(){

        return null;
    }

    /**
     * 直接上级审核职能部门员工时点击【待办】按钮
     * @return
     */
    @PostMapping(path = "/function/noAdopt")
    public AjaxResult functionNoAdopt(){
        return null;
    }

    /**
     * 直接上级审核门店员工时点击【通过】按钮
     * @return
     */
    @PostMapping(path = "/store/adopt")
    public AjaxResult storeAdopt(){
        return null;
    }

    /**
     * 直接上级审核门店员工时点击【待办】按钮
     * @return
     */
    @PostMapping(path = "/store/noAdopt")
    public AjaxResult storeNoAdopt(){
        return null;
    }
}
