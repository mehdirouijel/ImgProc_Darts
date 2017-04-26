package dartsproject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;

import javax.swing.*;
import java.io.File;
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
            mainCtrl.setDisplayedImage( Context.getInstance().getCurrentImage() );
        }
    }

    public void
    killApp()
    {
        System.exit( 0 );
    }

}
