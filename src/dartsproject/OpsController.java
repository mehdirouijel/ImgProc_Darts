/*
 *  File name:
 *      OpsController.java
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
    @FXML private CheckBox useBlurSobel;
    @FXML private CheckBox useBlurHough;


    @Override
    public void
    initialize( URL loc, ResourceBundle res )
    {
        houghLinesNb.valueProperty().addListener(
            ( observable, oldValue, newValue ) -> {
                houghLinesLabel.textProperty().setValue(
                        "Hough lines : " + String.valueOf( newValue.intValue() ) );
                proc.setNbOfLines( newValue.intValue() );
            }
        );
    }

    public void
    init( MainController mainController )
    {
        this.mainCtrl = mainController;
    }


    public void
    runBlur()
    {
        BufferedImage loaded;
        BufferedImage blurred;

        try
        {
            loaded = ImageIO.read( Context.getInstance().getCurrentFile() );

            proc.setInputImage( loaded );
            proc.setKernel( new BlurKernel() );
            blurred = proc.runKernel();
            this.mainCtrl.updateBlurView( SwingFXUtils.toFXImage( blurred, null ) );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    public void
    doThinning(  )
    {

    }

    public void
    runSobel()
    {
        BufferedImage loaded;
        BufferedImage result;
        BufferedImage blurred;

        try
        {
            loaded = ImageIO.read( Context.getInstance().getCurrentFile() );

            proc.setInputImage( loaded );

            if ( useBlurSobel.isSelected() )
            {
                proc.setKernel( new BlurKernel() );
                blurred = proc.runKernel();
                this.mainCtrl.updateBlurView( SwingFXUtils.toFXImage( blurred, null ) );
                proc.setInputImage( blurred );
            }

            proc.setKernel( new SobelKernel() );
            result = proc.runKernel();
            this.mainCtrl.updateSobelView( SwingFXUtils.toFXImage( result, null ) );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    public void
    runHough()
    {
        BufferedImage loaded;
        BufferedImage sobeled;
        BufferedImage houghed;
        BufferedImage blurred;

        try
        {
            loaded = ImageIO.read( Context.getInstance().getCurrentFile() );

            proc.setInputImage( loaded );

            if ( useBlurHough.isSelected() )
            {
                proc.setKernel( new BlurKernel() );
                blurred = proc.runKernel();
                this.mainCtrl.updateBlurView( SwingFXUtils.toFXImage( blurred, null ) );
                proc.setInputImage( blurred );
            }

            proc.setKernel( new SobelKernel() );

            sobeled = proc.runKernel();
            this.mainCtrl.updateSobelView( SwingFXUtils.toFXImage( sobeled, null ) );

            houghed = proc.runHough( sobeled );
            Context.getInstance().setHoughAccu( houghed );
            this.mainCtrl.updateAccumulatorView( SwingFXUtils.toFXImage( houghed, null ) );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    public void
    drawHoughLines() throws IOException
    {
        BufferedImage loaded = ImageIO.read( Context.getInstance().getCurrentFile() );
        BufferedImage houghed = Context.getInstance().getHoughAccu();
        BufferedImage lined[];

        proc.setInputImage( loaded );
        lined = proc.drawHoughLines( houghed, loaded.getWidth(), loaded.getHeight() );
        this.mainCtrl.updateAccumulatorView( SwingFXUtils.toFXImage( lined[ 1 ], null ) );
        this.mainCtrl.updateResultView( SwingFXUtils.toFXImage( lined[ 0 ], null ) );
    }

}
