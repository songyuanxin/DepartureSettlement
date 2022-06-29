package com.syx.domains.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 宋远欣
 * @date 2022/6/29
 **/
@Data
public class UploadDto {
    private MultipartFile file;
    private String userId;
}
