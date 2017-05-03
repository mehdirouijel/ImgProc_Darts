/*
 *  File name:
 *      ImageProcessor.java
 *
 *  ====================
 *  Description:
 *      The heart of the application, where the image processing algorithms are
 *      actually implemented.
 *
 *  ====================
 *  Sources:
 *      https://rosettacode.org/wiki/Image_convolution#Java
 *      http://vase.essex.ac.uk/software/HoughTransform/HoughTransform.java.html
 *      http://homepages.inf.ed.ac.uk/rbf/HIPR2/thin.htm
 *      http://northstar-www.dartmouth.edu/doc/idl/html_6.2/Thinning_Image_Objects.html
 *      https://math.stackexchange.com/a/1607673
 *      http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.1.8792&rep=rep1&type=pdf
 *      http://coding-experiments.blogspot.fr/2011/05/ellipse-detection-in-image-by-using.html
 *
 *  ====================
 *  Author:
 *      Mehdi Rouijel
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package imageprocessingops;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import dartsproject.Context;
import imageprocessingops.convolution.ConvoKernel;
import imageprocessingops.convolution.SobelKernel;
import imageprocessingops.math.HoughPoint;


public class
ImageProcessor
{

    private BufferedImage inputImage = null;
    private ConvoKernel kernel = null;
    private int nbOfLines;
    private boolean thresholding = false;
    private boolean blobAnalysis = false;
    private boolean thinning = false;
    private float sobelLoThresh;
    private float sobelHiThresh;
    private int ellipsesCount;
    private boolean showEllipseCenter;
    private boolean showEllipsesFound;

    public
    ImageProcessor()
    {
        this.nbOfLines = 10;
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
    public void
    setNbOfLines( int nbOfLines )
    {
        this.nbOfLines = nbOfLines;
    }
    public void
    setThresholding( boolean b )
    {
        this.thresholding = b;
    }
    public void
    setBlobAnalysis( boolean b )
    {
        this.blobAnalysis = b;
    }
    public void
    setThinning( boolean b )
    {
        this.thinning = b;
    }
    public void
    setSobelLoThresh( float f )
    {
        this.sobelLoThresh = f;
    }
    public void
    setSobelHiThresh( float f )
    {
        this.sobelHiThresh = f;
    }
    public void
    setEllipsesCount( int i )
    {
        this.ellipsesCount = i;
    }
    public void
    setShowEllipseCenter( boolean b )
    {
        this.showEllipseCenter = b;
    }
    public void
    setShowEllipsesFound( boolean b )
    {
        this.showEllipsesFound = b;
    }

    public BufferedImage
    runKernel( BufferedImage img )
    {
        final int STRG_EDGE =  1;
        final int WEAK_EDGE = -1;
        final int NO_EDGE   =  0;

        int height = img.getHeight();
        int width = img.getWidth();
        BufferedImage result;
        int[][] blob = new int[ height ][ width ];


        result = this.kernel.apply( img );


        if ( this.kernel instanceof SobelKernel )
        {
            if ( this.thresholding )
            {
                /*  THRESHOLDING
                 *  Gives a better edge detection.
                 *
                 * * * * */

                for ( int y = 0; y < result.getHeight(); ++y )
                {
                    for ( int x = 0; x < result.getWidth(); ++x )
                    {
                        int r = new Color( result.getRGB( x, y ) ).getRed();
                        int g = new Color( result.getRGB( x, y ) ).getGreen();
                        int b = new Color( result.getRGB( x, y ) ).getBlue();
                        float[] hsv = new float[ 3 ];

                        Color.RGBtoHSB( r, g, b, hsv);

                        if ( hsv[ 2 ] >= sobelHiThresh )
                        {
                            // NOTE: Strong edge pixel.
                            result.setRGB( x, y, 0xffffffff );
                            blob[ y ][ x ] = STRG_EDGE;
                        }
                        else if ( hsv[ 2 ] >= sobelLoThresh )
                        {
                            // NOTE: Weak edge pixel.
                            result.setRGB( x, y, 0xff000000 );
                            blob[ y ][ x ] = WEAK_EDGE;
                        }
                        else
                        {
                            // NOTE: Just noise.
                            result.setRGB( x, y, 0xff000000 );
                            blob[ y ][ x ] = NO_EDGE;
                        }
                    }
                }
            }

            if ( this.blobAnalysis )
            {
                /*  BLOB ANALYSIS
                 *  Keep "weak" points if connected to "strong" ones.
                 *
                 * * * * */

                for ( int y = 1; y < result.getHeight()-1; ++y )
                {
                    for ( int x = 1; x < result.getWidth()-1; ++x )
                    {
                        if ( blob[ y ][ x ] == WEAK_EDGE )
                        {
                            if ( ( blob[ y-1 ][ x-1 ] == STRG_EDGE ) ||
                                 ( blob[ y-1 ][ x   ] == STRG_EDGE ) ||
                                 ( blob[ y-1 ][ x+1 ] == STRG_EDGE ) ||
                                 ( blob[ y   ][ x-1 ] == STRG_EDGE ) ||
                                 ( blob[ y   ][ x+1 ] == STRG_EDGE ) ||
                                 ( blob[ y+1 ][ x-1 ] == STRG_EDGE ) ||
                                 ( blob[ y+1 ][ x   ] == STRG_EDGE ) ||
                                 ( blob[ y+1 ][ x+1 ] == STRG_EDGE ) )
                            {
                                // NOTE: The weak point is connected to a strong point.
                                result.setRGB( x, y, 0xffffffff );
                            }
                        }
                    }
                }
            }

            if ( this.thinning )
            {
                /*  THINNING
                 *  From paper given in class.
                 *
                 * * * * */

                boolean[][] b = new boolean[ height ][ width ];
                boolean[] v = new boolean[ 8 ];

                // NOTE: First pass
                for ( int y = 1; y < height-1; ++y )
                {
                    for ( int x = 1; x < width-1; ++x )
                    {
                        if ( result.getRGB( x, y ) != 0xff000000 )
                        {
                            v[ 0 ] = ( result.getRGB(   x, y+1 ) == 0xff000000 );
                            v[ 1 ] = ( result.getRGB( x+1, y+1 ) == 0xff000000 );
                            v[ 2 ] = ( result.getRGB( x+1,   y ) == 0xff000000 );
                            v[ 3 ] = ( result.getRGB( x+1, y-1 ) == 0xff000000 );
                            v[ 4 ] = ( result.getRGB(   x, y-1 ) == 0xff000000 );
                            v[ 5 ] = ( result.getRGB( x-1, y-1 ) == 0xff000000 );
                            v[ 6 ] = ( result.getRGB( x-1,   y ) == 0xff000000 );
                            v[ 7 ] = ( result.getRGB( x-1, y+1 ) == 0xff000000 );

                            int transitions = 0;
                            int neighbourCount = 0;
                            for ( int i = 0; i < 8; ++i )
                            {
                                if ( v[ i ] )
                                    neighbourCount++;

                                if ( ( !v[ i ] ) && ( v[ ( i + 1 ) % 8 ] ) )
                                    transitions++;
                            }

                            if ( ( transitions == 1 ) &&
                                 ( neighbourCount >= 2 ) &&
                                 ( neighbourCount <= 6 ) &&
                                 ( v[ 0 ] || v[ 2 ] || v[ 4 ] ) &&
                                 ( v[ 2 ] || v[ 4 ] || v[ 6 ] ) )
                            {
                                b[ y ][ x ] = true;
                            }
                        }
                    }
                }

                for ( int y = 0; y < height; ++y )
                {
                    for ( int x = 0; x < width; ++x )
                    {
                        if ( b[ y ][ x ] )
                        {
                            result.setRGB( x, y, 0xff000000 );
                            b[ y ][ x ] = false;
                        }
                    }
                }

                // NOTE: Second pass
                for ( int y = 1; y < height-1; ++y )
                {
                    for ( int x = 1; x < width-1; ++x )
                    {
                        if ( result.getRGB( x, y ) != 0xff000000 )
                        {
                            v[ 0 ] = ( result.getRGB(   x, y+1 ) == 0xff000000 );
                            v[ 1 ] = ( result.getRGB( x+1, y+1 ) == 0xff000000 );
                            v[ 2 ] = ( result.getRGB( x+1,   y ) == 0xff000000 );
                            v[ 3 ] = ( result.getRGB( x+1, y-1 ) == 0xff000000 );
                            v[ 4 ] = ( result.getRGB(   x, y-1 ) == 0xff000000 );
                            v[ 5 ] = ( result.getRGB( x-1, y-1 ) == 0xff000000 );
                            v[ 6 ] = ( result.getRGB( x-1,   y ) == 0xff000000 );
                            v[ 7 ] = ( result.getRGB( x-1, y+1 ) == 0xff000000 );

                            int transitions = 0;
                            int neighbourCount = 0;
                            for ( int i = 0; i < 8; ++i )
                            {
                                if ( v[ i ] )
                                    neighbourCount++;

                                if ( ( !v[ i ] ) && ( v[ ( i + 1 ) % 8 ] ) )
                                    transitions++;
                            }

                            if ( ( transitions == 1 ) &&
                                 ( neighbourCount >= 2 ) &&
                                 ( neighbourCount <= 6 ) &&
                                 ( v[ 0 ] || v[ 2 ] || v[ 6 ] ) &&
                                 ( v[ 0 ] || v[ 4 ] || v[ 6 ] ) )
                            {
                                b[ y ][ x ] = true;
                            }
                        }
                    }
                }

                for ( int y = 0; y < height; ++y )
                {
                    for ( int x = 0; x < width; ++x )
                    {
                        if ( b[ y ][ x ] )
                        {
                            result.setRGB( x, y, 0xff000000 );
                        }
                    }
                }
            }
        }

        return result;
    }
    public BufferedImage
    runKernel()
    {
        BufferedImage result = runKernel( this.inputImage );

        return result;
    }


    public BufferedImage
    runHoughLines( BufferedImage inputImage )
    {
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        int maxDistance = ( int )Math.sqrt( width*width + height*height );

        int accumulatorWidth = maxDistance;
        int accumulatorHeight = 360;
        // NOTE: 2D array should have every element initialised to 0.
        int[][] accumulator = new int[ accumulatorHeight ][ accumulatorWidth ];
        BufferedImage houghSpace = new BufferedImage( accumulatorWidth,
                                                      accumulatorHeight,
                                                      BufferedImage.TYPE_INT_ARGB );
        int maxValue = 0;

        for ( int y = 0; y < height; ++y )
        {
            for ( int x = 0; x < width; ++x )
            {
                if ( inputImage.getRGB( x, y ) != 0xff000000 )
                {
                    for ( int theta = 0; theta < 360; ++theta )
                    {
                        int rho = ( int )(
                            x*Math.cos( ( theta * Math.PI ) / 180.0 ) +
                            y*Math.sin( ( theta * Math.PI ) / 180.0 )
                        );

                        if ( ( rho > 0 ) && ( rho < maxDistance ) )
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

        for ( int row = 0; row < accumulatorHeight; ++row )
        {
            for ( int col = 0; col < accumulatorWidth; ++col )
            {
                int v = 255 * accumulator[ row ][ col ] / maxValue;
                houghSpace.setRGB( col, row, new Color( v, v, v ).getRGB() );
            }
        }

        return houghSpace;
    }

    private ArrayList< HoughPoint >
    findMaxima( BufferedImage houghed )
    {
        ArrayList< HoughPoint > maxima = new ArrayList<>();
        maxima.add( new HoughPoint( 0, 0, houghed.getRGB( 0, 0 ) ) );

        for ( int theta = 0; theta < houghed.getHeight(); ++theta )
        {
            for ( int rho = 0; rho < houghed.getWidth(); ++rho )
            {
                if ( maxima.size() > 1 )
                {
                    maxima.sort( Collections.reverseOrder() );
                }

                int value = houghed.getRGB( rho, theta );

                int i = 0;
                while ( ( i < maxima.size() ) && ( i < this.nbOfLines ) )
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

            double cosT = Math.cos( c.theta );
            double sinT = Math.sin( c.theta );
            double x0 = cosT * rho;
            double y0 = sinT * rho;

            int x1 = ( int ) ( x0 + 2*width * ( -sinT ) );
            int y1 = ( int ) ( y0 + 2*width * (  cosT ) );
            int x2 = ( int ) ( x0 - 2*width * ( -sinT ) );
            int y2 = ( int ) ( y0 - 2*width * (  cosT ) );

            houghLines.drawLine( x1, y1, x2, y2 );
            houghPoints.drawLine( rho-5,   ( int )( theta ), rho+5,   ( int )( theta ) );
            houghPoints.drawLine(   rho, ( int )( theta )-5,   rho, ( int )( theta )+5 );
        }

        houghLines.dispose();
        houghPoints.dispose();

        return new BufferedImage[] { lined, houghImage };
    }

    public double[]
    computeCoverage( int centerX, int centerY, double minRadiusX, double minRadiusY )
    {
        BufferedImage sobeled = Context.getInstance().getSobelImage();
        int bestCoverage = 0;
        double bestA = 0;
        double bestB = 0;
        double bestAlpha = 0.0;
        int maxRadiusX = sobeled.getWidth();
        int maxRadiusY = sobeled.getHeight();

        // NOTE: 'a' is one radius, b is the other.
        for ( int b = ( int )minRadiusY; b < maxRadiusY; b+=2 )
        {
            for ( int a = ( int )minRadiusX; a < maxRadiusX; a+=2 )
            {
                int previousX = 0;
                int previousY = 0;

                for ( double alpha = 0; alpha < ( 0.5*Math.PI ); alpha+=0.2 )
                {
                    int coverage = 0;

                    double cosA = Math.cos( alpha );
                    double sinA = Math.sin( alpha );

                    for ( double t = 0.0; t < 2*Math.PI; t+=0.02 )
                    {
                        double cosT = Math.cos( t );
                        double sinT = Math.sin( t );
                        double tmpX = centerX + a*cosT;
                        double tmpY = centerY + b*sinT;

                        // NOTE: Apply rotation.
                        int x = ( int )( tmpX*cosA + tmpY*sinA );
                        int y = ( int )( tmpY*cosA - tmpX*sinA );

                        if ( ( x < sobeled.getWidth() ) && ( y < sobeled.getHeight() ) &&
                             ( x > 0 ) && ( y > 0 ) &&
                             ( sobeled.getRGB( x, y ) == 0xffffffff ) &&
                             ( ( x != previousX ) || ( y != previousY ) ) )
                        {
                            coverage++;
                            previousX = x;
                            previousY = y;
                        }
                    }

                    if ( coverage > bestCoverage )
                    {
                        bestCoverage = coverage;
                        bestA = a;
                        bestB = b;
                        bestAlpha = alpha;
                    }
                }
            }
        }

        return new double[] { bestA, bestB, bestAlpha };
    }

    public BufferedImage[]
    drawHoughEllipses( BufferedImage houghed, int width, int height )
    {
        BufferedImage ellipsed = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );

        ArrayList< HoughPoint > maxima = findMaxima( houghed );

        Graphics2D houghEllipses = ellipsed.createGraphics();
        houghEllipses.drawImage( this.inputImage, 0, 0, null );
        houghEllipses.setColor( Color.red );

        int[][] pairsArray = new int[ maxima.size()*maxima.size() ][ 2 ];
        int[][] closestPair = new int[ 2 ][ 2 ];

        // TODO: I guess there is the possibility of getting two parallel lines.
        //       I should probably deal with that...
        for ( int j = 0; j < maxima.size(); ++j )
        {
            for ( int i = 0; i < maxima.size(); ++i )
            {
                if ( i != j )
                {
                    // NOTE: Standard equation of line: y = mx + b
                    //       If lines intersect, then m1*x + b1 = m2*x + b2
                    HoughPoint p1 = maxima.get( j );
                    HoughPoint p2 = maxima.get( i );

                    double m1 = -( Math.cos( p1.theta ) / Math.sin( p1.theta ) );
                    double b1 = p1.rho / Math.sin( p1.theta );

                    double m2 = -( Math.cos( p2.theta ) / Math.sin( p2.theta ) );
                    double b2 = p2.rho / Math.sin( p2.theta );

                    pairsArray[ j*maxima.size() + i ][ 0 ] = ( int )( ( b2 - b1 ) / ( m1 - m2 ) );
                    pairsArray[ j*maxima.size() + i ][ 1 ] = ( int )( ( m1*b2 - m2*b1 ) / ( m1 - m2 ) );
                }
            }
        }


        /*  BRUTE-FORCE closest pair search.
         *  Can do better.
         *
         * * * * */
        double minDist = Double.MAX_VALUE;
        for ( int j = 0; j < maxima.size(); ++j )
        {
            for ( int i = 0; i < maxima.size(); ++i )
            {
                if ( i != j )
                {
                    int x1x2 = pairsArray[ j ][ 0 ] - pairsArray[ i ][ 0 ];
                    int y1y2 = pairsArray[ j ][ 1 ] - pairsArray[ i ][ 1 ];
                    double dist = Math.sqrt( x1x2*x1x2 + y1y2*y1y2 );
                    if ( ( dist > 2 ) && ( dist < minDist ) )
                    {
                        minDist = dist;
                        closestPair[ 0 ][ 0 ] = pairsArray[ i ][ 0 ];
                        closestPair[ 0 ][ 1 ] = pairsArray[ i ][ 1 ];
                        closestPair[ 1 ][ 0 ] = pairsArray[ j ][ 0 ];
                        closestPair[ 1 ][ 1 ] = pairsArray[ j ][ 1 ];
                    }
                }
            }
        }

        int centerX = ( closestPair[ 0 ][ 0 ] + closestPair[ 1 ][ 0 ] ) / 2;
        int centerY = ( closestPair[ 0 ][ 1 ] + closestPair[ 1 ][ 1 ] ) / 2;

        int minRadiusX = 1;
        int minRadiusY = 1;
        double[] radii = new double[] { minRadiusX, minRadiusY, 0 };

        for ( int i = 0; i < this.ellipsesCount; ++i )
        {
            radii = computeCoverage( centerX, centerY, radii[ 0 ]+1.0, radii[ 1 ]+1.0 );

            if ( this.showEllipsesFound )
            {
                houghEllipses.setColor( Color.cyan );
                houghEllipses.setStroke( new BasicStroke( 2 ) );

                for ( double t = 0.0; t < 360.0; t+=0.5 )
                {
                    double cosA = Math.cos( radii[ 2 ] );
                    double sinA = Math.sin( radii[ 2 ] );
                    double cosT = Math.cos( t * 180 / Math.PI );
                    double sinT = Math.sin( t * 180 / Math.PI );
                    double a = radii[ 0 ];
                    double b = radii[ 1 ];
                    int x = ( int )( centerX + ( a*cosT*cosA - b*sinT*sinA ) );
                    int y = ( int )( centerY + ( b*cosA*sinT + a*cosT*sinA ) );
                    houghEllipses.drawLine( x, y, x, y );
                }
            }
        }

        if ( this.showEllipseCenter )
        {
            houghEllipses.setColor( Color.cyan );
            houghEllipses.setStroke( new BasicStroke( 2 ) );

            houghEllipses.drawLine( centerX-5,   centerY, centerX+5,   centerY );
            houghEllipses.drawLine(   centerX, centerY-5,   centerX, centerY+5 );
        }

        double step = 0.1;
        GeneralPath ellipse = new GeneralPath( GeneralPath.WIND_EVEN_ODD, ( int )( 360.0/step ) );

        double cosA = Math.cos( radii[ 2 ] );
        double sinA = Math.sin( radii[ 2 ] );
        double cosT = Math.cos( 0.0 );
        double sinT = Math.sin( 0.0 );
        double a = radii[ 0 ];
        double b = radii[ 1 ];
        int x = ( int )( centerX + ( a*cosT*cosA - b*sinT*sinA ) );
        int y = ( int )( centerY + ( b*cosA*sinT + a*cosT*sinA ) );

        ellipse.moveTo( x, y );

        for ( double t = step; t < 2.0*Math.PI; t+=step )
        {
            cosA = Math.cos( radii[ 2 ] );
            sinA = Math.sin( radii[ 2 ] );
            cosT = Math.cos( t );
            sinT = Math.sin( t );
            a = radii[ 0 ];
            b = radii[ 1 ];
            x = ( int )( centerX + ( a*cosT*cosA - b*sinT*sinA ) );
            y = ( int )( centerY + ( b*cosA*sinT + a*cosT*sinA ) );

            ellipse.lineTo( x, y );
        }

        ellipse.closePath();

        for ( int row = 0; row < height; ++row )
        {
            for ( int col = 0; col < width; ++col )
            {
                if ( !ellipse.contains( col, row ) )
                {
                    houghEllipses.setColor( Color.black );
                    houghEllipses.drawLine( col, row, col, row );
                }
            }
        }

        houghEllipses.dispose();

        return new BufferedImage[] { ellipsed, null };
    }

}
