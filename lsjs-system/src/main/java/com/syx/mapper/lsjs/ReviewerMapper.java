package com.syx.mapper.lsjs;

import com.syx.domain.Reviewer;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author 宋远欣
 * @date 2022/5/19
 **/
@Mapper
public interface ReviewerMapper {

    Reviewer getReviewer(String personScope, String parcelDesc, String region);

}
