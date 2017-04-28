/*
 *  File name:
 *      HoughPoint.java
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
HoughPoint implements Comparable< HoughPoint >
{

    public int rho;
    public double theta;
    public int value;

    public
    HoughPoint( int theRho, double theTheta, int theValue )
    {
        this.rho = theRho;
        this.theta = theTheta;
        this.value = theValue;
    }


    @Override
    public int
    compareTo( HoughPoint o )
    {
        if ( this.value < o.value )
            return -1;

        if ( this.value > o.value )
            return 1;

        return 0;
    }

}
