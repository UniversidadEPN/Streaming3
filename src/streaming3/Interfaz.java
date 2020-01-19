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
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.CannotRealizeException;
import javax.media.CaptureDeviceInfo;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.cdm.CaptureDeviceManager;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class Interfaz extends JFrame implements ActionListener, Runnable, Serializable {
    protected JButton botonCapturar;
    protected JButton botonGuardar;
    protected JButton botonSalir;
    protected JFileChooser chooser;
    protected JPanel panelCamara;
    protected JPanel panelBotones;
    protected ImagePanel panelImagen;
    protected Image imagen = null;
    protected ObjectInputStream entrada;
    protected ObjectOutputStream salida;

    public Interfaz(String titulo) {
        super(titulo);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void agregarComponentes() {
        botonCapturar = new JButton("Capturar");
        botonCapturar.setPreferredSize(new Dimension(90,25));
        botonCapturar.addActionListener(this);
        botonGuardar = new JButton("Guardar");
        botonGuardar.setPreferredSize(new Dimension(90,25));
        botonGuardar.addActionListener(this);
        botonSalir = new JButton("Salir");
        botonSalir.setPreferredSize(new Dimension(90,25));
        botonSalir.addActionListener(this);

        panelCamara = new JPanel();
        panelBotones = new JPanel();
        panelImagen = new ImagePanel();

        chooser = new JFileChooser(".");

        panelBotones.add(botonCapturar, BorderLayout.EAST);
        panelBotones.add(botonGuardar, BorderLayout.CENTER);
        panelBotones.add(botonSalir, BorderLayout.WEST);

        this.getContentPane().add(panelBotones, BorderLayout.CENTER);
        this.getContentPane().add(panelImagen, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent evento) {
    }

    public void run() {
    }
}