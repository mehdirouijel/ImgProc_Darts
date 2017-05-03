/*
 *  File name:
 *      ConvoKernel.java
 *
 *  ====================
 *  Description:
 *      Abstract class for a convolution kernel that was part of some work done in class
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
import java.awt.image.BufferedImage;


public abstract class
ConvoKernel
{

    Matrix kernelX;
    Matrix kernelY;
    float divisor;

    public abstract BufferedImage apply( BufferedImage img );
    public abstract int getSize();

}
