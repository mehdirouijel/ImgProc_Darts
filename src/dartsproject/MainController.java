/*
 *  File name:
 *      Controller.java
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
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable
{

    @FXML private ImageView imageView;

    @FXML private MenuBar menu;
    @FXML private MenuController menuController;


    @Override
    public void
    initialize( URL location, ResourceBundle resources )
    {
        File file = new File( "img/dartboard-23938_960_720.png" );

        try
        {
            Image img = new Image( file.toURI().toURL().toString() );
            this.imageView.setImage( img );
        }
        catch (MalformedURLException e )
        {
            e.printStackTrace();
        }

        Context.getInstance().setCurrentImage( file );


        this.menuController.init( this );
    }


    public void
    setDisplayedImage( Image img )
    {
        this.imageView.setImage( img );
    }

}
