/*
 *  File name:
 *      Main.java
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

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class
Main extends Application
{

    @Override
    public void
    start( Stage stage ) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dartsproject.fxml"));
        Parent root;

        try
        {
            root = loader.load();
            MainController mainController = loader.getController();
            Context.getInstance().setPrimaryStage( stage );
            Scene scene = new Scene(root);

            //scene.getStylesheets().add(getClass().getResource("../style.css").toExternalForm());

            stage.setScene( scene );
            stage.setTitle( "Dart Detection" );
            stage.setMinWidth( 1240.0 );
            stage.setMinHeight( 720.0 );
            stage.show();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }


    public static void
    main(String[] args)
    {
        launch(args);
    }
}
