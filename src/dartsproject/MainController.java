/*
 *  File name:
 *      MainController.java
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

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;


public class
MainController implements Initializable
{

    @FXML private ImageView imageView;
    @FXML private ImageView blurView;
    @FXML private ImageView sobelView;
    @FXML private ImageView accumulatorView;
    @FXML private ImageView resultView;

    @FXML private MenuBar menu;
    @FXML private MenuController menuController;

    @FXML private VBox ops;
    @FXML private OpsController opsController;

    @FXML private Label displayedImagePath;


    @Override
    public void
    initialize( URL location, ResourceBundle resources )
    {
        String defaultImagePath = "img/dartboard-23938_960_720.png";
        File file = new File( defaultImagePath );

        try
        {
            Image img = new Image( file.toURI().toURL().toString() );
            this.imageView.setImage( img );
            this.displayedImagePath.setText( defaultImagePath );
        }
        catch (MalformedURLException e )
        {
            e.printStackTrace();
        }

        Context.getInstance().setCurrentImage( file );


        this.menuController.init( this );
        this.opsController.init( this );
    }


    public void
    updateMainImageView()
    {
        this.imageView.setImage( Context.getInstance().getCurrentImage() );
        displayedImagePath.setText( Context.getInstance().getCurrentImagePath() );
    }

    public void
    updateBlurView( Image img )
    {
        this.blurView.setImage( img );
    }

    public void
    updateSobelView( Image img )
    {
        this.sobelView.setImage( img );
    }

    public void
    updateAccumulatorView( Image img )
    {
        this.accumulatorView.setImage( img );
    }

    public void updateResultView( Image img )
    {
        this.resultView.setImage( img );
    }

}
