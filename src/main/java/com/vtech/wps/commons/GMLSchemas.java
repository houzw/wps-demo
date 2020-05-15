package com.vtech.wps.commons;

import lombok.Getter;

/**
 * http://schemas.opengis.net/gml/3.1.1/base/
 *
 * @author houzhiwei
 * @version 3.1.1
 * @date 2018/5/12 21:54
 */
@Getter
public class GMLSchemas {

    public GMLSchemas() {}

    /**
     * GMLSchemas
     *
     * @param version version of schema, default is 3.1.1
     */
    public GMLSchemas(String version) {this.version = version;}

    private String version = "3.1.1";

    public String getVersion() {
        return version;
    }

    public String setVersion(String version) {
        return this.version = version;
    }

    public String feature = "http://schemas.opengis.net/gml/" + version + "/base/feature.xsd";
    public String basicTypes = "http://schemas.opengis.net/gml/" + version + "/base/basicTypes.xsd";
    public String coordinateOperations = "http://schemas.opengis.net/gml/" + version + "/base/coordinateOperations.xsd";
    public String coordinateReferenceSystems = "http://schemas.opengis.net/gml/" + version + "/base/coordinateReferenceSystems.xsd";
    public String coordinateSystems = "http://schemas.opengis.net/gml/" + version + "/base/coordinateSystems.xsd";
    public String coverage = "http://schemas.opengis.net/gml/" + version + "/base/coverage.xsd";
    public String dataQuality = "http://schemas.opengis.net/gml/" + version + "/base/dataQuality.xsd";
    public String datums = "http://schemas.opengis.net/gml/" + version + "/base/datums.xsd";
    public String defaultStyle = "http://schemas.opengis.net/gml/" + version + "/base/defaultStyle.xsd";
    public String dictionary = "http://schemas.opengis.net/gml/" + version + "/base/dictionary.xsd";
    public String direction = "http://schemas.opengis.net/gml/" + version + "/base/direction.xsd";
    public String dynamicFeature = "http://schemas.opengis.net/gml/" + version + "/base/dynamicFeature.xsd";
    public String geometryAggregates = "http://schemas.opengis.net/gml/" + version + "/base/geometryAggregates.xsd";
    public String geometryBasic0d1d = "http://schemas.opengis.net/gml/" + version + "/base/geometryBasic0d1d.xsd";
    public String geometryBasic2d = "http://schemas.opengis.net/gml/" + version + "/base/geometryBasic2d.xsd";
    public String geometryComplexes = "http://schemas.opengis.net/gml/" + version + "/base/geometryComplexes.xsd";
    public String geometryPrimitives = "http://schemas.opengis.net/gml/" + version + "/base/geometryPrimitives.xsd";
    public String gml = "http://schemas.opengis.net/gml/" + version + "/base/gml.xsd";
    public String gmlBase = "http://schemas.opengis.net/gml/" + version + "/base/gmlBase.xsd";
    public String grids = "http://schemas.opengis.net/gml/" + version + "/base/grids.xsd";
    public String measures = "http://schemas.opengis.net/gml/" + version + "/base/measures.xsd";
    public String observation = "http://schemas.opengis.net/gml/" + version + "/base/observation.xsd";
    public String referenceSystems = "http://schemas.opengis.net/gml/" + version + "/base/referenceSystems.xsd";
    public String temporal = "http://schemas.opengis.net/gml/" + version + "/base/temporal.xsd";
    public String temporalReference_systems = "http://schemas.opengis.net/gml/" + version + "/base/temporalReferenceSystems.xsd";
    public String temporalTopology = "http://schemas.opengis.net/gml/" + version + "/base/temporalTopology.xsd";
    public String topology = "http://schemas.opengis.net/gml/" + version + "/base/topology.xsd";
    public String units = "http://schemas.opengis.net/gml/" + version + "/base/units.xsd";
    public String valueObjects = "http://schemas.opengis.net/gml/" + version + "/base/valueObjects.xsd";
}
