package edu.colorado.phet.densityjava.model;

import java.awt.*;

public class Water extends RectangularObject {
    private DensityModel.SwimmingPool container;
    private double waterVolume;
    private DensityModel.WaterHeightMapper waterHeightMapper;

    public Water(DensityModel.SwimmingPool container, double waterVolume, DensityModel.WaterHeightMapper waterHeightMapper) {
        super("Water", container.getX(), container.getY(), container.getWidth(), 4, container.getDepth(), new Color(144, 207, 206, 128));
        this.container = container;
        this.waterVolume = waterVolume;
        this.waterHeightMapper = waterHeightMapper;
        updateWaterHeight();
    }

    public void updateWaterHeight() {
        setHeight(waterHeightMapper.getWaterHeight(waterVolume));
    }

    public double getWaterVolume() {
        return waterVolume;
    }

    public double getDistanceToTopOfPool() {
        return container.getHeight() - getHeight();
    }

    public double getDensity() {
        return 1000;
    }

    public double getBottomY() {
        return container.getY();
    }

    public double getSwimmingPoolSurfaceY() {
        return container.getY() + container.getHeight();
    }

}
