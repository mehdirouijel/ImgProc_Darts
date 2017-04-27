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
        this.computedX = 0.0f;
        this.computedY = 0.0f;

        for ( int row = 0; row < input.rows; ++row )
        {
            for ( int col = 0; col < input.cols; ++col )
            {
                this.computedX +=
                    this.kernelX.data[ row ][ col ] * input.data[ ( input.rows-1 )-row ][ ( input.cols-1 )-col ];
                this.computedY +=
                    this.kernelY.data[ row ][ col ] * input.data[ ( input.rows-1 )-row ][ ( input.cols-1 )-col ];

                result = ( float )Math.sqrt( this.computedX*this.computedX +
                                             this.computedY*this.computedY );

                result /= this.divisor;
            }
        }

        return result;
    }

}
