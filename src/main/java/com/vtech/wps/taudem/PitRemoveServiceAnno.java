package com.vtech.wps.taudem;

import org.egc.commons.gis.GeoTiffUtils;
import org.egc.commons.util.PropertiesUtil;
import org.geotools.coverage.grid.GridCoverage2D;
import org.n52.wps.algorithm.annotation.*;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTRasterDataBinding;
import org.n52.wps.io.data.binding.complex.GeotiffBinding;
import org.n52.wps.io.data.binding.literal.LiteralBooleanBinding;
import org.n52.wps.server.AbstractAnnotatedAlgorithm;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * <pre>
 * 发布成功
 * 单独上传此文件即可. 但需要将编译之后的 target 目录下的
 * TauDEMAnalysis.class 和 BaseTauDEM.class, 包括 egc-commons 的 jar 加入到 lib
 * 需要 geotools package 及相应的依赖
 * </pre>
 *
 * @author houzhiwei
 * @date 2018/12/24 9:17
 */
@Algorithm(version = "5.3.7", abstrakt = "TauDEM algorithms.", identifier = "PitRemove", title = "Pit Remove")
public class PitRemoveServiceAnno extends AbstractAnnotatedAlgorithm {

    public PitRemoveServiceAnno() {
        super();
    }

    //region utilities

    TauDEMAnalysis tauDEMAnalysis;

    public TauDEMAnalysis getInstance() {
        String workspace = PropertiesUtil.getPropertyFromConfig("workspace", "service");
        tauDEMAnalysis = TauDEMAnalysis.getInstance();
        tauDEMAnalysis.init(workspace);
        return tauDEMAnalysis;
    }

    public List<IData> getDataList(String identifier, Map<String, List<IData>> inputData) {
        if (inputData == null || !inputData.containsKey(identifier)) {
            throw new RuntimeException(
                    "Error while allocating input parameters");
        }
        List<IData> dataList = inputData.get(identifier);
        if (dataList == null || dataList.size() != 1) {
            throw new RuntimeException(
                    "Error while allocating input parameters");
        }
        return dataList;
    }

    //endregion

    private File dem;
    private boolean only4;
    private GridCoverage2D result;
    private File maskGrid;

    @ComplexDataInput(identifier = "dem", binding = GeotiffBinding.class, minOccurs = 1, maxOccurs = 1)
    public void setDem(File dem) {
        this.dem = dem;
    }

    @ComplexDataInput(identifier = "maskGrid", binding = GeotiffBinding.class, minOccurs = 0, maxOccurs = 1)
    public void setMaskGrid(File maskGrid) {
        this.maskGrid = maskGrid;
    }

    @LiteralDataInput(identifier = "only4", binding = LiteralBooleanBinding.class, defaultValue = "false", minOccurs = 0)
    public void setOnly4(Boolean only4) {
        this.only4 = only4;
    }

    @Execute
    public void runAlgorithm() {
        String input = dem.getAbsolutePath();
        System.out.println("Tiff file path: " + input);
        PitRemoveParams.Builder builder = new PitRemoveParams.Builder(input);

        if (only4) {
            builder.fillConsideringOnly4WayNeighbors(only4);
        }
        if (maskGrid.exists()) {
            builder.depressionMask(maskGrid.getAbsolutePath());
        }
        PitRemoveParams pitRemoveParams = builder.build();
        tauDEMAnalysis = getInstance();
        tauDEMAnalysis.pitRemove(pitRemoveParams);
        String output = pitRemoveParams.getPitFilledElevation();
        GridCoverage2D read = GeoTiffUtils.read(output);
        result = read;
//        result = null;
    }

    @ComplexDataOutput(identifier = "result", binding = GTRasterDataBinding.class, abstrakt = "PitFilled")
    public GridCoverage2D getResult() {
        return result;
    }
}
