package imageprocessingops;

import java.awt.*;
import java.awt.image.BufferedImage;

import imageprocessingops.convolution.ConvoKernel;
import imageprocessingops.math.Matrix;
import javafx.scene.image.*;


public class
ImageProcessor
{

    private ConvoKernel kernel = null;
    private BufferedImage inputImage = null;

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

}
