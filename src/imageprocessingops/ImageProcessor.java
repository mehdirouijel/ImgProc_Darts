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
 *      https://rosettacode.org/wiki/Image_convolution#Java
 *      http://vase.essex.ac.uk/software/HoughTransform/HoughTransform.java.html
 *      http://homepages.inf.ed.ac.uk/rbf/HIPR2/thin.htm
 *
 *  ====================
 *  Author:
 *      Mehdi Rouijel
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package imageprocessingops;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import imageprocessingops.convolution.BlurKernel;
import imageprocessingops.convolution.ConvoKernel;
import imageprocessingops.convolution.SobelKernel;
import imageprocessingops.math.Matrix;
import imageprocessingops.math.HoughPoint;


public class
ImageProcessor
{

    private BufferedImage inputImage = null;
    private int[] rgbData = null;
    private ConvoKernel kernel = null;
    private int[][] accumulator = null;
    private int nbOfLines;

    public
    ImageProcessor()
    {
        this.nbOfLines = 32;
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
        int width = theImage.getWidth();
        int height = theImage.getHeight();

        this.inputImage = theImage;
        this.rgbData = this.inputImage.getRGB( 0, 0, width, height, null, 0, width );
    }
    public void
    setNbOfLines( int nbOfLines )
    {
        this.nbOfLines = nbOfLines;
    }


    private BufferedImage
    rgbDataToImage( int[][] rgbData, int width, int height )
    {
        BufferedImage result = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );

        for ( int y = 0; y < height; ++y )
        {
            for ( int x = 0; x < width; ++x )
            {
                int r = rgbData[ 0 ][ y*width + x ];
                int g = rgbData[ 1 ][ y*width + x ];
                int b = rgbData[ 2 ][ y*width + x ];

                int rgb = ( ( r << 16 ) | ( g << 8 ) | ( b ) | ( 0xFF000000 ) );

                result.setRGB( x, y, rgb );
            }
        }

        return result;
    }

    public BufferedImage
    runKernel( BufferedImage img )
    {
        final int THRESH_HI = 45;
        final int THRESH_LO = 35;
        final int STRG_EDGE =  1;
        final int WEAK_EDGE = -1;
        final int NO_EDGE   =  0;

        int height = img.getHeight();
        int width = img.getWidth();
        BufferedImage result = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        int[][] blobAnalysis = new int[ height ][ width ];

        if ( this.kernel instanceof SobelKernel )
        {
            // NOTE: 3 away from the border because the bluring creates an edge by making
            // the border pixels transparent.
            // There is probably a better way to deal with this.
            for ( int row = 3; row < height - 3; ++row )
            {
                for ( int col = 3; col < width - 3; ++col )
                {
                        Matrix imageChunk = extractValueImageChunk( 3, row, col );

                        float newPixel = kernel.convolve( imageChunk );

                        result.setRGB( col, row, Color.HSBtoRGB( 0.0f, 0.0f, newPixel ) );
                }
            }

            /* THRESHOLDING
             * Gives a better edge detection.
             */

            for ( int y = 0; y < result.getHeight(); ++y )
            {
                for ( int x = 0; x < result.getWidth(); ++x )
                {
                    int r = new Color( result.getRGB( x, y ) ).getRed();
                    int g = new Color( result.getRGB( x, y ) ).getGreen();
                    int b = new Color( result.getRGB( x, y ) ).getBlue();

                    int avg = ( r + g + b ) / 3;

                    if ( avg >= THRESH_HI )
                    {
                        // NOTE: Strong edge pixel.
                        result.setRGB( x, y, 0xffffffff );
                        blobAnalysis[ y ][ x ] = STRG_EDGE;
                    }
                    else if ( avg >= THRESH_LO )
                    {
                        // NOTE: Weak edge pixel.
                        result.setRGB( x, y, 0xff000000 );
                        blobAnalysis[ y ][ x ] = WEAK_EDGE;
                    }
                    else
                    {
                        // NOTE: Just noise.
                        result.setRGB( x, y, 0xff000000 );
                        blobAnalysis[ y ][ x ] = NO_EDGE;
                    }
                }
            }

            /* BLOB ANALYSIS
             * Keep "weak" points if connected to "strong" ones.
             */
            for ( int y = 0; y < result.getHeight(); ++y )
            {
                for ( int x = 0; x < result.getWidth(); ++x )
                {
                    if ( blobAnalysis[ y ][ x ] == WEAK_EDGE )
                    {
                        if ( ( blobAnalysis[ y-1 ][ x-1 ] == STRG_EDGE ) ||
                             ( blobAnalysis[ y-1 ][ x   ] == STRG_EDGE ) ||
                             ( blobAnalysis[ y-1 ][ x+1 ] == STRG_EDGE ) ||
                             ( blobAnalysis[ y   ][ x-1 ] == STRG_EDGE ) ||
                             ( blobAnalysis[ y   ][ x+1 ] == STRG_EDGE ) ||
                             ( blobAnalysis[ y+1 ][ x-1 ] == STRG_EDGE ) ||
                             ( blobAnalysis[ y+1 ][ x   ] == STRG_EDGE ) ||
                             ( blobAnalysis[ y+1 ][ x+1 ] == STRG_EDGE ) )
                        {
                            // NOTE: The weak point is connected to a strong point.
                            result.setRGB( x, y, 0xffffffff );
                        }
                    }
                }
            }

            /* THINNING
             * kernel format : 1 = foreground colour
             *                 0 = background colour
             *                -1 = don't care
             */
            boolean somethingChanged = true;
            BufferedImage tmpImg = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
            int[][] a = {
                { 0, 0, 0,
                  -1, 1, -1,
                  1, 1, 1},
                { 1, -1, 0,
                  1, 1, 0,
                  1, -1, 0 },
                { 1, 1, 1,
                  -1, 1, -1,
                  0, 0, 0 },
                { 0, -1, 1,
                  0, 1, 1,
                  0, -1, 1 }
            };

            int[][] b = {
                { -1, 0, 0,
                  1, 1, 0,
                  -1, 1, -1 },
                { -1, 1, -1,
                  1, 1, 0,
                  -1, 0, 0 },
                { -1, 1, -1,
                  0, 1, 1,
                  0, 0, -1 },
                { 0, 0, -1,
                  0, 1, 1,
                  -1, 1, -1 }
            };

            //while ( somethingChanged )
            //{
                somethingChanged = false;

                for ( int i = 0; i < 4; ++i )
                {
                    for ( int y = 1; y < height-1; ++y )
                    {
                        for ( int x = 1; x < width-1; ++x )
                        {
                            int nw = ( new Color( result.getRGB( x-1, y-1 ) ).getRed() % 254 > 0) ? 1 : 0;
                            int n  = ( new Color( result.getRGB(   x, y-1 ) ).getRed() % 254 > 0) ? 1 : 0;
                            int ne = ( new Color( result.getRGB( x+1, y-1 ) ).getRed() % 254 > 0) ? 1 : 0;
                            int w  = ( new Color( result.getRGB( x-1,   y ) ).getRed() % 254 > 0) ? 1 : 0;
                            int c  = ( new Color( result.getRGB(   x,   y ) ).getRed() % 254 > 0) ? 1 : 0;
                            int e  = ( new Color( result.getRGB( x+1,   y ) ).getRed() % 254 > 0) ? 1 : 0;
                            int sw = ( new Color( result.getRGB( x-1, y+1 ) ).getRed() % 254 > 0) ? 1 : 0;
                            int s  = ( new Color( result.getRGB(   x, y+1 ) ).getRed() % 254 > 0) ? 1 : 0;
                            int se = ( new Color( result.getRGB( x+1, y+1 ) ).getRed() % 254 > 0) ? 1 : 0;
                            if ( ( nw > 0 ) &&
                                 (  n > 0 ) &&
                                 ( ne > 0 ) &&
                                 (  w > 0 ) &&
                                 (  c > 0 ) &&
                                 (  e > 0 ) &&
                                 ( sw > 0 ) &&
                                 (  s > 0 ) &&
                                 ( se > 0 ) )
                            {
                                if ( ( nw == a[ i ][ 0 ] ) &&
                                     (  n == a[ i ][ 1 ] ) &&
                                     ( ne == a[ i ][ 2 ] ) &&
                                     (  w == a[ i ][ 3 ] ) &&
                                     (  c == a[ i ][ 4 ] ) &&
                                     (  e == a[ i ][ 5 ] ) &&
                                     ( sw == a[ i ][ 6 ] ) &&
                                     (  s == a[ i ][ 7 ] ) &&
                                     ( se == a[ i ][ 8 ] ) )
                                {
                                    tmpImg.setRGB( x, y, 0xffffffff );
                                    somethingChanged = true;
                                }
                            }
                            else
                            {
                                tmpImg.setRGB( x, y, 0xff000000 );
                            }

                            /*
                            if ( ( new Color( result.getRGB( x-1, y-1 ) ).getRed() % 254 == b[ i ][ 0 ] ) &&
                                 ( new Color( result.getRGB(   x, y-1 ) ).getRed() % 254 == b[ i ][ 1 ] ) &&
                                 ( new Color( result.getRGB( x+1, y-1 ) ).getRed() % 254 == b[ i ][ 2 ] ) &&
                                 ( new Color( result.getRGB( x-1,   y ) ).getRed() % 254 == b[ i ][ 3 ] ) &&
                                 ( new Color( result.getRGB(   x,   y ) ).getRed() % 254 == b[ i ][ 4 ] ) &&
                                 ( new Color( result.getRGB( x+1,   y ) ).getRed() % 254 == b[ i ][ 5 ] ) &&
                                 ( new Color( result.getRGB( x-1, y+1 ) ).getRed() % 254 == b[ i ][ 6 ] ) &&
                                 ( new Color( result.getRGB(   x, y+1 ) ).getRed() % 254 == b[ i ][ 7 ] ) &&
                                 ( new Color( result.getRGB( x+1, y+1 ) ).getRed() % 254 == b[ i ][ 8 ] ) )
                            {
                                result.setRGB( x, y, 0xffffffff );
                                somethingChanged = true;
                            }
                            else
                            {
                                result.setRGB( x, y, 0xff000000 );
                            }
                            */
                        }
                    }
                }
            //}

            result = tmpImg;
        }
        else if ( this.kernel instanceof BlurKernel )
        {
                    /*
                    int[][] rgbChannels = extractRGBChannels();

                    for ( int i = 0; i < 3; ++i )
                    {
                        rgbChannels[ i ] = kernel.convolve( rgbChannels[ i ], width, height );

                    }
                    //result.setRGB( col, row, rgb );

                    result = rgbDataToImage( rgbChannels, width, height );
                    */

            result = ( ( BlurKernel ) this.kernel ).apply( img );
        }

        return result;
    }
    public BufferedImage
    runKernel()
    {
        BufferedImage result = runKernel( this.inputImage );

        return result;
    }


    /*
    private int[][]
    extractRGBChannels()
    {
        int width = this.inputImage.getWidth();
        int height = this.inputImage.getHeight();
        int[] rChannel = new int[ width*height ];
        int[] gChannel = new int[ width*height ];
        int[] bChannel = new int[ width*height ];

        for ( int y = 0; y < height; ++y )
        {
            for ( int x = 0; x < width; ++x )
            {
                int rgb = this.rgbData[ y*width + x ];
                rChannel[ y*width + x ] = ( rgb >>> 16 ) & 0xff;
                gChannel[ y*width + x ] = ( rgb >>>  8 ) & 0xff;
                bChannel[ y*width + x ] = ( rgb        ) & 0xff;
            }
        }

        return ( new int[][] { rChannel, gChannel, bChannel } );
    }
    */


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

        int accumulatorWidth = maxDistance;
        int accumulatorHeight = 180;
        // NOTE: 2D array should have every element initialised to 0.
        accumulator = new int[ accumulatorHeight ][ accumulatorWidth ];
        BufferedImage houghSpace = new BufferedImage( accumulatorWidth,
                                                      accumulatorHeight,
                                                      BufferedImage.TYPE_INT_ARGB );
        int maxValue = 0;

        for ( int y = 0; y < height; ++y )
        {
            for ( int x = 0; x < width; ++x )
            {
                // NOTE: '-16777216' == '0xFF000000' ( == black in ARGB )
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
                            accumulator[ theta ][ rho ] = accumulator[ theta ][ rho ] + 1;

                            if ( accumulator[ theta ][ rho ] > maxValue )
                            {
                                maxValue = accumulator[ theta ][ rho ];
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


    private ArrayList< HoughPoint >
    findMaxima( BufferedImage houghed )
    {
        ArrayList< HoughPoint > maxima = new ArrayList<>();
        maxima.add( new HoughPoint( 0, 0, ( houghed.getRGB( 0, 0 ) & 0x00ffffff ) ) );

        for ( int theta = 0; theta < houghed.getHeight(); ++theta )
        {
            for ( int rho = 0; rho < houghed.getWidth(); ++rho )
            {
                if ( maxima.size() > 1 )
                {
                    Collections.sort( maxima, Collections.reverseOrder() );
                }

                int value = ( houghed.getRGB( rho, theta ) & 0x00ffffff );

                int i = 0;
                while ( ( i < maxima.size() ) && ( i < 20 ) )
                {
                    if ( value > maxima.get( i ).value )
                    {
                        double actualTheta = ( theta * Math.PI ) / 180.0;
                        maxima.add( i, new HoughPoint( rho, actualTheta, value ) );
                        break;
                    }

                    i++;
                }
            }
        }

        //return maxima;
        return new ArrayList<>( maxima.subList( 0, this.nbOfLines ) );
    }

    public BufferedImage[]
    drawHoughLines( BufferedImage houghed, int width, int height )
    {
        BufferedImage lined = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        BufferedImage houghImage = new BufferedImage( houghed.getWidth(),
                                                      houghed.getHeight(),
                                                      BufferedImage.TYPE_INT_ARGB );

        ArrayList< HoughPoint > maxima = findMaxima( houghed );

        Graphics2D houghLines = lined.createGraphics();
        Graphics2D houghPoints = houghImage.createGraphics();
        houghLines.drawImage( this.inputImage, 0, 0, null );
        houghPoints.drawImage( houghed, 0, 0, null );
        houghLines.setColor( Color.red );
        houghPoints.setColor( Color.red );

        for ( HoughPoint c : maxima )
        {
            int rho = c.rho;
            double theta = c.theta * ( 180 / Math.PI );
            //System.out.println( "rho: "+rho+" ; theta: "+theta+" ; value: "+c.value );


            double cosT = Math.cos( c.theta );
            double sinT = Math.sin( c.theta );
            double x0 = cosT * rho;
            double y0 = sinT * rho;

            int x1 = ( int ) ( x0 + 1000 * ( -sinT ) );
            int y1 = ( int ) ( y0 + 1000 * (  cosT ) );
            int x2 = ( int ) ( x0 - 1000 * ( -sinT ) );
            int y2 = ( int ) ( y0 - 1000 * (  cosT ) );

            houghLines.drawLine( x1, y1, x2, y2 );
            houghPoints.drawLine( rho-5,   ( int )( theta ), rho+5,   ( int )( theta ) );
            houghPoints.drawLine(   rho, ( int )( theta )-5,   rho, ( int )( theta )+5 );
        }
        houghLines.dispose();
        houghPoints.dispose();

        return new BufferedImage[] { lined, houghImage };
    }

}
