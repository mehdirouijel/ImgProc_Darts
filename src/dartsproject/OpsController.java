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
import imageprocessingops.convolution.SobelKernel;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class
OpsController implements Initializable
{

    private MainController mainCtrl;

    @FXML private Button btnSobel;


    @Override
    public void
    initialize( URL loc, ResourceBundle res )
    {
    }

    public void
    init( MainController mainController )
    {
        this.mainCtrl = mainController;
    }


    public void
    runSobel()
    {
        ImageProcessor proc = new ImageProcessor();
        BufferedImage loaded = null;
        BufferedImage result;

        try
        {
            loaded = ImageIO.read( Context.getInstance().getCurrentFile() );

            proc.setInputImage( loaded );
            proc.setKernel( new SobelKernel() );
            result = proc.runKernel();

            //Context.getInstance().setCurrentPreview( SwingFXUtils.toFXImage( result, null ) );
            this.mainCtrl.updateSobelView( SwingFXUtils.toFXImage( result, null ) );

            //try
            //{
            //    ImageIO.write( result, "png", new File( "result.jpg" ) );
            //}
            //catch ( IOException e )
            //{
            //    e.printStackTrace();
            //}
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    public void
    runHough()
    {
        ImageProcessor proc = new ImageProcessor();
        BufferedImage loaded = null;
        BufferedImage sobeled;
        BufferedImage houghed;

        try
        {
            loaded = ImageIO.read( Context.getInstance().getCurrentFile() );

            proc.setInputImage( loaded );
            proc.setKernel( new SobelKernel() );

            sobeled = proc.runKernel();
            this.mainCtrl.updateSobelView( SwingFXUtils.toFXImage( sobeled, null ) );

            houghed = proc.runHough( sobeled );
            this.mainCtrl.updateAccumulatorView( SwingFXUtils.toFXImage( houghed, null ) );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

}
