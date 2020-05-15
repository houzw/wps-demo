package com.vtech.wps.services;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import org.n52.wps.io.data.binding.literal.LiteralDoubleBinding;
import org.n52.wps.server.AbstractAlgorithm;
import org.n52.wps.server.ExceptionReport;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 需要手动编写 ProcessDescription 文件
 */
public class DouglasPeukerSimplificationAlgorithm extends AbstractAlgorithm {

    //all input parameters as key value pairs
    @Override
    public Map<String, IData> run(Map<String, List<IData>> inputData) throws ExceptionReport {
        if(inputData==null || !inputData.containsKey("FEATURES")){
            throw new RuntimeException("Error while allocating input");
        }
        List<IData> features = inputData.get("FEATURES");
        if(features == null || features.size() != 1){
            throw new RuntimeException("Error while allocating input parameters");
        }
        IData firstInputFeature = features.get(0);
        FeatureCollection featureCollection = ((GTVectorDataBinding) firstInputFeature).getPayload();
        if( !inputData.containsKey("width")){
            throw new RuntimeException("Error while allocating input parameters");
        }
        List<IData> widthDataList = inputData.get("TOLERANCE");
        if(widthDataList == null || widthDataList.size() != 1){
            throw new RuntimeException("Error while allocating input parameters");
        }
        Double tolerance = ((LiteralDoubleBinding) widthDataList.get(0)).getPayload();
        FeatureIterator iter = featureCollection.features();
        while(iter.hasNext()) {
            SimpleFeature feature = (SimpleFeature) iter.next();
            if (feature.getDefaultGeometry() == null) {
                throw new NullPointerException("defaultGeometry is null in feature id: " + feature.getIdentifier());
            }
            Object userData = feature.getUserData();
            try {
                Geometry in = (Geometry) feature.getDefaultGeometry();
                Geometry out = DouglasPeuckerSimplifier.simplify(in, tolerance);
                if (in.getGeometryType().equals("MultiPolygon") && out.getGeometryType().equals("Polygon")) {
                    MultiPolygon mp = (MultiPolygon) in;
                    Polygon[] p = {(Polygon) out};
                    mp = new MultiPolygon(p, mp.getFactory());
                    feature.setDefaultGeometry(mp);
                } else if (in.getGeometryType().equals("MultiLineString") &&
                        out.getGeometryType().equals("LineString")) {
                    MultiLineString ml = (MultiLineString) in;
                    LineString[] l = {(LineString) out};
                    ml = new MultiLineString(l, ml.getFactory());
                    feature.setDefaultGeometry(ml);
                } else {
                    feature.setDefaultGeometry(out);
                }
                ((Geometry) feature.getDefaultGeometry()).setUserData(userData);
            } catch (IllegalAttributeException e) {
                throw new RuntimeException("geometrytype of result is not matching", e);
            }
        }
        HashMap<String, IData> result = new HashMap<String, IData>();
        result.put("SIMPLIFIED_FEATURES", new GTVectorDataBinding(featureCollection));
        return result;
    }

    @Override
    public List<String> getErrors() {
        return null;
    }

    @Override
    public Class<?> getInputDataType(String id) {
        return null;
    }

    @Override
    public Class<?> getOutputDataType(String id) {
        return null;
    }
}
