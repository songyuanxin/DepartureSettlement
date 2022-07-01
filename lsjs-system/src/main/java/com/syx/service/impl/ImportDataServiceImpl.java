package com.syx.service.impl;

import com.syx.domain.ImportData;
import com.syx.domains.dto.ImportDataDto;
import com.syx.domains.vo.SendMsgRes;
import com.syx.mapper.lsjs.ImportDataMapper;
import com.syx.service.directly.IFuncterProcessService;
import com.syx.service.IImportDataService;
import com.syx.service.directly.IStoreProcessService;
import com.syx.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 宋远欣
 * @date 2022/5/12
 **/
@Service
public class ImportDataServiceImpl implements IImportDataService {

    @Autowired
    private IStoreProcessService storeProcessService;

    @Autowired
    private IFuncterProcessService functerProcessService;

    @Autowired
    private ImportDataMapper importDataMapper;

    /**
     * 发起离司结算流程
     * @param dataList 导入数据
     */
    @Override
    public SendMsgRes launchProcess(List<ImportDataDto> dataList, String isReturn) {

        SendMsgRes sendMsgRes = new SendMsgRes();
        List<ImportDataDto> storeDataList = new ArrayList<>();//存放属于门店的离职员工数据
        List<ImportDataDto> functionDataList = new ArrayList<>();//存放属于职能的离职员工数据

        for (ImportDataDto importDataDto :dataList){
            if (importDataDto.getPersonScope().equals("门店")){
                storeDataList.add(importDataDto);
            }else if (importDataDto.getPersonScope().equals("职能") || importDataDto.getPersonScope().equals("配送中心") || importDataDto.getPersonScope().equals("体检中心")){
                functionDataList.add(importDataDto);
            }else {
                sendMsgRes.setErrcode(400);
                sendMsgRes.setErrmsg("流程发起失败，可能原因：导入模板中存在人员范围不属于【“门店”、“职能”、“体检中心”、“配送中心”】的离职员工！");
                return sendMsgRes;
            }
        }

        //将属于门店和职能的离职员工数据按照直接上级工号分组，为后续发送至直接上级时做准备
        Map<String,List<ImportDataDto>> storeDirectMap = storeDataList.stream().collect(Collectors.groupingBy(item -> item.getDirectPernr() + "_" + item.getDirectName()));
        Map<String,List<ImportDataDto>> functionDirectMap = functionDataList.stream().collect(Collectors.groupingBy(item -> item.getDirectPernr() + "_" + item.getDirectName()));

        //发起门店离司结算流程
        if (!storeDirectMap.isEmpty()){
            sendMsgRes = storeProcessService.sendDirectly(storeDirectMap, isReturn);
        }
        //发起职能离司结算流程
        if (!functionDirectMap.isEmpty()){
            sendMsgRes = functerProcessService.sendDirectly(functionDirectMap, isReturn);
        }
        return sendMsgRes;
    }

    /**
     * 根据离职员工工号查询人事发起离司结算时导入相关数据
     * @param quitPernr
     * @return
     */
    @Override
    public ImportData getLastImportDataByPernr(String quitPernr) {
        return importDataMapper.getLastImoprtDataByPernr(quitPernr);
    }
}
