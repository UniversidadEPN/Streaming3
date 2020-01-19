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
import java.awt.*;
import javax.swing.*;

class ImagePanel extends JPanel {
    private Image imagen = null;

    public ImagePanel() {
        super();
        setPreferredSize(new Dimension(320, 240));
    }

    public void setImage(Image imagen) {
        this.imagen = imagen;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (imagen != null) {
            g.drawImage(imagen, 3, 3, this);
        }
    }
}