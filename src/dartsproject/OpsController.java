/*
 *  File name:
 *      OpsController.java
 *
 *  ====================
 *  Description:
 *      This controller is in charge of the operations available through the panel
  *     on the right-hand side of the application window. It communicates the different
  *     options to the implementation of the algorithms.
 *
 *  ====================
 *  Sources:
 *      http://code.makery.ch/blog/javafx-2-event-handlers-and-change-listeners/
 *
 *  ====================
 *  Author:
 *      Mehdi Rouijel
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package dartsproject;

import imageprocessingops.ImageProcessor;
import imageprocessingops.convolution.BlurKernel;
import imageprocessingops.convolution.SobelKernel;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class
OpsController implements Initializable
{

    private MainController mainCtrl;
    private ImageProcessor proc = new ImageProcessor();

    @FXML private Slider houghLinesNb;
    @FXML private Label houghLinesLabel;
    @FXML private CheckBox useBlur;
    @FXML private CheckBox useThresholding;
    @FXML private CheckBox useBlobAnalysis;
    @FXML private CheckBox useThinning;
    @FXML private Slider lowThresh;
    @FXML private Label lowThreshLabel;
    @FXML private Slider highThresh;
    @FXML private Label highThreshLabel;
    @FXML private Slider nbOfEllipses;
    @FXML private Label nbOfEllipsesLabel;
    @FXML private CheckBox showCenterOfEllipse;
    @FXML private CheckBox showFoundEllipses;


    @Override
    public void
    initialize( URL loc, ResourceBundle res )
    {
        houghLinesNb.valueProperty().addListener(
            ( observable, oldValue, newValue ) -> {
                houghLinesLabel.textProperty().setValue(
                        "Hough lines: " + String.valueOf( newValue.intValue() ) );
                proc.setNbOfLines( newValue.intValue() );
            }
        );

        lowThresh.valueProperty().addListener(
            ( observable, oldValue, newValue ) -> {
                lowThreshLabel.textProperty().setValue(
                        "Low Thresh.: " + String.valueOf( newValue.floatValue() ) );
                proc.setSobelLoThresh( newValue.floatValue() );
            }
        );

        highThresh.valueProperty().addListener(
            ( observable, oldValue, newValue ) -> {
                highThreshLabel.textProperty().setValue(
                        "High Thresh.: " + String.valueOf( newValue.floatValue() ) );
                proc.setSobelHiThresh( newValue.floatValue() );
            }
        );

        useThresholding.selectedProperty().addListener(
            ( observable, oldValue, newValue ) -> proc.setThresholding( newValue )
        );

        useBlobAnalysis.selectedProperty().addListener(
                ( observable, oldValue, newValue ) -> proc.setBlobAnalysis( newValue )
        );

        useThinning.selectedProperty().addListener(
                ( observable, oldValue, newValue ) -> proc.setThinning( newValue )
        );

        nbOfEllipses.valueProperty().addListener(
                ( observable, oldValue, newValue ) -> {
                    nbOfEllipsesLabel.textProperty().setValue(
                            "Ellipses Count: " + String.valueOf( newValue.intValue() ) );
                    proc.setEllipsesCount( newValue.intValue() );
                }
        );

        showCenterOfEllipse.selectedProperty().addListener(
                ( observable, oldValue, newValue ) -> proc.setShowEllipseCenter( newValue )
        );

        showFoundEllipses.selectedProperty().addListener(
                ( observable, oldValue, newValue ) -> proc.setShowEllipsesFound( newValue )
        );
    }

    public void
    init( MainController mainController )
    {
        this.mainCtrl = mainController;
        useBlur.setSelected( true );
        useThresholding.setSelected( true );
        useBlobAnalysis.setSelected( false );
        useThinning.setSelected( true );

        this.proc.setThresholding( true );
        this.proc.setSobelLoThresh( ( float )lowThresh.getValue() );
        this.proc.setSobelHiThresh( ( float )highThresh.getValue() );
        this.proc.setBlobAnalysis( true );
        this.proc.setThinning( true );
        this.proc.setEllipsesCount( ( int )nbOfEllipses.getValue() );
        this.proc.setShowEllipseCenter( true );
        this.proc.setShowEllipsesFound( true );
    }


    public void
    runBlur()
    {
        long startTime;
        long endTime;
        BufferedImage loaded;
        BufferedImage blurred;

        try
        {
            loaded = ImageIO.read( Context.getInstance().getCurrentFile() );

            proc.setInputImage( loaded );
            proc.setKernel( new BlurKernel() );

            startTime = System.nanoTime();

            blurred = proc.runKernel();
            mainCtrl.updateBlurView( SwingFXUtils.toFXImage( blurred, null ) );

            endTime = System.nanoTime();
            double time = 0.000001 * ( endTime - startTime );
            mainCtrl.setExecTime( "Exec. Time: "+String.format( "%.3f",time )+"ms" );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    public void
    runSobel()
    {
        long startTime;
        long endTime;
        BufferedImage loaded;
        BufferedImage result;
        BufferedImage blurred;

        try
        {
            loaded = ImageIO.read( Context.getInstance().getCurrentFile() );

            proc.setInputImage( loaded );

            startTime = System.nanoTime();

            if ( useBlur.isSelected() )
            {
                proc.setKernel( new BlurKernel() );
                blurred = proc.runKernel();

                mainCtrl.updateBlurView( SwingFXUtils.toFXImage( blurred, null ) );
                proc.setInputImage( blurred );
            }

            proc.setKernel( new SobelKernel() );
            result = proc.runKernel();

            mainCtrl.updateSobelView( SwingFXUtils.toFXImage( result, null ) );

            endTime = System.nanoTime();
            double time = 0.000001 * ( endTime - startTime );
            mainCtrl.setExecTime( "Exec. Time: "+String.format( "%.3f",time )+"ms" );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    public void
    runHoughLines()
    {
        long startTime;
        long endTime;
        BufferedImage loaded;
        BufferedImage sobeled;
        BufferedImage houghed;
        BufferedImage blurred;

        try
        {
            loaded = ImageIO.read( Context.getInstance().getCurrentFile() );

            proc.setInputImage( loaded );

            startTime = System.nanoTime();

            if ( useBlur.isSelected() )
            {
                proc.setKernel( new BlurKernel() );
                blurred = proc.runKernel();
                mainCtrl.updateBlurView( SwingFXUtils.toFXImage( blurred, null ) );
                proc.setInputImage( blurred );
            }

            proc.setKernel( new SobelKernel() );

            sobeled = proc.runKernel();
            mainCtrl.updateSobelView( SwingFXUtils.toFXImage( sobeled, null ) );

            houghed = proc.runHoughLines( sobeled );

            Context.getInstance().setHoughAccu( houghed );
            mainCtrl.updateAccumulatorView( SwingFXUtils.toFXImage( houghed, null ) );

            drawHoughLines();

            endTime = System.nanoTime();
            double time = 0.000001 * ( endTime - startTime );
            mainCtrl.setExecTime( "Exec. Time: "+String.format( "%.3f",time )+"ms" );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    public void
    runEllipsesDetection()
    {
        long startTime;
        long endTime;

        BufferedImage loaded;
        BufferedImage sobeled;
        BufferedImage houghed;
        BufferedImage blurred;

        try
        {
            loaded = ImageIO.read( Context.getInstance().getCurrentFile() );

            proc.setInputImage( loaded );

            startTime = System.nanoTime();
            if ( useBlur.isSelected() )
            {
                proc.setKernel( new BlurKernel() );
                blurred = proc.runKernel();
                mainCtrl.updateBlurView( SwingFXUtils.toFXImage( blurred, null ) );
                proc.setInputImage( blurred );
            }

            proc.setKernel( new SobelKernel() );

            sobeled = proc.runKernel();
            Context.getInstance().setSobelImage( sobeled );
            mainCtrl.updateSobelView( SwingFXUtils.toFXImage( Context.getInstance().getSobelImage(), null ) );

            houghed = proc.runHoughLines( sobeled );
            Context.getInstance().setHoughAccu( houghed );

            drawHoughLines();
            drawHoughEllipses();

            endTime = System.nanoTime();
            double time = 0.000001 * ( endTime - startTime );
            mainCtrl.setExecTime( "Exec. Time: "+String.format( "%.3f",time )+"ms" );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    public void
    drawHoughLines()
    {
        try
        {
            BufferedImage loaded = ImageIO.read( Context.getInstance().getCurrentFile() );
            BufferedImage houghed = Context.getInstance().getHoughAccu();
            BufferedImage lined[];

            proc.setInputImage( loaded );
            lined = proc.drawHoughLines( houghed, loaded.getWidth(), loaded.getHeight() );
            mainCtrl.updateAccumulatorView( SwingFXUtils.toFXImage( lined[ 1 ], null ) );
            mainCtrl.updateLinesResultView( SwingFXUtils.toFXImage( lined[ 0 ], null ) );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    public void
    drawHoughEllipses()
    {
        try
        {
            BufferedImage loaded = ImageIO.read( Context.getInstance().getCurrentFile() );
            BufferedImage houghed = Context.getInstance().getHoughAccu();
            BufferedImage ellipsed[];

            proc.setInputImage( loaded );
            ellipsed = proc.drawHoughEllipses( houghed, loaded.getWidth(), loaded.getHeight() );
            if ( ellipsed[ 1 ] != null )
            {
                mainCtrl.updateAccumulatorView( SwingFXUtils.toFXImage( ellipsed[ 1 ], null ) );
            }
            mainCtrl.updateEllipsesResultView( SwingFXUtils.toFXImage( ellipsed[ 0 ], null ) );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

}
