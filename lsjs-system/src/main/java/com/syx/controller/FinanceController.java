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
@RequestMapping("/finance")
public class FinanceController {

    /**
     * 短款、借款审核时点击【通过】按钮
     * @return
     */
    @PostMapping(path = "/loan/adopt")
    public AjaxResult loanAdopt(){
        return null;
    }

    /**
     * 短款、借款审核时点击【待办】按钮
     * @return
     */
    @PostMapping(path = "/loan/noAdopt")
    public AjaxResult loanNoAdopt(){
        return null;
    }

    /**
     * 质量简报扣款审核时点击【通过】按钮
     * @return
     */
    @PostMapping(path = "/quality/adopt")
    public AjaxResult qualityAdopt(){
        return null;
    }

    /**
     * 财务、会计等审核时点击【待办】按钮
     * @return
     */
    @PostMapping(path = "/quality/noAdopt")
    public AjaxResult qualityNoAdopt(){
        return null;
    }

    /**
     * 管理责任盘点扣款审核时点击【通过】按钮
     * @return
     */
    @PostMapping(path = "/care/adopt")
    public AjaxResult careAdopt(){
        return null;
    }

    /**
     * 管理责任盘点扣款审核时点击【待办】按钮
     * @return
     */
    @PostMapping(path = "/care/noAdopt")
    public AjaxResult careNoAdopt(){
        return null;
    }
}
