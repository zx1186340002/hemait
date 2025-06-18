package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;


    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        //MultipartFile是一个文件类型
    log.info("文件正在上传：{}",file);
    //对原始文件进行UUID防止相同然后删掉。
        try {
            String originalFilename = file.getOriginalFilename();
            //截取原始文件名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID().toString()+extension;
           String filePath = aliOssUtil.upload(file.getBytes(),objectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败：{}",e);
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }

    }

}
