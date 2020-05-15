package com.vtech.wps.commons;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.OverviewPolicy;
import org.geotools.coverage.grid.io.imageio.GeoToolsWriteParams;
import org.geotools.data.DataSourceException;
import org.geotools.factory.Hints;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.gce.geotiff.GeoTiffWriteParams;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.n52.wps.io.data.IData;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * <pre>
 *
 * </pre>
 *
 * @author houzhiwei
 * @date 2018/12/15 15:26
 */
public class WPSUtils {

    private static final Logger log = LoggerFactory.getLogger(WPSUtils.class);

    public static List<IData> getDataList(String identifier, Map<String, List<IData>> inputData) {
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

    public static boolean writeGeoTiff(GridCoverage2D coverage2D, String dstFile) {
        try {
            final GeoTiffWriteParams wp = new GeoTiffWriteParams();
            wp.setCompressionMode(GeoTiffWriteParams.MODE_DEFAULT);
            wp.setTilingMode(GeoToolsWriteParams.MODE_DEFAULT);
            final GeoTiffFormat format = new GeoTiffFormat();
            final ParameterValueGroup paramWrite = format.getWriteParameters();

            paramWrite.parameter(AbstractGridFormat.GEOTOOLS_WRITE_PARAMS.getName().toString()).setValue(wp);
            GeoTiffWriter gtw = (GeoTiffWriter) format.getWriter(new File(dstFile));
            gtw.write(coverage2D, (GeneralParameterValue[]) paramWrite.values().toArray(new GeneralParameterValue[1]));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static GridCoverage2D readGeoTiff(String file) {
        Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);

        ParameterValue<OverviewPolicy> policy = AbstractGridFormat.OVERVIEW_POLICY.createValue();
        policy.setValue(OverviewPolicy.IGNORE);
        //this will basically read 4 tiles worth of data at once from the disk...
        ParameterValue<String> gridsize = AbstractGridFormat.SUGGESTED_TILE_SIZE.createValue();
        //Setting read type: use JAI ImageRead (true) or ImageReaders read methods (false)
        ParameterValue<Boolean> useJaiRead = AbstractGridFormat.USE_JAI_IMAGEREAD.createValue();
        useJaiRead.setValue(true);
        GeneralParameterValue[] params = new GeneralParameterValue[]{policy, gridsize, useJaiRead};
        try {
//            GridCoverage2DReader reader = new GeoTiffReader(file, hints);
            GridCoverage2DReader reader = new GeoTiffReader(file);
            GridCoverage2D coverage = reader.read(params);
            return coverage;
        } catch (DataSourceException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
