package com.vtech.wps.taudem;

import org.egc.commons.util.PropertiesUtil;
import org.n52.wps.algorithm.annotation.Algorithm;
import org.n52.wps.server.AbstractAnnotatedAlgorithm;

/**
 * Description:
 * <pre>
 *
 * </pre>
 *
 * @author houzhiwei
 * @date 2018/12/24 9:17
 */
@Deprecated
@Algorithm(version = "5.3.0", abstrakt = "TauDEM algorithms.")
public class BaseTauDEMAnno extends AbstractAnnotatedAlgorithm {
    TauDEMAnalysis tauDEMAnalysis;

    public TauDEMAnalysis getInstance() {
        String workspace = PropertiesUtil.getPropertyFromConfig("workspace", "service");
        tauDEMAnalysis = TauDEMAnalysis.getInstance();
        tauDEMAnalysis.init(workspace);
        return tauDEMAnalysis;
    }
}
