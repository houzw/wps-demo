package com.vtech.wps.taudem;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.egc.commons.command.Params;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;

/**
 * Wrapper of parameters of pitRemove
 *
 * @author houzhiwei
 * @date 2020-05-11T21:43:17+08:00
 */
//@Data
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class PitRemoveParams implements Params {


    /**
     * <pre>
     * elevation
     * depressionMask
     *  </pre>
     *
     * @see Builder#Builder(String)
     */
    public PitRemoveParams() {
    }

    public Boolean getFillConsideringOnly4WayNeighbors() {
        return fillConsideringOnly4WayNeighbors;
    }

    public String getDepressionMask() {
        return depressionMask;
    }

    /**
     * A digital elevation model (DEM) grid to serve as the base input for the terrain analysis and stream delineation.
     */
    @NotNull
    private String elevation;

    /**
     * If this option is selected Fill ensures that the grid is hydrologically conditioned with cell to cell connectivity in only 4 directions (N, S, E or W neighbors).
     */
    private Boolean fillConsideringOnly4WayNeighbors;

    /**
     *
     */
    private String depressionMask;

    public String getElevation() {
        return elevation;
    }

    /**
     * A grid of elevation values with pits removed so that flow is routed off of the domain.
     */
    private String pitFilledElevation;

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public String getOutputDir() {
        return outputDir;
    }

    //@Setter
    @XmlElement
    private String outputDir = System.getProperty("java.io.tmpdir");

    // if no output filename provided
    public String getPitFilledElevation() {
        if (StringUtils.isBlank(pitFilledElevation)) {
            return FilenameUtils.normalize(outputDir + File.separator + namingOutput(elevation, "pitFilledElevation", "Raster Dataset", "tif"));
        }
        return this.pitFilledElevation;
    }

    private PitRemoveParams(Builder builder) {
        this.elevation = builder.elevation;
        this.fillConsideringOnly4WayNeighbors = builder.fillConsideringOnly4WayNeighbors;
        this.depressionMask = builder.depressionMask;
        this.pitFilledElevation = builder.pitFilledElevation;
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class Builder {
        private String elevation;
        private Boolean fillConsideringOnly4WayNeighbors;
        private String depressionMask;
        private String pitFilledElevation;

        /**
         * @param elevation A digital elevation model (DEM) grid to serve as the base input for the terrain analysis and stream delineation.
         */
        public Builder(@NotBlank String elevation) {
            this.elevation = elevation;
        }

        public Builder elevation(String val) {
            this.elevation = val;
            return this;
        }

        public Builder fillConsideringOnly4WayNeighbors(Boolean val) {
            this.fillConsideringOnly4WayNeighbors = val;
            return this;
        }

        public Builder depressionMask(String val) {
            this.depressionMask = val;
            return this;
        }

        public Builder pitFilledElevation(String val) {
            this.pitFilledElevation = val;
            return this;
        }

        public PitRemoveParams build() {
            return new PitRemoveParams(this);
        }
    }
}