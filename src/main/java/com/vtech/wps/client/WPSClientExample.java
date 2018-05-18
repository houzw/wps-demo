package com.vtech.wps.client;

import com.vtech.wps.commons.GMLSchemas;
import com.vtech.wps.commons.MimeTypes;
import net.opengis.wps.x100.*;
import org.geotools.feature.FeatureCollection;
import org.n52.wps.client.ExecuteRequestBuilder;
import org.n52.wps.client.ExecuteResponseAnalyser;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.client.WPSClientSession;
import org.n52.wps.commons.WPSConfig;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

/**
 * TODO
 *
 * @author houzhiwei
 * @date 2018 /5/12 21:06
 */
public class WPSClientExample {
    private static final Logger log = LoggerFactory.getLogger(WPSClientExample.class);

    public static void main(String[] args) {
        //启动tomcat服务之后，测试服务
        WPSClientExample wps = new WPSClientExample();
        wps.testExecute();
    }

    public void testExecute() {
        String path = this.getClass().getResource("/").getPath();

        WPSConfig.getInstance(path + "config/wps_config_geotools.xml");

        String wpsURL = "http://localhost:8080/wps/WebProcessingService";
        // ows:Identifier
        String processID = "org.n52.wps.server.algorithm.SimpleBufferAlgorithm";

        /*try {
            ProcessDescriptionType describeProcessDocument = requestDescribeProcess(
                    wpsURL, processID);
            System.out.println(describeProcessDocument);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        try {
            CapabilitiesDocument capabilitiesDocument = requestCapabilities(wpsURL);
            ProcessDescriptionType describeProcessDocument = requestDescribeProcess(
                    wpsURL, processID);
            // define inputs
            HashMap<String, Object> inputs = new HashMap<String, Object>();
            // complex data by reference
            inputs.put(
                    "data",
                    "http://geoprocessing.demo.52north.org:8080/geoserver/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=topp:tasmania_roads&outputFormat=GML3");
            // literal data
            inputs.put("width", "0.05");
            // 执行并获得结果
            IData data = executeProcess(wpsURL, processID,
                                        describeProcessDocument, inputs);

            if (data instanceof GTVectorDataBinding) {
                FeatureCollection featureCollection = ((GTVectorDataBinding) data)
                        .getPayload();
                System.out.println(featureCollection.size());
            }
        } catch (WPSClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Request a WPS Capabilities document
     *
     * @param url the url
     * @return the capabilities document
     * @throws WPSClientException the wps client exception
     */
    public CapabilitiesDocument requestCapabilities(String url) throws WPSClientException {
        // 初始化
        WPSClientSession wpsClient = WPSClientSession.getInstance();
        // 注册url
        boolean connected = wpsClient.connect(url);
        // 发送 capabilities 请求，获取信息
        CapabilitiesDocument document = wpsClient.getWPSCaps(url);

        // 获取 capabiilities 文档的 ProcessOfferings 元素中的 Process 元素
        ProcessBriefType[] processList = document.getCapabilities().getProcessOfferings().getProcessArray();

        for (ProcessBriefType process : processList) {
            System.out.println(process.getIdentifier().getStringValue());
        }
        return document;
    }

    /**
     * 请求 WPS DescribeProcess 文档.
     *
     * @return the process description type
     */
    public ProcessDescriptionType requestDescribeProcess(String url, String processId) throws IOException {

        WPSClientSession wpsClient = WPSClientSession.getInstance();
        ProcessDescriptionType processDescription = wpsClient.getProcessDescription(url, processId);
        System.out.println("Process description:\n" + processDescription.xmlText() + "\n");
        // 获取处理过程的所有的输入数据描述
        InputDescriptionType[] inputList = processDescription.getDataInputs().getInputArray();
        for (InputDescriptionType input : inputList) {
            System.out.println(input.getIdentifier().getStringValue());
        }
        return processDescription;
    }

    /**
     * 构造并执行请求
     * 编码（encoding）只有在与默认的编码不一致时才需要设置。如果需要参数二进制数据值，可以设置编码为  "**base64**"
     *
     * @param url
     * @param processId
     * @param processDescription
     * @param inputs             // complex data by reference
     *                           inputs.put(
     *                           "data",
     *                           "http://geoprocessing.demo.52north.org:8080/geoserver/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=topp:tasmania_roads&outputFormat=GML3");
     *                           // literal data
     *                           inputs.put("width", "0.05");
     * @return
     * @throws Exception
     */
    public IData executeProcess(String url, String processId, ProcessDescriptionType processDescription,
                                HashMap<String, Object> inputs) throws Exception
    {
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
                // Complexdata by value
                if (inputValue instanceof FeatureCollection) {
                    IData data = new GTVectorDataBinding((FeatureCollection) inputValue);
                    executeBuilder.addComplexData(inputName, data, GMLSchemas.FEATURE, null, MimeTypes.XML);
                }
                // Complexdata Reference
                // 复合型数据，如只通过**URL**获取的 GML Features
                // 指向地理数据资源的**URL**的字符串: inputValue
                if (inputValue instanceof String) {
                    executeBuilder.addComplexDataReference(inputName, (String) inputValue, GMLSchemas.FEATURE, null, MimeTypes.XML);
                }
                // 需要输入数据，但是为空
                if (inputValue == null && input.getMinOccurs().intValue() > 0) {
                    throw new IOException("Property not set, but mandatory: " + inputName);
                }
            }
        }
        // 设置输出数据的格式
        executeBuilder.setMimeTypeForOutput(MimeTypes.XML, "result");
        executeBuilder.setSchemaForOutput(GMLSchemas.FEATURE, "result");
        //build and send the request document
        ExecuteDocument executeDocument = executeBuilder.getExecute();
        executeDocument.getExecute().setService("WPS");
        WPSClientSession wpsClientSession = WPSClientSession.getInstance();
        //执行
        Object resp = wpsClientSession.execute(url, executeDocument);

        if (resp instanceof ExecuteResponseDocument) {
            ExecuteResponseDocument responseDocument = (ExecuteResponseDocument) resp;
            ExecuteResponseAnalyser analyser = new ExecuteResponseAnalyser(executeDocument, responseDocument, processDescription);
            IData data = analyser.getComplexDataByIndex(0, GTVectorDataBinding.class);
            return data;
        }
        throw new Exception("Exception: " + resp.toString());
    }
}
