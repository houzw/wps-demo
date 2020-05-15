package com.vtech.wps.client;

import com.vtech.wps.commons.GMLSchemas;
import com.vtech.wps.commons.MimeTypes;
import lombok.extern.slf4j.Slf4j;
import net.opengis.wps.x100.*;
import org.egc.commons.util.PathUtil;
import org.geotools.feature.FeatureCollection;
import org.n52.wps.client.ExecuteRequestBuilder;
import org.n52.wps.client.ExecuteResponseAnalyser;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;
import org.n52.wps.commons.WPSConfig;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

import java.io.IOException;
import java.util.HashMap;

/**
 * Description:
 * <pre>
 * 52 North WPS 客户端工具类
 * </pre>
 *
 * @author houzhiwei
 * @date 2018/12/16 14:22
 */
@Slf4j
public class WPSClientUtils {

    private WPSClientUtils() {
    }

    public static void initWPSConfig() {
        String path = PathUtil.getClassPath();
        WPSConfig.getInstance(path + "config/wps_config_geotools_client.xml");
    }

    /**
     * Request a WPS Capabilities document
     *
     * @param url the url
     * @return the capabilities document
     * @throws WPSClientException the wps client exception
     */
    public static CapabilitiesDocument requestCapabilities(String url) throws WPSClientException {
        // 初始化
        WPSClientSession wpsClient = WPSClientSession.getInstance();
        // 注册url
        boolean connected = wpsClient.connect(url);
        // 发送 capabilities 请求，获取信息
        CapabilitiesDocument document = wpsClient.getWPSCaps(url);
        // 获取 capabilities 文档的 ProcessOfferings 元素中的 Process 元素
        ProcessBriefType[] processList = document.getCapabilities().getProcessOfferings().getProcessArray();
        for (ProcessBriefType process : processList) {
            log.debug("Process identifier: {}", process.getIdentifier().getStringValue());
        }
        return document;
    }

    /**
     * 请求 WPS DescribeProcess 文档.
     *
     * @param url       WPS url
     * @param processId value of ows:Identifier in CapabilitiesDocument
     * @return the process description type
     */
    public static ProcessDescriptionType requestDescribeProcess(String url, String processId) throws IOException {

        WPSClientSession wpsClient = WPSClientSession.getInstance();
        ProcessDescriptionType processDescription = wpsClient.getProcessDescription(url, processId);
        log.debug("Process description: {}", processDescription.xmlText());
        // 获取处理过程的所有的输入数据描述
        InputDescriptionType[] inputList = processDescription.getDataInputs().getInputArray();
        for (InputDescriptionType input : inputList) {
            log.debug("Input identifier: {}", input.getIdentifier().getStringValue());
        }
        return processDescription;
    }

    /**
     * 构造并执行请求
     * 编码（encoding）只有在与默认的编码不一致时才需要设置。如果需要参数二进制数据值，可以设置编码为  "**base64**"
     *
     * @param url                WPS url
     * @param processId          value of ows:Identifier in CapabilitiesDocument
     * @param processDescription
     * @param inputs
     * @return
     * @throws Exception
     * @code {@code
     * // complex data by reference
     * <p>
     * inputs.put( "data",
     * "http://geoprocessing.demo.52north.org:8080/geoserver/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=topp:tasmania_roads&outputFormat=GML3");
     * // literal data
     * <p>
     * inputs.put("width", "0.05");
     * }
     */
    public static IData executeProcess(String url, String processId, ProcessDescriptionType processDescription,
                                       HashMap<String, Object> inputs, String outputName) throws Exception {
        GMLSchemas schemas = new GMLSchemas();
        // 初始化一个 ExecuteRequestBuilder 对象，用于构建一个真正的WPS Execute请求
        ExecuteRequestBuilder executeBuilder = new ExecuteRequestBuilder(processDescription);
        for (InputDescriptionType input : processDescription.getDataInputs().getInputArray()) {
            String inputName = input.getIdentifier().getStringValue();
            Object inputValue = inputs.get(inputName);
            // 字符型数据：字符串、数字
            if (input.getLiteralData() != null) {
                if (inputValue instanceof String) {
                    executeBuilder.addLiteralData(inputName, (String) inputValue);
                }
            }
            // 复合型数据，如GML Features
            else if (input.getComplexData() != null) {
                // Complex data by value
                if (inputValue instanceof FeatureCollection) {
                    IData data = new GTVectorDataBinding((FeatureCollection) inputValue);
                    executeBuilder.addComplexData(inputName, data, schemas.getFeature(), null, MimeTypes.XML);
                }
                // Complex data Reference
                // 复合型数据，如只通过**URL**获取的 GML Features
                // 指向地理数据资源的**URL**的字符串: inputValue
                else if (inputValue instanceof String) {
                    executeBuilder.addComplexDataReference(inputName, (String) inputValue, schemas.getFeature(), null,
                            MimeTypes.XML);
                }
                // 需要输入数据，但是为空
                else if (inputValue == null && input.getMinOccurs().intValue() > 0) {
                    throw new IOException("Mandatory property not set : " + inputName);
                }
            }
        }
        // 设置输出数据的格式
        executeBuilder.setMimeTypeForOutput(MimeTypes.XML, outputName);
        executeBuilder.setSchemaForOutput(schemas.getFeature(), outputName);
        // build and send the request document
        ExecuteDocument executeDocument = executeBuilder.getExecute();
        executeDocument.getExecute().setService("WPS");
        WPSClientSession wpsClientSession = WPSClientSession.getInstance();
        // 执行
        Object resp = wpsClientSession.execute(url, executeDocument);

        if (resp instanceof ExecuteResponseDocument) {
            ExecuteResponseDocument responseDocument = (ExecuteResponseDocument) resp;
            ExecuteResponseAnalyser analyser = new ExecuteResponseAnalyser(executeDocument, responseDocument,
                    processDescription);
            IData data = analyser.getComplexDataByIndex(0, GTVectorDataBinding.class);
            return data;
        }
        throw new Exception("Exception: " + resp.toString());
    }

    /**
     * 执行请求
     *
     * @param url
     * @param processId
     * @param inputs
     * @param outputName
     * @return
     * @throws Exception
     */
    public static IData executeProcess(String url, String processId, HashMap<String, Object> inputs, String outputName) throws Exception {
        ProcessDescriptionType processDescription = requestDescribeProcess(url, processId);
        return executeProcess(url, processId, processDescription, inputs, outputName);
    }
}
