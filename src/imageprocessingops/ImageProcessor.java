/*
 *  File name:
 *      ImageProcessor.java
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

package imageprocessingops;

import java.awt.*;
import java.awt.image.BufferedImage;

import imageprocessingops.convolution.ConvoKernel;
import imageprocessingops.math.Matrix;


public class
ImageProcessor
{

    private BufferedImage inputImage = null;
    private ConvoKernel kernel = null;

    public
    ImageProcessor()
    {
    }
    public
    ImageProcessor( ConvoKernel theKernel )
    {
        this.kernel = theKernel;
    }
    public
    ImageProcessor( ConvoKernel theKernel, BufferedImage theImage )
    {
        this.kernel = theKernel;
        this.inputImage = theImage;
    }

    public void
    setKernel( ConvoKernel theKernel )
    {
        this.kernel = theKernel;
    }
    public void
    setInputImage( BufferedImage theImage )
    {
        this.inputImage = theImage;
    }


    public BufferedImage
    runKernel()
    {
        int height = this.inputImage.getHeight();
        int width = this.inputImage.getWidth();
        BufferedImage result = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );

        for ( int row = 1; row < height - 1; ++row )
        {
            for ( int col = 1; col < width - 1; ++col )
            {
                Matrix imageChunk = extractValueImageChunk( 3, row, col );

                float newPixel = kernel.convolve( imageChunk );
                result.setRGB( col, row, Color.HSBtoRGB( 0.0f, 0.0f, newPixel ) );
            }
        }

        return result;
    }


    private Matrix
    extractValueImageChunk( int squareMatrixSize,
                            int centerRow,
                            int centerCol )
    {
        int[] pixel = new int[ squareMatrixSize ];
        float[] hsv = new float[ squareMatrixSize ];

        pixel[ 0 ] = new Color( this.inputImage.getRGB( centerCol-1, centerRow-1 ) ).getRed();
        pixel[ 1 ] = new Color( this.inputImage.getRGB( centerCol-1, centerRow-1 ) ).getGreen();
        pixel[ 2 ] = new Color( this.inputImage.getRGB( centerCol-1, centerRow-1 ) ).getBlue();
        Color.RGBtoHSB( pixel[ 0 ], pixel[ 1 ], pixel[ 2 ], hsv );
        float value00 = hsv[ 2 ];

        pixel[ 0 ] = new Color( this.inputImage.getRGB( centerCol  , centerRow-1 ) ).getRed();
        pixel[ 1 ] = new Color( this.inputImage.getRGB( centerCol  , centerRow-1 ) ).getGreen();
        pixel[ 2 ] = new Color( this.inputImage.getRGB( centerCol  , centerRow-1 ) ).getBlue();
        Color.RGBtoHSB( pixel[ 0 ], pixel[ 1 ], pixel[ 2 ], hsv );
        float value01 = hsv[ 2 ];

        pixel[ 0 ] = new Color( this.inputImage.getRGB( centerCol+1, centerRow-1 ) ).getRed();
        pixel[ 1 ] = new Color( this.inputImage.getRGB( centerCol+1, centerRow-1 ) ).getGreen();
        pixel[ 2 ] = new Color( this.inputImage.getRGB( centerCol+1, centerRow-1 ) ).getBlue();
        Color.RGBtoHSB( pixel[ 0 ], pixel[ 1 ], pixel[ 2 ], hsv );
        float value02 = hsv[ 2 ];

        pixel[ 0 ] = new Color( this.inputImage.getRGB( centerCol-1, centerRow   ) ).getRed();
        pixel[ 1 ] = new Color( this.inputImage.getRGB( centerCol-1, centerRow   ) ).getGreen();
        pixel[ 2 ] = new Color( this.inputImage.getRGB( centerCol-1, centerRow   ) ).getBlue();
        Color.RGBtoHSB( pixel[ 0 ], pixel[ 1 ], pixel[ 2 ], hsv );
        float value10 = hsv[ 2 ];

        pixel[ 0 ] = new Color( this.inputImage.getRGB( centerCol  , centerRow   ) ).getRed();
        pixel[ 1 ] = new Color( this.inputImage.getRGB( centerCol  , centerRow   ) ).getGreen();
        pixel[ 2 ] = new Color( this.inputImage.getRGB( centerCol  , centerRow   ) ).getBlue();
        Color.RGBtoHSB( pixel[ 0 ], pixel[ 1 ], pixel[ 2 ], hsv );
        float value11 = hsv[ 2 ];

        pixel[ 0 ] = new Color( this.inputImage.getRGB( centerCol+1, centerRow   ) ).getRed();
        pixel[ 1 ] = new Color( this.inputImage.getRGB( centerCol+1, centerRow   ) ).getGreen();
        pixel[ 2 ] = new Color( this.inputImage.getRGB( centerCol+1, centerRow   ) ).getBlue();
        Color.RGBtoHSB( pixel[ 0 ], pixel[ 1 ], pixel[ 2 ], hsv );
        float value12 = hsv[ 2 ];

        pixel[ 0 ] = new Color( this.inputImage.getRGB( centerCol-1, centerRow+1 ) ).getRed();
        pixel[ 1 ] = new Color( this.inputImage.getRGB( centerCol-1, centerRow+1 ) ).getGreen();
        pixel[ 2 ] = new Color( this.inputImage.getRGB( centerCol-1, centerRow+1 ) ).getBlue();
        Color.RGBtoHSB( pixel[ 0 ], pixel[ 1 ], pixel[ 2 ], hsv );
        float value20 = hsv[ 2 ];

        pixel[ 0 ] = new Color( this.inputImage.getRGB( centerCol  , centerRow+1 ) ).getRed();
        pixel[ 1 ] = new Color( this.inputImage.getRGB( centerCol  , centerRow+1 ) ).getGreen();
        pixel[ 2 ] = new Color( this.inputImage.getRGB( centerCol  , centerRow+1 ) ).getBlue();
        Color.RGBtoHSB( pixel[ 0 ], pixel[ 1 ], pixel[ 2 ], hsv );
        float value21 = hsv[ 2 ];

        pixel[ 0 ] = new Color( this.inputImage.getRGB( centerCol+1, centerRow+1 ) ).getRed();
        pixel[ 1 ] = new Color( this.inputImage.getRGB( centerCol+1, centerRow+1 ) ).getGreen();
        pixel[ 2 ] = new Color( this.inputImage.getRGB( centerCol+1, centerRow+1 ) ).getBlue();
        Color.RGBtoHSB( pixel[ 0 ], pixel[ 1 ], pixel[ 2 ], hsv );
        float value22 = hsv[ 2 ];

        Matrix imageChunk = new Matrix( 3, 3 );
        imageChunk.data = new float[][] {
                { value00, value01, value02 },
                { value10, value11, value12 },
                { value20, value21, value22 }
        };

        return imageChunk;
    }


    public BufferedImage
    runHough( BufferedImage inputImage )
    {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        int maxDistance = ( int )Math.sqrt( width*width + height*height );

        int accumulatorWidth = 180;
        int accumulatorHeight = maxDistance;
        // NOTE: 2D array should have every element initialised to 0.
        int[][] accumulator = new int[ accumulatorHeight ][ accumulatorWidth ];
        BufferedImage houghSpace = new BufferedImage( accumulatorWidth,
                                                      accumulatorHeight,
                                                      BufferedImage.TYPE_INT_RGB );
        int maxValue = 0;

        for ( int y = 0; y < height; ++y )
        {
            for ( int x = 0; x < width; ++x )
            {
                if ( inputImage.getRGB( x, y ) != -16777216 )
                {
                    for ( int theta = 0; theta < 180; ++theta )
                    {
                        int rho = ( int )(
                            x*Math.cos( ( theta * Math.PI ) / 180.0 ) +
                            y*Math.sin( ( theta * Math.PI ) / 180.0 )
                        );

                        if ( ( rho > 0 ) && ( rho <= maxDistance ) )
                        {
                            accumulator[ rho ][ theta ] = accumulator[ rho ][ theta ] + 1;

                            if ( accumulator[ rho ][ theta ] > maxValue )
                            {
                                maxValue = accumulator[ rho ][ theta ];
                            }
                        }
                    }
                }
            }
        }

        int x = 0;
        int y = 0;
        for ( int row = 0; row < accumulatorHeight; ++row )
        {
            for ( int col = 0; col < accumulatorWidth; ++col )
            {
                int v = 255 * accumulator[ row ][ col ] / maxValue;
                //int v = 255 - ( int )value;
                //houghSpace.setRGB( col, row, Color.HSBtoRGB( 0.0f, 0.0f, v ) );
                houghSpace.setRGB( col, row, new Color( v, v, v ).getRGB() );
            }
        }

        return houghSpace;
    }


}
