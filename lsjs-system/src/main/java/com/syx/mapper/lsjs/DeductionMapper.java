package com.syx.mapper.lsjs;

import com.syx.domain.Deduction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 宋远欣
 * @date 2022/5/19
 **/
@Mapper
public interface DeductionMapper {
    List<Deduction> getDeductionByPernr(String quitPernr);

    int insertDeduction(Deduction deduction);

    int deleteDeduction(String pernr);

    Deduction getDeductionByPernrAndMonth(@Param("pernr") String pernr, @Param("month") String month);

    int updateDeduction(Deduction deduction);
}
