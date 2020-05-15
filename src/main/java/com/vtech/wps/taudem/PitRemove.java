package com.vtech.wps.taudem;

import org.egc.commons.gis.GeoTiffUtils;
import org.egc.commons.util.PropertiesUtil;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTRasterDataBinding;
import org.n52.wps.io.data.binding.complex.GeotiffBinding;
import org.n52.wps.io.data.binding.literal.LiteralBooleanBinding;
import org.n52.wps.server.AbstractSelfDescribingAlgorithm;
import org.n52.wps.server.ExceptionReport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * <pre>
 * taudem PitRemove
 * 这个好像会出错，不能得到 ProcessDescriptions
 * </pre>
 *
 * @author houzhiwei
 * @date 2018/11/13 15:11
 * @date 2020-4-25
 */
public class PitRemove extends AbstractSelfDescribingAlgorithm {
    //为了方便发布

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

    private static final String DEM = "dem";
    private static final String ONLY_4_WAY = "only_4_way";
    private static final String PIT_REMOVED_DEM = "pit_removed_dem";
    private static final String DEPRESSION_MASK_GRID = "mask_grid";

    @Override
    public List<String> getInputIdentifiers() {
        List<String> list = new ArrayList<String>();
        list.add(DEM);
        list.add(ONLY_4_WAY);
        list.add(DEPRESSION_MASK_GRID);
        return list;
    }


    @Override
    public Class<?> getInputDataType(String s) {
        // 根据输入数据id指定数据绑定。
        // 每个输入都需要
        // 与 run 里面的一致
        if (s.equalsIgnoreCase(DEM)) {
            return GeotiffBinding.class;
        } else if (s.equalsIgnoreCase(DEPRESSION_MASK_GRID)) {
            return GeotiffBinding.class;
        } else if (s.equalsIgnoreCase(ONLY_4_WAY)) {
            return LiteralBooleanBinding.class;
        }
        return null;
    }

    @Override
    public Map<String, IData> run(Map<String, List<IData>> inputData) throws ExceptionReport {

        IData firstInputData = getDataList(DEM, inputData).get(0);
        IData only_4_way_neighbors = getDataList(ONLY_4_WAY, inputData).get(0);
        IData Depression_Mask = getDataList(DEPRESSION_MASK_GRID, inputData).get(0);
        File file = ((GeotiffBinding) firstInputData).getPayload();
        Boolean only4 = ((LiteralBooleanBinding) only_4_way_neighbors).getPayload();
        File mask = ((GeotiffBinding) Depression_Mask).getPayload();

        String input = file.getAbsolutePath();
        System.out.println("Tiff file path: " + input);
        PitRemoveParams.Builder builder = new PitRemoveParams.Builder(input);

        if (only4) {
            builder.fillConsideringOnly4WayNeighbors(only4);
        }
        if (mask.exists()) {
            builder.depressionMask(mask.getAbsolutePath());
        }
        PitRemoveParams pitRemoveParams = builder.build();
        tauDEMAnalysis = getInstance();
        tauDEMAnalysis.pitRemove(pitRemoveParams);
        String output = pitRemoveParams.getPitFilledElevation();
        HashMap<String, IData> result = new HashMap<String, IData>();
        result.put(PIT_REMOVED_DEM, new GTRasterDataBinding(GeoTiffUtils.read(output)));
        return result;
    }

    @Override
    public List<String> getOutputIdentifiers() {
        List<String> list = new ArrayList<String>();
        list.add(PIT_REMOVED_DEM);
        return list;
    }

    @Override
    public Class<?> getOutputDataType(String s) {
        //注意与run方法里面的一致
        if (s.equalsIgnoreCase(PIT_REMOVED_DEM)) {
            return GTRasterDataBinding.class;
        }
        return null;
    }
}
