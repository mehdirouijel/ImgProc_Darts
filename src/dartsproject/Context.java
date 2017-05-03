/*
 *  File name:
 *      Context.java
 *
 *  ====================
 *  Description:
 *      Implementation of a Singleton pattern to share data accross
 *      the application.
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

import java.awt.image.BufferedImage;
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
    private File currentFile = null;
    private BufferedImage houghAccu = null;
    private BufferedImage sobelImage = null;


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
                this.currentFile = f;
            }
            catch ( MalformedURLException e )
            {
                e.printStackTrace();
            }
        }

        return this.currentImage;
    }

    public void
    setCurrentImage( File file )
    {
        try
        {
            String path = file.toURI().toURL().toString();
            this.currentImage = new Image( path );
            this.currentImagePath = path;
            this.currentFile = file;
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
    public File
    getCurrentFile()
    {
        return this.currentFile;
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

    public BufferedImage
    getHoughAccu()
    {
        return this.houghAccu;
    }
    public void
    setHoughAccu( BufferedImage img )
    {
        this.houghAccu = img;
    }

    public BufferedImage
    getSobelImage()
    {
        return this.sobelImage;
    }
    public void
    setSobelImage( BufferedImage img )
    {
        this.sobelImage = img;
    }

}
