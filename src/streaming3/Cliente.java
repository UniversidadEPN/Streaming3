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
import javax.swing.ImageIcon;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.rmi.*;
import java.rmi.registry.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.media.Player;

public class Cliente extends Interfaz {
    private Player player;
    private Socket socket;
    private Thread hilo;
    private JFrame conexion;
    private JTextField campoHost;
    private JTextField campoPuerto;
    private String host;
    private static int puerto = 1234;
    private static transformarImagen img;
    private static Registry registro;
    private static String direccionServidor = "127.0.0.1";

    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        //conectarseAlServidor();
    }
    
    private static void conectarseAlServidor() {
        try {
        // obtener el registro
        registro = LocateRegistry.getRegistry(direccionServidor,(new Integer(puerto)).intValue());
        // creando el objeto remoto
        img = (transformarImagen) (registro.lookup("rmiServidor"));
        } catch (RemoteException e) {
        e.printStackTrace();
        } catch (NotBoundException e) {
        e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Image imagenGris;
        while (true) {
            try {
                ImageIcon icono = (ImageIcon) entrada.readObject();
                imagen = icono.getImage();
                panelImagen.setImage(imagen);
                //imagenGris = img.transform(imagen);
                //panelImagen.setImage(imagenGris);
            } catch(IOException e1) {
            } catch(ClassNotFoundException e2) {
            }
        }
    }

    public Cliente() {
        super("Cliente");
        this.mostrarConexion();

        hilo = new Thread(this);
    }

    public void mostrarConexion() {
        conexion = new JFrame("Conexion al servidor");
        conexion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel panelConexion = new JPanel();
        JButton botonConectar = new JButton("Conectar");
        botonConectar.addActionListener(this);
        campoHost = new JTextField("localhost", 20);
        campoPuerto = new JTextField("1234", 20);
        panelConexion.add(new JLabel("Nombre o direccion del servidor:"));
        panelConexion.add(campoHost);
        panelConexion.add(new JLabel("Numero del puerto:"));
        panelConexion.add(campoPuerto);
        panelConexion.add(botonConectar);

        conexion.getContentPane().add(panelConexion, BorderLayout.CENTER);
        conexion.setSize(300, 300);
        conexion.setVisible(true);
    }

    @Override
    public void agregarComponentes() {
        super.agregarComponentes();

        this.pack();
        this.setVisible(true);

        hilo.start();
    }

    @Override
    public void actionPerformed(ActionEvent evento) {
        String accion = evento.getActionCommand();

        if (accion.equalsIgnoreCase("Conectar")) {
            if (campoHost.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Por favor digite el nombre o direcci�n del servidor");
                campoHost.grabFocus();
                return;
            }

            host = campoHost.getText();
            puerto = 0;

            try {
                puerto = Integer.parseInt(campoPuerto.getText());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Por favor digite un n�mero correcto para el puerto");
                campoPuerto.grabFocus();
                return;
            }

            while (true) {
                try {
                    socket = new Socket(host, puerto);
                    entrada = new ObjectInputStream(socket.getInputStream());
                    break;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    JOptionPane.showMessageDialog(this, "No se pudo establecer la conexi�n");
                    campoHost.setText("");
                    campoPuerto.setText("");
                    return;
                }
            }

            conexion.dispose();
            this.agregarComponentes();
            botonCapturar.setEnabled(false);
        }

        if (accion.equalsIgnoreCase("Capturar")) {
           try {
                ImageIcon i = (ImageIcon) entrada.readObject();
                imagen = i.getImage();
                System.out.println(imagen);
                panelImagen.setImage(imagen);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        if (accion.equalsIgnoreCase("Guardar")) {
            imagen = panelImagen.createImage(320, 240);

            if (imagen != null) {
                int result = chooser.showSaveDialog(this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File archivo = chooser.getSelectedFile();
                    String nombre = archivo.getName().toLowerCase();

                    if (!nombre.endsWith(".jpg")) {
                        nombre = archivo.getAbsolutePath() + ".jpg";
                    }

                    try {
                        ImageIO.write((RenderedImage) imagen, "JPEG", new File(nombre));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        JOptionPane.showMessageDialog(this, "No se pudo guardar la imagen");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Primero debe realizar una captura");
            }
        }

        if (accion.equalsIgnoreCase("Salir")) {
            hilo.interrupt();

            try {
                salida.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.exit(0);
        }
    }
}
