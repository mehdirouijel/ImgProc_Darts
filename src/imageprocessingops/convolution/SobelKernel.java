/*
 *  File name:
 *      SobelKernel.java
 *
 *  ====================
 *  Description:
 *      Sobel kernel and convolution.
 *      The convolution takes in a colour image that it converts to greyscale.
 *
 *  ====================
 *  Sources:
 *      https://en.wikipedia.org/wiki/Grayscale#Colorimetric_.28luminance-preserving.29_conversion_to_grayscale
 *
 *  ====================
 *  Author:
 *      Mehdi Rouijel
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package imageprocessingops.convolution;

import imageprocessingops.math.Matrix;
import java.awt.image.BufferedImage;


public class
SobelKernel extends ConvoKernel
{

    private final int MAT_SIZE = 3;

    public
    SobelKernel()
    {
        this.kernelX = new Matrix( MAT_SIZE, MAT_SIZE );
        this.kernelX.data = new float[][]{
            { -1.0f, 0.0f, 1.0f },
            { -2.0f, 0.0f, 2.0f },
            { -1.0f, 0.0f, 1.0f }
        };

        this.kernelY = new Matrix( MAT_SIZE, MAT_SIZE );
        this.kernelY.data = new float[][]{
            {  1.0f,  2.0f,  1.0f },
            {  0.0f,  0.0f,  0.0f },
            { -1.0f, -2.0f, -1.0f }
        };

        this.divisor = 1.0f;
    }

    @Override
    public BufferedImage
    apply( BufferedImage img )
    {
        BufferedImage result = null;

        int width = img.getWidth();
        int height = img.getHeight();

        int[] rgbData = img.getRGB( 0, 0, width, height, null, 0, width );
        int[] rgbOut = new int[ rgbData.length ];


        // NOTE: Still ignoring those edge pixels...
        int w = width - MAT_SIZE;
        int h = height - MAT_SIZE;
        for ( int y = MAT_SIZE; y < h-2; ++y )
        {
            for ( int x = MAT_SIZE; x < w-2; ++x )
            {
                int rX = 0; int gX = 0; int bX = 0;
                int rY = 0; int gY = 0; int bY = 0;
                int r = 0; int g = 0; int b = 0;

                for ( int kRow = 0, pixelIndex = y*width + x;
                      kRow < MAT_SIZE;
                      ++kRow, pixelIndex+=w )
                {
                    for ( int kCol = 0; kCol < MAT_SIZE; ++kCol, ++pixelIndex )
                    {
                        int rgb = rgbData[ pixelIndex ];

                        rX += ( ( rgb >>> 16 ) & 0xff ) * this.kernelX.data[ kRow ][ kCol ];
                        gX += ( ( rgb >>>  8 ) & 0xff ) * this.kernelX.data[ kRow ][ kCol ];
                        bX += ( ( rgb        ) & 0xff ) * this.kernelX.data[ kRow ][ kCol ];

                        rY += ( ( rgb >>> 16 ) & 0xff ) * this.kernelY.data[ kRow ][ kCol ];
                        gY += ( ( rgb >>>  8 ) & 0xff ) * this.kernelY.data[ kRow ][ kCol ];
                        bY += ( ( rgb        ) & 0xff ) * this.kernelY.data[ kRow ][ kCol ];
                    }
                }

                r = ( int )Math.sqrt( rX*rX + rY*rY );
                g = ( int )Math.sqrt( gX*gX + gY*gY );
                b = ( int )Math.sqrt( bX*bX + bY*bY );

                r /= this.divisor;
                g /= this.divisor;
                b /= this.divisor;

                //int grey = ( int )( 0.2126*r + 0.7152*g + 0.0722*b );

                rgbOut[ ( y + MAT_SIZE/2 )*width + ( x + MAT_SIZE/2 ) ] =
                        ( ( 0xff000000 ) | ( r << 16 ) | ( g << 8 ) | ( b ) );
            }
        }

        result = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        result.setRGB( 0, 0, width, height, rgbOut, 0, width );

        return result;
    }

    @Override
    public int
    getSize()
    {
        return this.kernelX.cols;
    }

}
