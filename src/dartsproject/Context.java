/*
 *  File name:
 *      Context.java
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
    private String currentImagePath = null;


    public Image
    getCurrentImage()
    {
        if ( this.currentImage == null )
        {
            String path = "img/dartboard-23938_960_720.png";
            File f = new File( path );

            try
            {
                this.currentImage = new Image( f.toURI().toURL().toString() );
                this.currentImagePath = path;
            }
            catch ( MalformedURLException e )
            {
                e.printStackTrace();
            }
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
            this.currentImagePath = path;
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
            String path = file.toURI().toURL().toString();
            this.currentImage = new Image( path );
            this.currentImagePath = path;
        }
        catch( MalformedURLException e )
        {
            e.printStackTrace();
        }
    }
    public String
    getCurrentImagePath()
    {
        return this.currentImagePath;
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
