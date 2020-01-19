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
import java.awt.event.ItemEvent;
import javax.swing.ImageIcon;
import java.awt.BorderLayout;
//import java.awt.Choice;
import java.awt.Choice;
import java.awt.Component;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;

import java.awt.color.*;
//import java.awt.image.ColorConvertOp;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;

//import java.awt.image.BufferedImageOp;
//import java.awt.image.ConvolveOp;
//import java.awt.image.Kernel;
//import java.awt.image.LookupOp;
//import java.awt.image.ShortLookupTable;
import java.awt.image.*;

import java.rmi.RemoteException;
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import javax.swing.JFrame;

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
//import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;
import javax.swing.ButtonGroup;
//import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
//import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import java.util.*;

public class Servidor extends Interfaz implements transformarImagen{
    private JFrame seleccion;
    private Player player;
    private ServerSocket server;
    private Socket socket;
    private String dispositivo;
    private Thread hilo;
    private int puerto;
    private static Registry registro;
    private Image imageFinal;
    //private Image imageFinal2;
    
    private BufferedImage mBufferedImage; // NUEVO

    
    public static void main(String[] args) {
        Servidor servidor = new Servidor();
    }


    public Servidor() {
        super("Servidor de video");
        this.mostrarSeleccion();
        try {
            // crear el registro y ligar el nombre y objeto.
            registro = LocateRegistry.createRegistry(puerto);
            registro.rebind("rmiServidor", this);
        } catch (RemoteException e) {
            
        }
        hilo = new Thread(this);
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(puerto);
            System.out.println("Esperando conexiones ...");
            socket = server.accept();
            System.out.println("Aceptando conexion ...");
            salida = new ObjectOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void mostrarSeleccion() {
        Vector listaDispositivos = CaptureDeviceManager.getDeviceList();
        Iterator iterador = listaDispositivos.iterator();
        seleccion = new JFrame("Seleccione el dispositivo");
        seleccion.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel panelSeleccion = new JPanel();
        ButtonGroup grupo = new ButtonGroup();

        while (iterador.hasNext()) {
            CaptureDeviceInfo cdi = (CaptureDeviceInfo) iterador.next();
            JRadioButton radio = new JRadioButton(cdi.getName());
            radio.addActionListener(this);
            grupo.add(radio);
            panelSeleccion.add(radio);
        }

        seleccion.getContentPane().add(panelSeleccion, BorderLayout.CENTER);
        seleccion.setSize(300, 300);
        seleccion.setVisible(true);
        createOps();
    }

    @Override
    public void agregarComponentes() {
        super.agregarComponentes();
        CaptureDeviceInfo dev = CaptureDeviceManager.getDevice(dispositivo);
        Format[] formatos = dev.getFormats();
//        RGBFormat formatoRGB = (RGBFormat) formatos[4];
        MediaLocator loc = dev.getLocator();

        try {
            player = Manager.createRealizedPlayer(loc);
        } catch (NoPlayerException e1) {
            JOptionPane.showMessageDialog(this, e1.getMessage());
        } catch (CannotRealizeException e2) {
            JOptionPane.showMessageDialog(this, e2.getMessage());
        } catch (IOException e3) {
            JOptionPane.showMessageDialog(this, e3.getMessage());
        }

        player.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            System.out.println(e1.getMessage());
        }

        Component componente = player.getVisualComponent();

        if (componente != null) {
            panelCamara.add(componente, BorderLayout.CENTER);
            this.getContentPane().add(panelCamara, BorderLayout.NORTH);
        }

        this.pack();
        this.setVisible(true);

        hilo.start();
    }
    
    public Image transform(Image imagen, String key){
       // String key = "Posterize";
    
        mBufferedImage = new BufferedImage(imagen.getWidth(null), imagen.getHeight(null),BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = mBufferedImage.createGraphics();
        g2.drawImage(imagen, null, null);
        BufferedImageOp op = (BufferedImageOp)mOps.get(key);
        mBufferedImage = op.filter(mBufferedImage, null);
        imageFinal = (Image) mBufferedImage;
        
        return imageFinal;
    }

    /* PARTE IMAGEN */
    
    private Hashtable mOps;
  
  /**
   * The createOps() method creates the image processing
   * operations discussed in the column.
  **/
  private void createOps() {
    // Create a brand new Hashtable to hold the operations.
    mOps = new Hashtable();
    
    // Blurring
    float ninth = 1.0f / 9.0f;
    float[] blurKernel = {
        ninth, ninth, ninth,
        ninth, ninth, ninth,
        ninth, ninth, ninth,
    };
    mOps.put("Blur", new ConvolveOp(
        new Kernel(3, 3, blurKernel)));

    // Edge detection
    float[] edgeKernel = {
        0.0f, -1.0f, 0.0f,
        -1.0f, 4.0f, -1.0f,
        0.0f, -1.0f, 0.0f
    };
    mOps.put("Edge detector", new ConvolveOp(
        new Kernel(3, 3, edgeKernel)));

    // Sharpening
    float[] sharpKernel = {
        0.0f, -1.0f, 0.0f,
        -1.0f, 5.0f, -1.0f,
        0.0f, -1.0f, 0.0f
    };
    mOps.put("Sharpen", new ConvolveOp(
        new Kernel(3, 3, sharpKernel),
        ConvolveOp.EDGE_NO_OP, null));

    // Lookup table operations: posterizing and inversion.
    short[] posterize = new short[256];
    short[] invert = new short[256];
    short[] straight = new short[256];
    for (int i = 0; i < 256; i++) {
      posterize[i] = (short)(i - (i % 32));
      invert[i] = (short)(255 - i);
      straight[i] = (short)i;
    }
    
    mOps.put("Posterize", new LookupOp(new ShortLookupTable(0, posterize),null));
    mOps.put("Invert", new LookupOp(new ShortLookupTable(0, invert), null));
    short[][] blueInvert = new short[][] { straight, straight, invert };
    mOps.put("Invert blue", new LookupOp(new ShortLookupTable(0, blueInvert), null));
    
    // Thresholding
    mOps.put("Threshold 192", createThresholdOp(192, 0, 255));
    mOps.put("Threshold 128", createThresholdOp(128, 0, 255));
    mOps.put("Threshold 64", createThresholdOp(64, 0, 255));
  }
  

  /**
   * createThresholdOp() uses a LookupOp to simulate a
   * thresholding operation.
  **/
  private BufferedImageOp createThresholdOp(int threshold,
    int minimum, int maximum) {
    short[] thresholdArray = new short[256];
    for (int i = 0; i < 256; i++) {
      if (i < threshold)
        thresholdArray[i] = (short)minimum;
      else
        thresholdArray[i] = (short)maximum;
      }
    return new LookupOp(new ShortLookupTable(0, thresholdArray), null);
  }
    /*FIN IMAGEN*/
    
  
   
    @Override
    public void actionPerformed(ActionEvent evento){
        String accion = evento.getActionCommand();
        //Image imagenGrises;

        if (evento.getSource() instanceof JRadioButton) {
            dispositivo = evento.getActionCommand();
            JOptionPane.showMessageDialog(this, "Dispositivo \"" + dispositivo + "\" seleccionado");
            seleccion.dispose();

            while (true) {
                String tempo = JOptionPane.showInputDialog(this, "Por favor digite el numero del puerto a utilizar");

                try {
                    puerto = Integer.parseInt(tempo);
                    break;
                } catch (Exception e) {
                    continue;
                }
            }

            this.agregarComponentes();
        }

        if (accion.equalsIgnoreCase("Capturar")) {
            FrameGrabbingControl fgc = (FrameGrabbingControl)
                player.getControl("javax.media.control.FrameGrabbingControl");
            Buffer buffer = fgc.grabFrame();
            BufferToImage btoi = new BufferToImage((VideoFormat) buffer.getFormat());
            imagen = btoi.createImage(buffer);  
             
            // Create a list of operations.
    
            final Choice processChoice = new Choice();
            Enumeration e = mOps.keys();
            
            // Add all the operation names from the Hashtable.
            while (e.hasMoreElements())
                processChoice.add((String)e.nextElement());
            
  processChoice.addItemListener(new ItemListener() {  
   public void itemStateChanged(ItemEvent ie) {
            //if (ie.getStateChange() == ItemEvent.SELECTED) return;
              String key = processChoice.getSelectedItem();
              //System.out.println(key);
             imageFinal = transform(imagen,key);
      }
    });
             panelImagen.setImage(imageFinal);
             panelImagen.add(processChoice);
            
             try {
                ImageIcon i = new ImageIcon(imageFinal);
                salida.writeObject(i);
                salida.flush();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        if (accion.equalsIgnoreCase("Guardar")) {
            if (imagen != null) {
                int result = chooser.showSaveDialog(this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File archivo = chooser.getSelectedFile();
                    String nombre = archivo.getName().toLowerCase();

                    if (!nombre.endsWith(".jpg")) {
                        nombre = archivo.getAbsolutePath() + ".jpg";
                    }

                    try {
                        ImageIO.write((RenderedImage) imageFinal, "JPEG", new File(nombre));
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "No se pudo guardar la imagen");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Primero debe realizar una captura");
            }
        }

        if (accion.equalsIgnoreCase("Salir")) {
            player.close();
            hilo.interrupt();

            try {
                salida.flush();
                salida.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.exit(0);
        }
    }

    public Image transform(Image imagen) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}