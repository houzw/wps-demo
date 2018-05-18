package com.vtech.test;

import com.vtech.wps.client.WPSClientExample;
import net.opengis.wps.x100.CapabilitiesDocument;
import net.opengis.wps.x100.ProcessDescriptionType;
import org.geotools.feature.FeatureCollection;
import org.junit.Test;
import org.n52.wps.client.WPSClientException;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.GTVectorDataBinding;

import java.io.IOException;
import java.util.HashMap;

/**
 * TODO
 *
 * @author houzhiwei
 * @date 2018/5/13 10:56
 */
public class WpsTest {


    /**
     * 启动服务之后，测试服务
     */
    @Test
    public void testWps() {

        WPSClientExample wps = new WPSClientExample();
        String wpsURL = "http://localhost:8080/wps/WebProcessingService";
        // ows:Identifier
        String processID = "org.n52.wps.server.algorithm.SimpleBufferAlgorithm";

        try {
            ProcessDescriptionType describeProcessDocument = wps.requestDescribeProcess(
                    wpsURL, processID);
            System.out.println(describeProcessDocument);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            CapabilitiesDocument capabilitiesDocument = wps.requestCapabilities(wpsURL);
            ProcessDescriptionType describeProcessDocument = wps.requestDescribeProcess(
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
            IData data = wps.executeProcess(wpsURL, processID,
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
}
