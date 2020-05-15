package com.vtech.wps.taudem;

import com.vtech.wps.commons.WPSUtils;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTRasterDataBinding;
import org.n52.wps.io.data.binding.complex.GeotiffBinding;
import org.n52.wps.server.ExceptionReport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * <pre>
 * TODO 未测试
 * </pre>
 *
 * @author houzhiwei
 * @date 2018/12/15 15:23
 */
public class D8FlowDirections extends BaseTauDEMAlgorithm {

    private static final String FILLED_ELEVATION_GRID = "pit_filled_elevation_grid";
    private static final String D8_SLOPE = "d8_slope";
    private static final String D8_FLOW_DIRECTION = "d8_flow_direction";

    @Override
    public List<String> getInputIdentifiers() {
        List<String> list = new ArrayList<String>();
        list.add(FILLED_ELEVATION_GRID);
        return list;
    }

    @Override
    public List<String> getOutputIdentifiers() {
        List<String> list = new ArrayList<String>();
        list.add(D8_SLOPE);
        list.add(D8_FLOW_DIRECTION);
        return list;
    }

    @Override
    public Map<String, IData> run(Map<String, List<IData>> inputData) throws ExceptionReport {
        IData firstInputData = WPSUtils.getDataList(FILLED_ELEVATION_GRID, inputData).get(0);
        //输入
        File file = ((GeotiffBinding) firstInputData).getPayload();
        String input = file.getPath();
        System.out.println(input);
        D8FlowDirectionsParams params = new D8FlowDirectionsParams.Builder(input).build();
        tauDEMAnalysis = getInstance();
        //执行
        tauDEMAnalysis.d8FlowDirections(params);
        HashMap<String, IData> result = new HashMap<String, IData>();
        //获得输出并赋给wps
        result.put(D8_FLOW_DIRECTION, new GTRasterDataBinding(WPSUtils.readGeoTiff(params.getD8FlowDirection())));
        result.put(D8_SLOPE, new GTRasterDataBinding(WPSUtils.readGeoTiff(params.getD8Slope())));
        return result;
    }

    @Override
    public Class<?> getInputDataType(String s) {
        if (s.equalsIgnoreCase(FILLED_ELEVATION_GRID)) {
            return GeotiffBinding.class;
        }
        return null;
    }

    @Override
    public Class<?> getOutputDataType(String s) {
        if (s.equalsIgnoreCase(D8_FLOW_DIRECTION)) {
            return GeotiffBinding.class;
        } else if (s.equalsIgnoreCase(D8_SLOPE)) {
            return GeotiffBinding.class;
        }
        return null;
    }
}
