/*
 *  File name:
 *      BlurKernel.java
 *
 *  ====================
 *  Description:
 *
 *
 *  ====================
 *  Sources:
 *      http://stackoverflow.com/questions/39684820/java-implementation-of-gaussian-blur
 *
 *  ====================
 *  Author:
 *      Mehdi Rouijel
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package imageprocessingops.convolution;

import imageprocessingops.math.Matrix;

import java.awt.image.BufferedImage;

import static com.sun.javafx.util.Utils.clamp;


public class
BlurKernel extends ConvoKernel
{

    private final int MAT_SIZE = 5;

    public
    BlurKernel()
    {
        this.kernelX = new Matrix( MAT_SIZE, MAT_SIZE );
        /*
        this.kernelX.data = new float[][]{
            { 0.0f,  0.0f,   0.0f,   5.0f,   0.0f,  0.0f, 0.0f },
            { 0.0f,  5.0f,  18.0f,  32.0f,  18.0f,  5.0f, 0.0f },
            { 0.0f, 18.0f,  64.0f, 100.0f,  64.0f, 18.0f, 0.0f },
            { 5.0f, 32.0f, 100.0f, 100.0f, 100.0f, 32.0f, 5.0f },
            { 0.0f, 18.0f,  64.0f, 100.0f,  64.0f, 18.0f, 0.0f },
            { 0.0f,  5.0f,  18.0f,  32.0f,  18.0f,  5.0f, 0.0f },
            { 0.0f,  0.0f,   0.0f,   5.0f,   0.0f,  0.0f, 0.0f }
        };
        */
        this.kernelX.data = new float[][]{
            { 1.0f,  4.0f,  6.0f,  4.0f, 1.0f },
            { 4.0f, 16.0f, 24.0f, 16.0f, 4.0f },
            { 6.0f, 24.0f, 36.0f, 24.0f, 6.0f },
            { 4.0f, 26.0f, 24.0f, 16.0f, 4.0f },
            { 1.0f,  4.0f,  6.0f,  4.0f, 1.0f }
        };

        this.kernelY = null;

        //this.divisor = 1068.0f;
        this.divisor = 256.0f;
    }

    public BufferedImage
    apply( BufferedImage img )
    {
        BufferedImage result = null;

        int width = img.getWidth();
        int height = img.getHeight();

        int[] rgbData = img.getRGB( 0, 0, width, height, null, 0, width );
        int[] rgbOut = new int[ rgbData.length ];

        int w = width - MAT_SIZE + 1;
        int h = height - MAT_SIZE + 1;
        for ( int y = 0; y < h; ++y )
        {
            for ( int x = 0; x < w; ++x )
            {
                int r = 0;
                int g = 0;
                int b = 0;

                for ( int kRow = 0, pixelIndex = y*width + x;
                      kRow < MAT_SIZE;
                      ++kRow, pixelIndex += width-MAT_SIZE )
                {
                    for ( int kCol = 0; kCol < MAT_SIZE; ++kCol, ++pixelIndex )
                    {
                        int rgb = rgbData[ pixelIndex ];

                        r += ( ( rgb >>> 16 ) & 0xff ) * this.kernelX.data[ kRow ][ kCol ];
                        g += ( ( rgb >>>  8 ) & 0xff ) * this.kernelX.data[ kRow ][ kCol ];
                        b += ( ( rgb        ) & 0xff ) * this.kernelX.data[ kRow ][ kCol ];
                    }
                }

                r /= this.divisor;
                g /= this.divisor;
                b /= this.divisor;

                rgbOut[ ( y + MAT_SIZE/2 )*width + ( x + MAT_SIZE/2 ) ] =
                    ( ( 0xff000000 ) | ( r << 16 ) | ( g << 8 ) | ( b ) );

            }
        }

        result = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        result.setRGB( 0, 0, width, height, rgbOut, 0, width );

        return result;
    }


    /* NOTE: I would need to rewrite a bunch of stuff to make this work...
     */
    @Override
    public float
    convolve( Matrix input )
    {
        /*
        int[] result = new int[ width * height ];

        for ( int imageRow = 0; imageRow < height-1; ++imageRow )
        {
            for ( int imageCol = 0; imageCol < width-1; ++imageCol )
            {
                double tmp = 0;

                for ( int kernelRow = 0; kernelRow < 3; ++kernelRow )
                {
                    for ( int kernelCol = 0; kernelCol < 3; ++kernelCol )
                    {
                        int inputX = clamp( imageCol+kernelCol-1, 0, width ) ;
                        int inputY = clamp( imageRow+kernelRow-1, 0, height );

                        tmp +=
                            this.kernelX.data[ kernelRow ][ kernelCol ] *
                                input[ inputY*width + inputX ];
                    }
                }

                tmp /= this.divisor;

                result[ imageRow*width + imageCol ] = ( int )tmp;
            }
        }

        return result;
        */
        return 0.0f;
    }

}
