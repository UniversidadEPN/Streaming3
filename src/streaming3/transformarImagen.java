/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package streaming3;

/**
 *
 * @author julio
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author The Xkiver
 */

import java.rmi.*;
import java.awt.Image;

public interface transformarImagen extends Remote {
    //Este es el metodo que implementa el servidor
    public Image transform(Image imagen) throws RemoteException;
}
