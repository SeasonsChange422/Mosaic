package io.github.tml.mosaic.entity.vo.cube;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 描述: Cube插件信息前端展示对象
 * @author suifeng
 * 日期: 2025/6/7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CubeInfoVO {

    private String id;
    private String name;
    private String version;
    private String description;
    private String model;
    private String scope;
    private String className;
    private CubeStatus status;
    private List<ExtensionPackageVO> extensionPackages;
    private CubeConfigVO config;
    private CubeStatisticsVO statistics;
    private LocalDateTime registeredTime;
    private LocalDateTime lastUpdatedTime;

    @Getter
    public enum CubeStatus {
        ACTIVE("运行中"),
        INACTIVE("未激活"),
        ERROR("错误"),
        LOADING("加载中");
        private final String description;
        CubeStatus(String description) {
            this.description = description;
        }
    }
}