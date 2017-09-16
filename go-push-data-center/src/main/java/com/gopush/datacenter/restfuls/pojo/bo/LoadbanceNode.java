package com.gopush.datacenter.restfuls.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/14 下午10:15
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("选择节点")
public class LoadbanceNode {
    @ApiModelProperty(value = "链接IP", example = "192.168.1.1")
    private String ip;

    @ApiModelProperty(value = "链接PORT", example = "9999")
    private int port;
    @ApiModelProperty(value = "读周期", example = "10")
    private int readInterval;
    @ApiModelProperty(value = "写周期", example = "30")
    private int writeInterval;
    @ApiModelProperty(value = "读写周期", example = "50")
    private int allInterval;

}
