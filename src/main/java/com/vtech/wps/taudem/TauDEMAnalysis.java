package com.vtech.wps.taudem;

import com.google.common.collect.LinkedHashMultimap;
import org.apache.commons.lang3.StringUtils;
import org.egc.commons.command.ExecResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * <p>TauDEMAnalysis
 * @author houzhiwei
 * @date 2020-05-11T21:43:17+08:00
 */
//@Slf4j
public class TauDEMAnalysis extends BaseTauDEM {
    Logger log = LoggerFactory.getLogger(TauDEMAnalysis.class);
    /**
    * SingletonHolder is loaded on the first execution of Singleton.getInstance()
    * or the first access to SingletonHolder.INSTANCE, not before.
    * <p>此实现方式可保证単例的延迟加载和线程安全</p>
    */
    private static class SingletonHolder {
        private static final TauDEMAnalysis INSTANCE = new TauDEMAnalysis();
    }

    public static TauDEMAnalysis getInstance() {  return SingletonHolder.INSTANCE; }

    private TauDEMAnalysis() {}


   /**
    * recommend to configure it (taudem.workspace) in Spring Framework configuration files <br/>
    * (e.g., application.yml in Spring Boot)
    * @param userWorkspace re-initialize workspace/output directory <br/>
    */
    public void init(String userWorkspace) {
        if (StringUtils.isBlank(userWorkspace)) {
            workspace = System.getProperty("java.io.tmpdir");
        }
        workspace = userWorkspace;
    }
    /**
     * d8FlowDirections
     * <p> Creates 2 grids.
     * @param params {@link D8FlowDirectionsParams}
     * @return {@link ExecResult}
     */
    public ExecResult d8FlowDirections(D8FlowDirectionsParams params){
        log.debug("Start d8FlowDirections...");
        Map<String,String> files = new LinkedHashMap<>();
        Map<String,String> outFiles = new LinkedHashMap<>();
        LinkedHashMultimap<String, Object> extraParams = LinkedHashMultimap.<String, Object>create();
        if (StringUtils.isNotBlank(workspace)) {
            params.setOutputDir(workspace);
        }
        // A grid of elevation values.
        files.put("-fel", params.getPitFilledElevation());
        // A grid of D8 flow directions which are defined, for each cell, as the direction of the one of its eight adjacent or diagonal neighbors with the steepest downward slope.
        outFiles.put("-p", params.getD8FlowDirection());
        // A grid giving slope in the D8 flow direction.
        outFiles.put("-sd8", params.getD8Slope());
        ExecResult result = run("D8FlowDir", files, outFiles, extraParams, params.getOutputDir());
        result.setParams(params);
        if(result.getSuccess()){
            log.debug("d8FlowDirections finished");
        }else{
            log.error("d8FlowDirections failed!");
        }
        return result;
    }
    /**
    * pitRemove
    * <p> This funciton identifies all pits in the DEM and raises their elevation to the level of the lowest pour point around their edge.
    * @param params {@link PitRemoveParams}
    * @return {@link ExecResult}
    */
    public ExecResult pitRemove(PitRemoveParams params){
        log.debug("Start pitRemove...");
        Map<String,String> files = new LinkedHashMap<>();
        Map<String,String> outFiles = new LinkedHashMap<>();
        LinkedHashMultimap<String, Object> extraParams = LinkedHashMultimap.<String, Object>create();
        if (StringUtils.isNotBlank(workspace)) {
            params.setOutputDir(workspace);
        }
        // A digital elevation model (DEM) grid to serve as the base input for the terrain analysis and stream delineation.
        files.put("-z", params.getElevation());
        // If this option is selected Fill ensures that the grid is hydrologically conditioned with cell to cell connectivity in only 4 directions (N, S, E or W neighbors).
        extraParams.put("-4way", params.getFillConsideringOnly4WayNeighbors());
        // 
        files.put("-depmask", params.getDepressionMask());
        // A grid of elevation values with pits removed so that flow is routed off of the domain.
        outFiles.put("-fel", params.getPitFilledElevation());
        ExecResult result = run("PitRemove", files, outFiles, extraParams, params.getOutputDir());
        result.setParams(params);
        if(result.getSuccess()){
            log.debug("pitRemove finished");
        }else{
            log.error("pitRemove failed!");
        }
        return result;
    }

}