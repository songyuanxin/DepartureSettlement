package com.syx.mapper.lsjs;

import com.syx.domain.Resume;
import com.syx.domains.vo.ResumeRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Date;
import java.util.List;

/**
 * @author 宋远欣
 * @date 2022/5/19
 **/
@Mapper
public interface ResumeMapper {
    List<ResumeRes> getResumeByPernr(String quitPernr);

    int insertResume(Resume resume);

    int deleteResume(String pernr);

    ResumeRes getResumeByPernrAndStartDate(@Param("pernr") String pernr, @Param("startDate") Date startDate);

    int updateResume(Resume resume);
}
