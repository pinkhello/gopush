package com.gopush.datacenter.restfuls.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/14 下午3:32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("设备")
public class Device {

    @ApiModelProperty(value = "设备号", required = true, example = "IMEI99999")
    private String deviceNo;

    @ApiModelProperty(value = "设备类型", required = true, example = "ios,android,other")
    private String type;

    @ApiModelProperty(value = "应用Code", example = "com.baidu")
    private String appCode;

}
