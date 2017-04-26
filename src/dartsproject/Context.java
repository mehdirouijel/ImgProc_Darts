package dartsproject;


import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;


public class
Context
{

    /*  Singleton stuff
     *
     * * * * */

    private static Context instance = null;

    private Context()
    {
    }

    // NOTE: 'synchronized' should make this thread-safe.
    //       Not that I'll be using any multithreading in this...
    //       But you know. Why not?
    public synchronized static Context
    getInstance()
    {
        if ( instance == null )
        {
            instance = new Context();
        }

        return instance;
    }


    /*  Shared data
     *
     * * * * */

    private Stage primaryStage = null;
    private Image currentImage = null;


    public Image
    getCurrentImage()
    {
        if ( this.currentImage == null )
        {
            File f = new File( "img/dartboard-23938_960_720.png" );
            this.currentImage = new Image( f.toURI().toString() );
        }

        return this.currentImage;
    }

    public void
    setCurrentImage( String path )
    {
        File f = new File( path );
        try
        {
            this.currentImage = new Image( f.toURI().toURL().toString() );
        }
        catch( MalformedURLException e )
        {
            e.printStackTrace();
        }
    }
    public void
    setCurrentImage( File file )
    {
        try
        {
            this.currentImage = new Image( file.toURI().toURL().toString() );
        }
        catch( MalformedURLException e )
        {
            e.printStackTrace();
        }
    }


    public Stage
    getPrimaryStage()
    {
        return this.primaryStage;
    }

    public void
    setPrimaryStage( Stage s )
    {
        this.primaryStage = s;
    }

}
