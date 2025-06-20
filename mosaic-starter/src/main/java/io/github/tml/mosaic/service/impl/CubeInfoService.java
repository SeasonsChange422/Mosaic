package io.github.tml.mosaic.service.impl;

import io.github.tml.mosaic.convert.CubeConvert;
import io.github.tml.mosaic.domain.CubeDomain;
import io.github.tml.mosaic.entity.dto.CubeDTO;
import io.github.tml.mosaic.entity.req.CubeFilterReq;
import io.github.tml.mosaic.entity.vo.cube.CubeInfoVO;
import io.github.tml.mosaic.service.CubeService;
import io.github.tml.mosaic.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Cube信息查询服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CubeInfoService implements CubeService {

    private final CubeDomain cubeDomain;

    @Override
    public R<?> getCubeList() {
        List<CubeDTO> cubeList = cubeDomain.getCubeList();
        List<CubeInfoVO> cubeVOs = CubeConvert.convert2VOs(cubeList);
        
        return R.success(Map.of(
                "cubeList", cubeVOs,
                "total", cubeVOs.size()
        ));
    }

    @Override
    public R<?> getCubesByFilter(CubeFilterReq cubeFilterReq) {
        log.debug("Service: Applying filter criteria: {}", cubeFilterReq);
        
        List<CubeDTO> cubeList = cubeDomain.getCubesByFilter(cubeFilterReq);
        List<CubeInfoVO> cubeVOs = CubeConvert.convert2VOs(cubeList);
        
        return R.success(Map.of(
                "cubeList", cubeVOs,
                "total", cubeVOs.size(),
                "searchCriteria", Map.of(
                        "name", cubeFilterReq.getName() != null ? cubeFilterReq.getName() : "",
                        "model", cubeFilterReq.getModel() != null ? cubeFilterReq.getModel() : "",
                        "version", cubeFilterReq.getVersion() != null ? cubeFilterReq.getVersion() : ""
                )
        ));
    }

    @Override
    public R<?> getCubeById(String cubeId) {
        log.debug("Service: Fetching cube for ID: {}", cubeId);
        
        return cubeDomain.getCubeById(cubeId)
                .map(CubeConvert::convert2VO)
                .map(R::success)
                .orElse(R.error("Cube not found for ID: " + cubeId));
    }

    @Override
    public R<?> getCubeOverview() {
        log.debug("Service: Generating cube overview");
        return R.success(cubeDomain.generateCubeOverview());
    }

    @Override
    public R<?> checkCubeExists(String cubeId) {
        boolean exists = cubeDomain.hasCube(cubeId);
        
        return R.success(Map.of(
                "cubeId", cubeId,
                "exists", exists
        ));
    }
}