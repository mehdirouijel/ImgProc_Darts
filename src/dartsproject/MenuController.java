/*
 *  File name:
 *      MenuController.java
 *
 *  ====================
 *  Description:
 *      Controller for the menu bar... Not much else to say here.
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

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class
MenuController implements Initializable
{

    private MainController mainCtrl;

    @FXML private MenuBar menuBar;


    @Override
    public void
    initialize( URL loc, ResourceBundle res )
    {
        menuBar.setFocusTraversable( true );
    }

    public void
    init( MainController mainController )
    {
        this.mainCtrl = mainController;
    }


    public void
    chooseFile()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle( "Open an image" );
        fileChooser.setInitialDirectory( new java.io.File( "." ) );
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter( "Image Files",
                                             "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp" ),
            new FileChooser.ExtensionFilter( "All Files", "*.*" )
        );

        File selectedFile = fileChooser.showOpenDialog( Context.getInstance().getPrimaryStage() );
        if ( selectedFile != null )
        {
            Context.getInstance().setCurrentImage( selectedFile );
            mainCtrl.updateMainImageView();
        }
    }

    public void
    saveImage()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle( "Save current tab" );
        fileChooser.setInitialDirectory( new java.io.File( "." ) );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter( "PNG Images", "*.png", "*.PNG" ),
                new FileChooser.ExtensionFilter( "JPG Images", "*.jpg", "*.JPG" ),
                new FileChooser.ExtensionFilter( "JPEG Images", "*.jpeg", "*.JPEG" ),
                new FileChooser.ExtensionFilter( "GIF Images", "*.gif"),
                new FileChooser.ExtensionFilter( "BMP Images", "*.bmp" ),
                new FileChooser.ExtensionFilter( "All Files", "*.*" )
        );

        File file = fileChooser.showSaveDialog( Context.getInstance().getPrimaryStage() );
        if ( file != null )
        {
            String ext = "png";
            String extension = fileChooser.getSelectedExtensionFilter().getExtensions().get( 0 );
            System.out.println( extension );
            if ( extension.equals( "*.jpg" ) )
            {
                ext = "jpg";
            }
            if ( extension.equals( "*.png" ) )
            {
                ext = "png";
            }
            if ( extension.equals( "*.gif" ) )
            {
                ext = "gif";
            }
            if ( extension.equals( "*.jpeg" ) )
            {
                ext = "jpeg";
            }
            if ( extension.equals( "*.bmp" ) )
            {
                ext = "bmp";
            }

            try
            {
                ImageIO.write( SwingFXUtils.fromFXImage( this.mainCtrl.getSelectedImage(), null ),
                               ext,
                               file );
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }

    public void
    killApp()
    {
        System.exit( 0 );
    }

}
