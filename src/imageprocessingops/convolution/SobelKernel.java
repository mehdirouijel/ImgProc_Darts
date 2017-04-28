/*
 *  File name:
 *      SobelKernel.java
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


public class
SobelKernel extends ConvoKernel
{

    public
    SobelKernel()
    {
        this.kernelX = new Matrix( 3, 3 );
        this.kernelX.data = new float[][]{
            { -1.0f, 0.0f, 1.0f },
            { -2.0f, 0.0f, 2.0f },
            { -1.0f, 0.0f, 1.0f }
        };

        this.kernelY = new Matrix( 3, 3 );
        this.kernelY.data = new float[][]{
            {  1.0f,  2.0f,  1.0f },
            {  0.0f,  0.0f,  0.0f },
            { -1.0f, -2.0f, -1.0f }
        };

        this.divisor = 4.0f;
    }

    @Override
    public float
    convolve( Matrix input )
    {
        float result = 0.0f;
        float computedX = 0.0f;
        float computedY = 0.0f;

        for ( int row = 0; row < input.rows; ++row )
        {
            for ( int col = 0; col < input.cols; ++col )
            {
                computedX += this.kernelX.data[ row ][ col ] * input.data[ row ][ col ];
                computedY += this.kernelY.data[ row ][ col ] * input.data[ row ][ col ];

                //this.computedX /= this.divisor;
                //this.computedY /= this.divisor;

                result = ( float )Math.sqrt( computedX*computedX +
                                             computedY*computedY );

                result /= this.divisor;
            }
        }

        return result;
    }

}
