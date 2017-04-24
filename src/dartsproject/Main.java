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


public class
Main extends Application
{

    @Override
    public void
    start( Stage primaryStage ) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource( "dartsproject.fxml" ) );
        primaryStage.setTitle( "Dart Detection" );
        primaryStage.setScene( new Scene( root, 1270, 720 ) );
        primaryStage.show();
    }


    public static void
    main(String[] args)
    {
        launch(args);
    }
}
