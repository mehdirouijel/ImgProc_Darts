/*
 *  File name:
 *      ConvoKernel.java
 *
 *  ====================
 *  Description:
 *
 *
 *  ====================
 *  Sources:
 *
 *  ====================
 *  Author:
 *      Mehdi Rouijel
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

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

    public abstract float convolve( Matrix input );
    //public abstract int[] convolve( int[] input, int width, int height );

}
