package com.vtech.wps.commons;

/**
 * MimeTypes
 *
 * @author houzhiwei
 * @date 2018/5/12 21:58
 */
public class MimeTypes {

    public static final String PNG = "image/png";
    public static final String JPEG = "image/jpeg";
    public static final String TIFF = "image/tiff";
    public static final String GEOTIFF = "image/geotiff";
    public static final String PDF = "application/pdf";
    public static final String ZIP = "application/zip";
    public static final String JSON = "application/json";

    public static final String TEXT = "text/plain";
    public static final String HTML = "text/html";
    public static final String XML = "text/xml";

    public static final String R_DATA = "application/rData";
    public static final String R_DATA_SPATIAL = "application/rData+Spatial";
    public static final String R_DATA_SPATIAL_POINTS = "application/rData+SpatialPoints";
    public static final String R_DATA_SPATIAL_POLYGONS = "application/rData+SpatialPolygons";

    public static final String APP_ZIPPED_SHP = "application/x-zipped-shp";
    public static final String APP_HDF4 = "application/hdf4-eos";
    public static final String APP_X_GEOTIFF = "application/x-geotiff";
    public static final String APP_GEOTIFF = "application/geotiff";
    public static final String APP_X_ERDAS_HFA = "application/x-erdas-hfa";
    public static final String APP_X_NETCDF = "application/x-netcdf";
    public static final String APP_NETCDF = "application/netcdf";
    public static final String APP_SHP = "application/shp";

    public static final String APP_IMG = "application/img";
    public static final String APP_DBASE = "application/dbase";
    public static final String APP_REMAP = "application/remap";
    public static final String APP_DGN = "application/dgn";

    /**
     * get Suffix From MIMEType
     * {@link org.n52.wps.commons.MIMEUtil#getSuffixFromMIMEType(String)}
     *
     * @param mimeType
     * @return Suffix
     */
    public static String getSuffixFromMIMEType(String mimeType) {
        String[] mimeTypeSplit = mimeType.split("/");
        String suffix = mimeTypeSplit[mimeTypeSplit.length - 1];
        if ("geotiff".equalsIgnoreCase(suffix)
                || "x-geotiff".equalsIgnoreCase(suffix)) {
            suffix = "tiff";
        } else if ("netcdf".equalsIgnoreCase(suffix)
                || "x-netcdf".equalsIgnoreCase(suffix)) {
            suffix = "nc";
        } else if ("x-zipped-shp".equalsIgnoreCase(suffix)) {
            suffix = "zip";
        } else if ("text/plain".equals(mimeType)) {
            suffix = "txt";
        } else if ("text/html".equals(mimeType)) {
            suffix = "html";
        } else if ("application/json".equals(mimeType)) {
            suffix = "json";
        } else if ("text/csv".equals(mimeType)) {
            suffix = "csv";
        } else if (mimeType.contains("rData")) {
            suffix = "rData";
        }
        return suffix;
    }
}
