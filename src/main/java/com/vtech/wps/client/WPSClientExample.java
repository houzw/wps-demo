package com.vtech.wps.client;

import net.opengis.wps.x100.CapabilitiesDocument;
import net.opengis.wps.x100.ProcessDescriptionType;
import org.geotools.feature.FeatureCollection;
import org.n52.wps.client.WPSClientException;
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
        WPSClientUtils.initWPSConfig();

        String wpsURL = "http://localhost:8080/wps/WebProcessingService";
        // ows:Identifier
        String processID = "org.n52.wps.server.algorithm.SimpleBufferAlgorithm";

        try {
            CapabilitiesDocument capabilitiesDoc = WPSClientUtils.requestCapabilities(wpsURL);
            ProcessDescriptionType descProDoc = WPSClientUtils.requestDescribeProcess(wpsURL, processID);
            // define inputs

            HashMap<String, Object> inputs = new HashMap<String, Object>();
            String dataRef = "http://geoprocessing.demo.52north.org:8080/geoserver/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=topp:tasmania_roads&outputFormat=GML3";
            // complex data by reference
            inputs.put("data", dataRef);
            // literal data
            inputs.put("width", "0.05");
            // 执行并获得结果
            IData data = WPSClientUtils.executeProcess(wpsURL, processID, descProDoc, inputs, "result");

            if (data instanceof GTVectorDataBinding) {
                FeatureCollection featureCollection = ((GTVectorDataBinding) data).getPayload();
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

}
