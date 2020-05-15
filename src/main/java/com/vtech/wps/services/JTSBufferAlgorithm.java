package com.vtech.wps.services;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.operation.buffer.BufferParameters;
import org.apache.log4j.Logger;
import org.n52.wps.algorithm.annotation.*;
import org.n52.wps.io.data.binding.complex.JTSGeometryBinding;
import org.n52.wps.server.AbstractAnnotatedAlgorithm;

/**
 * This algorithm creates a buffers around a JTS geometry using the build-in buffer-method.
 * @author BenjaminPross(bpross-52n)
 *
 */
@Algorithm(version = "1.0.0", abstrakt = "This algorithm creates a buffers around a JTS geometry using the build-in buffer-method.")
public class JTSBufferAlgorithm extends AbstractAnnotatedAlgorithm {


    private static Logger LOGGER = Logger.getLogger(JTSBufferAlgorithm.class);

    public JTSBufferAlgorithm() {
        super();
    }

    private Geometry result;
    private Geometry data;
    private double distance;
    private int quadrantSegments;
    private int endCapStyle;

    @ComplexDataOutput(identifier = "result", binding = JTSGeometryBinding.class)
    public Geometry getResult() {
        return result;
    }

    @ComplexDataInput(identifier = "data", binding = JTSGeometryBinding.class, minOccurs = 1)
    public void setData(Geometry data) {
        this.data = data;
    }

    @LiteralDataInput(identifier = "distance", minOccurs = 1)
    public void setDistance(double distance) {
        this.distance = distance;
    }

    @LiteralDataInput(identifier = "quadrantSegments", defaultValue = "" + BufferParameters.DEFAULT_QUADRANT_SEGMENTS, minOccurs = 0)
    public void setQuadrantSegments(int quadrantSegments) {
        this.quadrantSegments = quadrantSegments;
    }

    @LiteralDataInput(identifier = "endCapStyle", abstrakt = "CAP_ROUND = 1, CAP_FLAT = 2, CAP_SQUARE = 3", allowedValues = {"1", "2", "3"}, defaultValue = "" + BufferParameters.CAP_ROUND, minOccurs = 0)
    public void setEndCapStyle(int endCapStyle) {
        this.endCapStyle = endCapStyle;
    }

    @Execute
    public void runAlgorithm() {
        result = data.buffer(distance, quadrantSegments, endCapStyle);
    }

}
