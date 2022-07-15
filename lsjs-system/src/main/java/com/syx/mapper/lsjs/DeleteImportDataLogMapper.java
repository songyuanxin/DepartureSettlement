package com.syx.mapper.lsjs;

import com.syx.domain.DeleteImportDataLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 宋远欣
 * @date 2022/7/15
 **/
@Mapper
public interface DeleteImportDataLogMapper {
    int insertDeleteLog(DeleteImportDataLog deleteImportDataLog);
}
