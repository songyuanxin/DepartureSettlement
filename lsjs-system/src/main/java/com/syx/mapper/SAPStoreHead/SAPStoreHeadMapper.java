package com.syx.mapper.SAPStoreHead;

import com.syx.domain.SAPStoreHead;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 宋远欣
 * @date 2022/5/20
 **/
@Mapper
public interface SAPStoreHeadMapper {
    SAPStoreHead getSAPStoreHeadByStoreId(String storeId);

    String getSAPStoreNameByStoreId(String department);

    List<SAPStoreHead> getSAPStoreHeadByStoreIdAndSqlserver(String name, List<String> storeNameList);
}
