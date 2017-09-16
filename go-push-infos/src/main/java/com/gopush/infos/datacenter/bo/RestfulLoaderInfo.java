package com.gopush.infos.datacenter.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 喝咖啡的囊地鼠
 * @date 2017/9/15 上午11:17
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestfulLoaderInfo {

    private int callCounter;

    private List<String> restfulUrl;

}