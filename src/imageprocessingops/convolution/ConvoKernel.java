package imageprocessingops.convolution;

import imageprocessingops.math.Matrix;

import java.awt.*;
import java.awt.image.BufferedImage;


public abstract class
ConvoKernel
{

    Matrix kernelX;
    Matrix kernelY;
    float divisor;
    float computedX;
    float computedY;

    public abstract float convolve( Matrix input );

}
