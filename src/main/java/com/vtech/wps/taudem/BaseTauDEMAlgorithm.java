package com.vtech.wps.taudem;

import org.egc.commons.util.PropertiesUtil;
import org.n52.wps.server.AbstractSelfDescribingAlgorithm;

/**
 * Description:
 * <pre>
 *
 * </pre>
 *
 * @author houzhiwei
 * @date 2018/12/15 14:33
 */
@Deprecated
public abstract class BaseTauDEMAlgorithm extends AbstractSelfDescribingAlgorithm {
    TauDEMAnalysis tauDEMAnalysis;

    public TauDEMAnalysis getInstance() {
        String workspace = PropertiesUtil.getPropertyFromConfig("workspace", "service");
        tauDEMAnalysis = TauDEMAnalysis.getInstance();
        tauDEMAnalysis.init(workspace);
        return tauDEMAnalysis;
    }
}
