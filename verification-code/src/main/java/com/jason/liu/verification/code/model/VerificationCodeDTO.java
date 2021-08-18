package com.jason.liu.verification.code.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author: meng.liu
 * @date: 2021/4/12
 * TODO:
 */
@Data
@ApiModel("验证码请求实体")
public class VerificationCodeDTO<T> implements Serializable {

    @ApiModelProperty("验证码ID")
    private String vcodeId;

    @NotBlank(message = "验证码不能为空")
    @ApiModelProperty("用户输入的验证码")
    private T verifyCode;

}
