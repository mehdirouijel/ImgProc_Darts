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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;


public class
Controller implements Initializable
{

    @FXML
    private ImageView imageView;


    @Override
    public void
    initialize( URL location, ResourceBundle resources )
    {
        File file = new File( "img/dartboard-23938_960_720.png" );
        Image image = new Image( file.toURI().toString() );

        imageView.setImage( image );
    }

}
