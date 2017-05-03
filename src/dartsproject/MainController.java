/*
 *  File name:
 *      MainController.java
 *
 *  ====================
 *  Description:
 *      The application sort of loosely follows the MVC design pattern; it uses
 *      multiple controllers and this class is where they are all put together.
 *      This controller is also in charge of the different views to display images.
 *
 *  ====================
 *  Sources:
 *      http://stackoverflow.com/questions/22993550/how-to-resize-an-image-when-resizing-the-window-in-javafx
 *
 *  ====================
 *  Author:
 *      Mehdi Rouijel
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package dartsproject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
    @FXML private ImageView linesResultView;
    @FXML private ImageView ellipsesResultView;

    @FXML private MenuBar menu;
    @FXML private MenuController menuController;

    @FXML private TitledPane originalPane;
    @FXML private TabPane resultTabs;
    @FXML private Tab linesResultTab;
    @FXML private Tab ellipsesResultTab;
    @FXML private Tab blurTab;
    @FXML private Tab accuTab;
    @FXML private Tab sobelTab;

    @FXML private VBox ops;
    @FXML private OpsController opsController;

    @FXML private Label displayedImagePath;
    @FXML private Label execTime;


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


        if ( imageView.getImage().getWidth() >= imageView.getImage().getHeight() )
        {
            imageView.fitWidthProperty()
                     .bind( originalPane.widthProperty() );
            blurView.fitWidthProperty()
                    .bind( blurTab.getTabPane().widthProperty() );
            sobelView.fitWidthProperty()
                     .bind( sobelTab.getTabPane().widthProperty() );
            accumulatorView.fitWidthProperty()
                           .bind( accuTab.getTabPane().widthProperty() );
            linesResultView.fitWidthProperty()
                           .bind( linesResultTab.getTabPane().widthProperty() );
            ellipsesResultView.fitWidthProperty()
                              .bind( ellipsesResultTab.getTabPane().widthProperty() );
        }
        else
        {
            imageView.fitHeightProperty()
                     .bind( originalPane.heightProperty() );
            blurView.fitHeightProperty()
                    .bind( blurTab.getTabPane().heightProperty() );
            sobelView.fitHeightProperty()
                     .bind( sobelTab.getTabPane().heightProperty() );
            accumulatorView.fitHeightProperty()
                           .bind( accuTab.getTabPane().heightProperty() );
            linesResultView.fitHeightProperty()
                           .bind( linesResultTab.getTabPane().heightProperty() );
            ellipsesResultView.fitHeightProperty()
                              .bind( ellipsesResultTab.getTabPane().heightProperty() );
        }

        displayedImagePath.minWidthProperty().bind( originalPane.widthProperty() );


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
        resultTabs.getSelectionModel().select( blurTab );
    }
    public void
    updateSobelView( Image img )
    {
        this.sobelView.setImage( img );
        resultTabs.getSelectionModel().select( sobelTab );
    }
    public void
    updateAccumulatorView( Image img )
    {
        this.accumulatorView.setImage( img );
        resultTabs.getSelectionModel().select( accuTab );
    }
    public void updateLinesResultView( Image img )
    {
        this.linesResultView.setImage( img );
        resultTabs.getSelectionModel().select( linesResultTab );
    }
    public void updateEllipsesResultView( Image img )
    {
        this.ellipsesResultView.setImage( img );
        resultTabs.getSelectionModel().select( ellipsesResultTab );
    }
    public void
    setExecTime( String s )
    {
        this.execTime.setText( s );
    }

    public Image
    getSelectedImage()
    {
        Image img;
        String tab = resultTabs.getSelectionModel().getSelectedItem().getId();

        if ( tab.equals( "linesResultTab" ) )
        {
            img = this.linesResultView.getImage();
        }

        if ( tab.equals( "ellipsesResultTab" ) )
        {
            img = this.ellipsesResultView.getImage();
        }
        else if ( tab.equals( "blurTab" ) )
        {
            img = this.blurView.getImage();
        }
        else if ( tab.equals( "sobelTab" ) )
        {
            img = this.sobelView.getImage();
        }
        else if ( tab.equals( "accuTab" ) )
        {
            img = this.accumulatorView.getImage();
        }
        else
        {
            img = null;
        }

        return img;
    }

}
