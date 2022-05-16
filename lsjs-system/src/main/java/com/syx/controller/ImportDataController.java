package com.syx.controller;

import com.syx.domains.AjaxResult;
import com.syx.domains.ImportData;
import com.syx.service.IImportDataService;
import com.syx.utils.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 宋远欣
 * @date 2022/5/11
 **/
@RestController
@RequestMapping("/importData")
public class ImportDataController {

    @Autowired
    private IImportDataService importDataService;

    /**
     * 导入离司结算
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping(path = "/import")
    public AjaxResult importData(MultipartFile file) throws Exception{
        ExcelUtil<ImportData> util = new ExcelUtil<ImportData>(ImportData.class);
        List<ImportData> dataList = util.importExcel(file.getInputStream());

        //一、校验导入数据是否有重复，重复元素为p
        List<String> uniqueList = dataList.stream().collect(Collectors.groupingBy(ImportData::getPernr, Collectors.counting()))
                .entrySet().stream().filter(e -> e.getValue() > 1)
                .map(Map.Entry::getKey).collect(Collectors.toList());
        List<String> repeatList = new ArrayList<>();
        if (uniqueList.size() > 0){
            for (String pernr : uniqueList){
                repeatList.add(pernr);
            }
            return AjaxResult.error(repeatList + "等人的离司结算重复，请检查导入数据！！");
        }

        List<String> noMatchingList = new ArrayList<>();
        List<String> appearsList = new ArrayList<>();
        for (ImportData importData : dataList) {
            String pernr = importData.getPernr();

            //二、校验工号和姓名是否匹配
            //根据工号查询离职员工姓名
//            String name = "宋远欣";
//            if (!name.equals(importData.getName())){
//                noMatchingList.add(pernr+name);
//            }
            //三、校验是否存在已经发起过离司结算的员工
            //根据工号查询该离职员工是否已经发起过离司结算申请，先查出最近一个发起离司结算的时间，再查询该员工最近一次的入职日期，若发起日期在入职日期之后则属正常

        }
        if (noMatchingList.size() > 0){
            return AjaxResult.error(noMatchingList + "等人的离司结算工号与姓名不匹配，请检查导入数据！！");
        }

        //导入数据时若以上校验通过则开始发起流程
        int launchResult = importDataService.launchProcess(dataList);
        if (launchResult == 1){
            return AjaxResult.error("流程发起失败，原因：导入模板中存在人员范围不属于【“门店”、“职能”、“体检中心”、“配送中心”】的离职员工！");
        }

        return AjaxResult.success("流程发起成功");
    }

}
