/*
 *  File name:
 *      Matrix.java
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

package imageprocessingops.math;


public class
Matrix
{

    public int rows;
    public int cols;
    public float[][] data;


    public
    Matrix( int rows, int cols )
    {
        this.rows = rows;
        this.cols = cols;

        this.data = new float[ rows ][ cols ];
    }


    public Matrix
    mul( Matrix mat)
    {
        if ( this.cols != mat.rows )
            return null;

        Matrix result = new Matrix( this.rows, mat.cols );
        // TODO: Should initialize all elements to zero. Might want to check that...
        result.data = new float[ result.rows ][ result.cols ];

        for ( int row = 0; row < this.rows; ++row )
        {
            for ( int col = 0; col < mat.cols; ++col )
            {
                for ( int k = 0; k < this.cols; ++k )
                {
                    result.data[ row ][ col ] += this.data[ row ][ k ] * mat.data[ k ][ col ];
                }
            }
        }

        return result;
    }

}
