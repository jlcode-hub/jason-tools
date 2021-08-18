package com.jason.liu.verification.code.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: meng.liu
 * @date: 2021/4/12
 * TODO:
 */
@Data
@ApiModel("Base64验证码响应实体")
public class Base64VerificationCodeVO implements Serializable {

    @ApiModelProperty("验证码ID")
    private String vcodeId;

    @ApiModelProperty("验证码图片Base64")
    private String img64;

}
