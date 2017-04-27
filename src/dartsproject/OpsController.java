/*
 *  File name:
 *      OpsController.java
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

import java.net.URL;
import java.util.ResourceBundle;


public class
OpsController implements Initializable
{

    private MainController mainCtrl;


    @Override
    public void
    initialize( URL loc, ResourceBundle res )
    {
    }

    public void
    init( MainController mainController )
    {
        this.mainCtrl = mainController;
    }

}
