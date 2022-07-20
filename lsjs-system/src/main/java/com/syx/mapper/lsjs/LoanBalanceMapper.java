package com.syx.mapper.lsjs;

import com.syx.domain.LoanBalance;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 宋远欣
 * @date 2022/7/20
 **/
@Mapper
public interface LoanBalanceMapper {
    LoanBalance getLoanBalanceByPernr(String quitPernr);

    int insertLoanBalance(LoanBalance loanBalance);

    int updateLoanBalance(LoanBalance loanBalance);

    int deleteLoanBalance(String quitPernr);
}
