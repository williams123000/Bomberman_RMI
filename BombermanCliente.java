import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;
import java.awt.*;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;

import java.io.File;
import java.io.IOException;

import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.util.List;

public class BombermanCliente extends JFrame {
    static int ID;

    int Number_Players;
    int ID_Player;
    MyKeyListener myKeyListener = new MyKeyListener();
    static Bomberman server2;
    // static String[][] Tablero_Game;
    ScheduledExecutorService Segundo_Plano = Executors.newSingleThreadScheduledExecutor();
    ScheduledFuture<?> Tarea;
    boolean State_Game = true;

    BombermanCliente() {
        super("The Bomberman");
    }

    // Método para cargar una fuente personalizada desde un archivo TTF
    private Font loadCustomFont(String fontPath) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File(fontPath)).deriveFont(Font.PLAIN, 12);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            // Manejar la excepción apropiadamente (puedes mostrar un mensaje de error,
            // utilizar una fuente predeterminada, etc.)
            return new Font("Arial", Font.PLAIN, 12);
        }
    }

    // Método para aplicar la fuente a todos los componentes de la interfaz
    private static void setUIFont(FontUIResource font) {
        UIManager.put("Button.font", font);
        UIManager.put("ToggleButton.font", font);
        UIManager.put("RadioButton.font", font);
        UIManager.put("CheckBox.font", font);
        UIManager.put("ColorChooser.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("List.font", font);
        UIManager.put("MenuBar.font", font);
        UIManager.put("MenuItem.font", font);
        UIManager.put("RadioButtonMenuItem.font", font);
        UIManager.put("CheckBoxMenuItem.font", font);
        UIManager.put("Menu.font", font);
        UIManager.put("PopupMenu.font", font);
        UIManager.put("OptionPane.font", font);
        UIManager.put("Panel.font", font);
        UIManager.put("ProgressBar.font", font);
        UIManager.put("ScrollPane.font", font);
        UIManager.put("Viewport.font", font);
        UIManager.put("TabbedPane.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("TableHeader.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("PasswordField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("TextPane.font", font);
        UIManager.put("EditorPane.font", font);
        UIManager.put("TitledBorder.font", font);
        UIManager.put("ToolBar.font", font);
        UIManager.put("ToolTip.font", font);
        UIManager.put("Tree.font", font);
    }

    // Método para crear un ImageIcon desde un archivo PNG
    private ImageIcon createImageIcon(String path) {
        try {
            return new ImageIcon(getClass().getResource(path));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void limpiarJFrame() {
        // Eliminar todos los componentes dentro del JFrame
        getContentPane().removeAll();

        // Actualizar la interfaz gráfica
        revalidate();
        repaint();
    }

    private void Juego_GUI() {
        try {
            String[][] Tablero = server2.Obtener_Campo();
            Font customFont = loadCustomFont("PixelGameFont.ttf");
            JLabel label = new JLabel("** PARTIDA ** ");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setForeground(Color.WHITE);
            label.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
            // Aumentar el tamaño de la fuente personalizada
            Font originalFont = customFont.deriveFont(customFont.getSize2D() + 8f);
            label.setFont(originalFont);

            JPanel Panel_Juego = new JPanel();
            Panel_Juego.setPreferredSize(new Dimension(352, 352));
            Panel_Juego.setLayout(new GridLayout(11, 11));
            int marginSize = 10;
            Panel_Juego.setBorder(new EmptyBorder(marginSize, 58, marginSize, 58));
            Panel_Juego.setBackground(Color.BLACK);
            int Number_Players_Living = 0;

            boolean Living = true;
            ArrayList<Estado> Estados = server2.Obtener_Estado();
            for (Jugador Jugador : Estados.get(Estados.size() - 1).Jugadores) {
                if (Jugador.Estado == true) {
                    Tablero[Jugador.Posicion.X][Jugador.Posicion.Y] = Jugador.Simbolo;
                    Number_Players_Living++;
                }
                if (Jugador.ID == ID_Player) {
                    Living = Jugador.Estado;
                }
            }

            if (Living == false) {
                if (State_Game == true) {
                    Tarea.cancel(true);
                    Object[] opciones = { "Seguir viendo la partida", "Salir" };
                    int opcion = JOptionPane.showOptionDialog(
                            null,
                            "¿Qué deseas hacer?",
                            "Te mataron :(",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            opciones,
                            opciones[0] // Opción predeterminada seleccionada
                    );

                    // Verificar cuál opción fue seleccionada
                    if (opcion == JOptionPane.YES_OPTION) {
                        State_Game = false;
                        Tarea.cancel(false);
                        // Agrega la lógica para "Seguir viendo la partida" aquí
                    } else if (opcion == JOptionPane.NO_OPTION) {
                        dispose();
                        System.exit(0);
                        // Agrega la lógica para "Salir" aquí
                    } else if (opcion == JOptionPane.CLOSED_OPTION) {
                        dispose();
                        System.exit(0);
                        // Puedes manejar el caso en que el usuario cierre el diálogo sin seleccionar
                        // una opción
                    }
                }

            }

            if (Number_Players_Living == 1) {
                Tarea.cancel(true);
                JOptionPane.showMessageDialog(null, "Haz ganado!", "Ganador!!!", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                System.exit(0);
            }

            for (Bomba Bomba : Estados.get(Estados.size() - 1).Bombas) {
                int comparacion = Bomba.Hora_Estallido.compareTo(LocalDateTime.now());
                if (comparacion > 0) {
                    Tablero[Bomba.Posicion.X][Bomba.Posicion.Y] = "O";
                }

                int comparacion2 = Bomba.Hora_Explosion.compareTo(LocalDateTime.now());
                if (comparacion < 0 && comparacion2 > 0) {
                    Tablero[Bomba.Posicion.X][Bomba.Posicion.Y] = "X";
                    if (!Tablero[Bomba.Posicion.X - 1][Bomba.Posicion.Y].equals("1")) {
                        if (!Tablero[Bomba.Posicion.X - 1][Bomba.Posicion.Y].equals("0")) {
                            server2.eliminacion(Tablero[Bomba.Posicion.X - 1][Bomba.Posicion.Y]);
                        }
                        Tablero[Bomba.Posicion.X - 1][Bomba.Posicion.Y] = "W";
                    }
                    if (!Tablero[Bomba.Posicion.X + 1][Bomba.Posicion.Y].equals("1")) {
                        if (!Tablero[Bomba.Posicion.X + 1][Bomba.Posicion.Y].equals("0")) {
                            server2.eliminacion(Tablero[Bomba.Posicion.X + 1][Bomba.Posicion.Y]);
                        }
                        Tablero[Bomba.Posicion.X + 1][Bomba.Posicion.Y] = "S";
                    }
                    if (!Tablero[Bomba.Posicion.X][Bomba.Posicion.Y + 1].equals("1")) {
                        if (!Tablero[Bomba.Posicion.X][Bomba.Posicion.Y + 1].equals("0")) {
                            server2.eliminacion(Tablero[Bomba.Posicion.X][Bomba.Posicion.Y + 1]);
                        }
                        Tablero[Bomba.Posicion.X][Bomba.Posicion.Y + 1] = "F";
                    }
                    if (!Tablero[Bomba.Posicion.X][Bomba.Posicion.Y - 1].equals("1")) {
                        if (!Tablero[Bomba.Posicion.X][Bomba.Posicion.Y - 1].equals("1")) {
                            server2.eliminacion(Tablero[Bomba.Posicion.X][Bomba.Posicion.Y - 1]);
                        }
                        Tablero[Bomba.Posicion.X][Bomba.Posicion.Y - 1] = "G";
                    }
                }
            }

            for (int i = 0; i < Tablero.length; i++) {
                for (int j = 0; j < Tablero.length; j++) {
                    if (Tablero[i][j].equals("A")) {
                        JLabel Prueba = new JLabel();
                        Prueba.setIcon(new ImageIcon("Images/Player1.png"));
                        Border border = BorderFactory.createLineBorder(Color.BLACK);
                        Prueba.setBorder(border);
                        Panel_Juego.add(Prueba);
                    }

                    if (Tablero[i][j].equals("B")) {
                        JLabel Prueba = new JLabel();
                        Prueba.setIcon(new ImageIcon("Images/Player2.png"));
                        Border border = BorderFactory.createLineBorder(Color.BLACK);
                        Prueba.setBorder(border);
                        Panel_Juego.add(Prueba);
                    }

                    if (Tablero[i][j].equals("C")) {
                        JLabel Prueba = new JLabel();
                        Prueba.setIcon(new ImageIcon("Images/Player3.png"));
                        Border border = BorderFactory.createLineBorder(Color.BLACK);
                        Prueba.setBorder(border);
                        Panel_Juego.add(Prueba);
                    }
                    if (Tablero[i][j].equals("D")) {
                        JLabel Prueba = new JLabel();
                        Prueba.setIcon(new ImageIcon("Images/Player4.png"));
                        Border border = BorderFactory.createLineBorder(Color.BLACK);
                        Prueba.setBorder(border);
                        Panel_Juego.add(Prueba);
                    }

                    if (Tablero[i][j].equals("O")) {
                        JLabel Prueba = new JLabel();
                        Prueba.setIcon(new ImageIcon("Images/Bomb.png"));
                        Border border = BorderFactory.createLineBorder(Color.BLACK);
                        Prueba.setBorder(border);
                        Panel_Juego.add(Prueba);
                    }

                    if (Tablero[i][j].equals("X")) {
                        JLabel Prueba = new JLabel();
                        Prueba.setIcon(new ImageIcon("Images/Burst_Central.png"));
                        Border border = BorderFactory.createLineBorder(Color.BLACK);
                        Prueba.setBorder(border);
                        Panel_Juego.add(Prueba);
                    }
                    if (Tablero[i][j].equals("W")) {
                        JLabel Prueba = new JLabel();
                        Prueba.setIcon(new ImageIcon("Images/Burst_Up.png"));
                        Border border = BorderFactory.createLineBorder(Color.BLACK);
                        Prueba.setBorder(border);
                        Panel_Juego.add(Prueba);
                    }
                    if (Tablero[i][j].equals("S")) {
                        JLabel Prueba = new JLabel();
                        Prueba.setIcon(new ImageIcon("Images/Burst_Down.png"));
                        Border border = BorderFactory.createLineBorder(Color.BLACK);
                        Prueba.setBorder(border);
                        Panel_Juego.add(Prueba);
                    }
                    if (Tablero[i][j].equals("F")) {
                        JLabel Prueba = new JLabel();
                        Prueba.setIcon(new ImageIcon("Images/Burst_Right.png"));
                        Border border = BorderFactory.createLineBorder(Color.BLACK);
                        Prueba.setBorder(border);
                        Panel_Juego.add(Prueba);
                    }
                    if (Tablero[i][j].equals("G")) {
                        JLabel Prueba = new JLabel();
                        Prueba.setIcon(new ImageIcon("Images/Burst_Left.png"));
                        Border border = BorderFactory.createLineBorder(Color.BLACK);
                        Prueba.setBorder(border);
                        Panel_Juego.add(Prueba);
                    }

                    if (Tablero[i][j].equals("1")) {
                        JLabel Prueba = new JLabel();
                        Prueba.setIcon(new ImageIcon("Images/Muro.png"));
                        Border border = BorderFactory.createLineBorder(Color.BLACK);
                        Prueba.setBorder(border);
                        Panel_Juego.add(Prueba);
                    }

                    if (Tablero[i][j].equals("0")) {
                        JLabel Prueba = new JLabel();
                        Prueba.setIcon(new ImageIcon("Images/Empty.png"));
                        Border border = BorderFactory.createLineBorder(Color.BLACK);
                        Prueba.setBorder(border);
                        Panel_Juego.add(Prueba);
                    }
                }
            }

            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(label, BorderLayout.NORTH);
            getContentPane().add(Panel_Juego, BorderLayout.CENTER);
            addKeyListener(myKeyListener);
            revalidate();
            repaint();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    List<Integer> Ubicar_Player() {
        try {
            int X_Axis = 0;
            int Y_Axis = 0;
            ArrayList<Estado> Estados = server2.Obtener_Estado();
            for (Jugador Jugador : Estados.get(Estados.size() - 1).Jugadores) {
                if (Jugador.ID == ID_Player) {
                    X_Axis = Jugador.Posicion.X;
                    Y_Axis = Jugador.Posicion.Y;
                }
            }
            List<Integer> Posicion = new ArrayList<>();
            Posicion.add(X_Axis);
            Posicion.add(Y_Axis);

            return Posicion;
        } catch (RemoteException e) {

            e.printStackTrace();
        }
        return null;
    }

    void Move_Up(List<Integer> Posicion) {
        try {
            String[][] Tablero = server2.Obtener_Campo();
            int X_Axis = Posicion.get(0);
            int Y_Axis = Posicion.get(1);
            if (!Tablero[X_Axis - 1][Y_Axis].equals("1")) {
                server2.movimiento(ID_Player, X_Axis - 1, Y_Axis);
                // Tablero_Game[X_Axis][Y_Axis] = "0";
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    void Move_Down(List<Integer> Posicion) {
        try {
            String[][] Tablero = server2.Obtener_Campo();
            int X_Axis = Posicion.get(0);
            int Y_Axis = Posicion.get(1);
            if (!Tablero[X_Axis + 1][Y_Axis].equals("1")) {
                server2.movimiento(ID_Player, X_Axis + 1, Y_Axis);
                // Tablero_Game[X_Axis][Y_Axis] = "0";
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    void Move_Left(List<Integer> Posicion) {
        try {
            String[][] Tablero = server2.Obtener_Campo();
            int X_Axis = Posicion.get(0);
            int Y_Axis = Posicion.get(1);
            if (!Tablero[X_Axis][Y_Axis - 1].equals("1")) {
                server2.movimiento(ID_Player, X_Axis, Y_Axis - 1);
                // Tablero_Game[X_Axis][Y_Axis] = "0";
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    void Move_Right(List<Integer> Posicion) {
        try {
            String[][] Tablero = server2.Obtener_Campo();
            int X_Axis = Posicion.get(0);
            int Y_Axis = Posicion.get(1);
            if (!Tablero[X_Axis][Y_Axis + 1].equals("1")) {
                server2.movimiento(ID_Player, X_Axis, Y_Axis + 1);
                // Tablero_Game[X_Axis][Y_Axis] = "0";
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    void Place_Bomb(List<Integer> Posicion) {
        int X_Axis = Posicion.get(0);
        int Y_Axis = Posicion.get(1);
        try {
            server2.ponerBomba(ID_Player, X_Axis, Y_Axis);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class MyKeyListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            // No necesitas implementar esto para las teclas de flecha
        }

        @Override
        public void keyPressed(KeyEvent e) {
            // Verificar las teclas de flecha y realizar acciones correspondientes
            List<Integer> Posicion = Ubicar_Player();
            int keyCode = e.getKeyCode();

            switch (keyCode) {
                case KeyEvent.VK_UP:

                    Move_Up(Posicion);
                    // removeKeyListener(myKeyListener);
                    // limpiarJFrame();
                    // Juego_GUI();

                    break;
                case KeyEvent.VK_DOWN:

                    Move_Down(Posicion);
                    // removeKeyListener(myKeyListener);
                    // limpiarJFrame();
                    // Juego_GUI();
                    // Acción cuando se presiona la flecha hacia abajo
                    break;
                case KeyEvent.VK_LEFT:

                    Move_Left(Posicion);
                    // removeKeyListener(myKeyListener);
                    // limpiarJFrame();
                    // Juego_GUI();
                    // Acción cuando se presiona la flecha hacia la izquierda
                    break;
                case KeyEvent.VK_RIGHT:
                    Move_Right(Posicion);
                    // removeKeyListener(myKeyListener);
                    // limpiarJFrame();
                    // Juego_GUI();

                    // Acción cuando se presiona la flecha hacia la derecha
                    break;
                case KeyEvent.VK_SPACE:
                    Place_Bomb(Posicion);
                    // Acción cuando se presiona la barra espaciadora
                    break;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // Puedes dejar este método vacío si no necesitas realizar acciones cuando se
            // libera una tecla.
        }
    }

    private void SalaPartida_GUI() {

        try {
            Font customFont = loadCustomFont("PixelGameFont.ttf");
            JLabel label = new JLabel("SALA DE LA PARTIDA");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setForeground(Color.WHITE);
            label.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));

            // Aumentar el tamaño de la fuente personalizada
            Font originalFont = customFont.deriveFont(customFont.getSize2D() + 8f);
            label.setFont(originalFont);

            // Crear un JLabel con el texto y centrarlo en la ventana
            JLabel label1 = new JLabel("Faltan algun los jugadores ...");
            label1.setHorizontalAlignment(SwingConstants.CENTER);
            label1.setVerticalAlignment(SwingConstants.CENTER);
            label1.setForeground(Color.WHITE);
            label1.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));

            Font originalFont1 = customFont.deriveFont(customFont.getSize2D() + 5f);
            label1.setFont(originalFont1);

            ArrayList<Jugador> Jugadores = server2.Jugadores_Listos();

            DefaultTableModel modelo = new DefaultTableModel();
            JTable tabla = new JTable(modelo);

            Object[] columnas = { "ID", "Nombre", "Simbolo" };
            modelo.setColumnIdentifiers(columnas);

            for (Jugador Jugador : Jugadores) {
                Object[] filaDatos = new Object[3];
                filaDatos[0] = Jugador.ID;
                filaDatos[1] = Jugador.Nombre;
                filaDatos[2] = Jugador.Simbolo;
                modelo.addRow(filaDatos);
            }

            // Envuelve la tabla en un JScrollPane
            JScrollPane scrollPane = new JScrollPane(tabla);
            tabla.revalidate();
            tabla.repaint();

            // Crear un botón para aceptar
            JButton aceptarButton = new JButton("Aceptar");
            aceptarButton.addActionListener(e -> {
                // Acciones al hacer clic en "Aceptar"

                try {
                    if (server2.partidaLista()) {
                        // label1.setText("Todos los jugadores listos ...");
                        limpiarJFrame();
                        int periodoSegundos = 100; // Ajusta el periodo según tus necesidades
                        Tarea = Segundo_Plano.scheduleAtFixedRate(() -> {
                            // Lógica de la tarea que se ejecutará en segundo plano
                            SwingUtilities.invokeLater(() -> {
                                removeKeyListener(myKeyListener);
                                limpiarJFrame();
                                Juego_GUI();
                            });
                        }, 0, periodoSegundos, TimeUnit.MILLISECONDS);

                    } else {
                        JOptionPane.showMessageDialog(null, "Aun no estan todos los jugadores", "SUCCESS de SERVER",
                                JOptionPane.INFORMATION_MESSAGE);
                        limpiarJFrame();
                        SalaPartida_GUI();
                    }
                } catch (RemoteException i) {
                    i.printStackTrace();
                }
            });

            // Crear un JPanel para el botón Aceptar
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(aceptarButton);
            buttonPanel.setBackground(Color.BLACK);

            // Establecer la altura del JPanel a 100 píxeles
            int panelHeight = 100;
            buttonPanel.setPreferredSize(new Dimension(buttonPanel.getPreferredSize().width, panelHeight));

            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(label, BorderLayout.NORTH);
            getContentPane().add(scrollPane, BorderLayout.CENTER);
            // getContentPane().add(label1, BorderLayout.CENTER);
            getContentPane().add(buttonPanel, BorderLayout.SOUTH);
            revalidate();
            repaint();

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void CrearJugador_GUI() {
        Font customFont = loadCustomFont("PixelGameFont.ttf");

        // Crear un JLabel con el texto y centrarlo en la ventana
        JLabel label = new JLabel("CREAR JUGADOR");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));

        // Aumentar el tamaño de la fuente personalizada
        Font originalFont = customFont.deriveFont(customFont.getSize2D() + 8f);
        label.setFont(originalFont);

        // Crear un JPanel para el JTextField con un alto fijo
        JPanel textFieldPanel = new JPanel(new GridBagLayout());
        textFieldPanel.setBackground(Color.BLACK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 30, 0, 30); // Ajustar los márgenes según sea necesario

        JLabel userLabel = new JLabel("Usuario del Jugador");
        userLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // Expandir horizontalmente
        textFieldPanel.add(userLabel, gbc);

        JTextField Name_Player = new JTextField(20);
        Name_Player.setPreferredSize(new Dimension(Name_Player.getPreferredSize().width, 30));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0; // Expandir horizontalmente
        textFieldPanel.add(Name_Player, gbc);

        // Crear un botón para aceptar
        JButton aceptarButton = new JButton("Aceptar");
        aceptarButton.addActionListener(e -> {
            // Acciones al hacer clic en "Aceptar"

            try {
                if (server2.Limite_Jugadores()) {
                    String _Name_Player = Name_Player.getText();
                    ID_Player = server2.nuevoJugador(_Name_Player);
                    JOptionPane.showMessageDialog(null, "Jugador creado correctamente", "SUCCESS de SERVER",
                            JOptionPane.INFORMATION_MESSAGE);
                    limpiarJFrame();
                    Open_Window();
                } else {
                    JOptionPane.showMessageDialog(null, "Ya hay una partida en curso ...", "Error de SERVER",
                            JOptionPane.WARNING_MESSAGE);
                    limpiarJFrame();
                    Open_Window();
                }

            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        });

        // Crear un JPanel para el botón Aceptar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(aceptarButton);
        buttonPanel.setBackground(Color.BLACK);

        // Establecer la altura del JPanel a 100 píxeles
        int panelHeight = 100;
        buttonPanel.setPreferredSize(new Dimension(buttonPanel.getPreferredSize().width, panelHeight));

        // Usar un BorderLayout para el contenedor principal
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(label, BorderLayout.NORTH);
        getContentPane().add(textFieldPanel, BorderLayout.CENTER); // Usar el nuevo JPanel
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private void CrearNuevaPartida_GUI() {
        Font customFont = loadCustomFont("PixelGameFont.ttf");

        // Crear un JLabel con el texto y centrarlo en la ventana
        JLabel label = new JLabel("NUEVA PARTIDA");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setForeground(Color.WHITE); // Cambiar el color del texto según sea necesario
        label.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));

        // Aumentar el tamaño de la fuente personalizada
        Font originalFont = customFont.deriveFont(customFont.getSize2D() + 8f);
        label.setFont(originalFont);

        // Crear JCheckBox para seleccionar el número de jugadores
        JCheckBox checkBox2Jugadores = new JCheckBox("2 Jugadores");
        JCheckBox checkBox4Jugadores = new JCheckBox("4 Jugadores");

        Font originalFont_ = customFont.deriveFont(customFont.getSize2D() + 4f);

        checkBox2Jugadores.setBackground(Color.BLACK);
        checkBox2Jugadores.setFont(originalFont_);
        checkBox2Jugadores.setForeground(Color.WHITE);
        checkBox4Jugadores.setBackground(Color.BLACK);
        checkBox4Jugadores.setFont(originalFont_);
        checkBox4Jugadores.setForeground(Color.WHITE);

        // Crear un ButtonGroup para que solo se pueda seleccionar una opción a la vez
        ButtonGroup buttonGroup = new ButtonGroup();

        buttonGroup.add(checkBox2Jugadores);
        buttonGroup.add(checkBox4Jugadores);

        // Agregar un ActionListener al checkBox para manejar el evento
        ActionListener checkBoxActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBox2Jugadores.isSelected()) {
                    System.out.println("Seleccionado: 2 Jugadores");
                    Number_Players = 2;
                } else if (checkBox4Jugadores.isSelected()) {
                    System.out.println("Seleccionado: 4 Jugadores");
                    Number_Players = 4;
                }
            }
        };

        checkBox2Jugadores.addActionListener(checkBoxActionListener);
        checkBox4Jugadores.addActionListener(checkBoxActionListener);

        // Crear un JPanel para los checkBox con GridLayout
        JPanel checkBoxPanel = new JPanel(new GridBagLayout());
        checkBoxPanel.setBackground(Color.BLACK);
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = new Insets(0, 0, 0, 20);
        checkBoxPanel.add(checkBox2Jugadores, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.insets = new Insets(0, 20, 0, 0);

        checkBoxPanel.add(checkBox4Jugadores, c);

        // Crear un botón para aceptar
        JButton aceptarButton = new JButton("Aceptar");
        aceptarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Acciones al hacer clic en "Aceptar"
                try {
                    if (server2.Validar_Partida()) {
                        JOptionPane.showMessageDialog(null, "Ya hay una partida en curso ...", "Error de SERVER",
                                JOptionPane.WARNING_MESSAGE);
                        limpiarJFrame();
                        Open_Window();
                    } else {
                        server2.nuevaPartida(Number_Players);
                        limpiarJFrame();
                        Open_Window();
                    }
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
        });

        // Crear un JPanel para el botón Aceptar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(aceptarButton);

        buttonPanel.setBackground(Color.BLACK);

        // Establecer la altura del JPanel a 100 píxeles
        int panelHeight = 100;
        buttonPanel.setPreferredSize(new Dimension(buttonPanel.getPreferredSize().width, panelHeight));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(label, BorderLayout.NORTH);
        getContentPane().add(checkBoxPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private void Open_Window() {
        Font customFont = loadCustomFont("PixelGameFont.ttf");
        // Aplicar la fuente al JFrame y sus componentes
        setUIFont(new FontUIResource(customFont));

        setSize(500, 500);
        // Obtener la resolución de la pantalla
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Calcular la posición para centrar la ventana
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;

        // Establecer la posición de la ventana
        setLocation(x, y);

        getContentPane().setBackground(Color.BLACK);

        // Crear un JLabel con el texto y centrarlo en la ventana
        JLabel label = new JLabel("THE BOMBERMAN");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setForeground(Color.WHITE); // Cambiar el color del texto según sea necesario

        // Crear un JLabel para la imagen
        File file = new File("Bomberman.jpg");
        try {
            BufferedImage originalImage = ImageIO.read(file);

            // Calcular las nuevas dimensiones basadas en un porcentaje
            double scalePercentage = 0.5; // 50% del tamaño original
            int newWidth = (int) (originalImage.getWidth() * scalePercentage);
            int newHeight = (int) (originalImage.getHeight() * scalePercentage);
            Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

            ImageIcon imageIcon = new ImageIcon(scaledImage);
            JLabel jLabel = new JLabel(imageIcon);
            jLabel.setHorizontalAlignment(SwingConstants.CENTER);
            jLabel.setVerticalAlignment(SwingConstants.CENTER);

            // Usar un BorderLayout para organizar la imagen y el texto
            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(jLabel, BorderLayout.NORTH); // La imagen estará en el centro
            getContentPane().add(label, BorderLayout.SOUTH); // El texto estará en la parte inferior
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Crear botones con ActionListener para manejar eventos
        JButton nuevaPartidaButton = new JButton("NUEVA PARTIDA");
        JButton crearJugadorButton = new JButton("CREAR JUGADOR");
        JButton iniciarPartidaButton = new JButton("INICIAR PARTIDA");
        JButton salirButton = new JButton("SALIR");

        // Crear un objeto Insets para definir el margen interno
        Insets buttonInsets = new Insets(20, 0, 20, 0); // (arriba, izquierda, abajo, derecha)
        nuevaPartidaButton.setMargin(buttonInsets);

        // nuevaPartidaButton.setContentAreaFilled(false);
        // nuevaPartidaButton.setBorderPainted(false);
        nuevaPartidaButton.setBackground(Color.ORANGE);// inside the brackets your rgb color value like 255,255,255
        nuevaPartidaButton.setForeground(Color.RED);
        // nuevaPartidaButton.setFocusPainted(false);
        // nuevaPartidaButton.setOpaque(true);

        crearJugadorButton.setMargin(buttonInsets);

        crearJugadorButton.setContentAreaFilled(false);
        crearJugadorButton.setBorderPainted(false);
        crearJugadorButton.setBackground(Color.ORANGE);// inside the brackets your rgb color value like 255,255,255
        crearJugadorButton.setForeground(Color.RED);
        crearJugadorButton.setFocusPainted(false);
        crearJugadorButton.setOpaque(true);

        iniciarPartidaButton.setContentAreaFilled(false);
        iniciarPartidaButton.setBorderPainted(false);
        iniciarPartidaButton.setBackground(Color.ORANGE);// inside the brackets your rgb color value like 255,255,255
        iniciarPartidaButton.setForeground(Color.RED);
        iniciarPartidaButton.setFocusPainted(false);
        iniciarPartidaButton.setOpaque(true);

        salirButton.setContentAreaFilled(false);
        salirButton.setBorderPainted(false);
        salirButton.setBackground(Color.ORANGE);// inside the brackets your rgb color value like 255,255,255
        salirButton.setForeground(Color.RED);
        salirButton.setFocusPainted(false);
        salirButton.setOpaque(true);

        // Establecer un ancho específico a los botones
        int buttonWidth = 25;
        int buttonHeight = 50;
        nuevaPartidaButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        crearJugadorButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        iniciarPartidaButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        salirButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));

        // Agregar ActionListener a cada botón
        nuevaPartidaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica para la opción de Nueva Partida
                limpiarJFrame();
                CrearNuevaPartida_GUI();
            }
        });

        crearJugadorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica para la opción de Crear Jugador
                try {
                    if (server2.Validar_Partida()) {
                        limpiarJFrame();
                        CrearJugador_GUI();
                    } else {
                        JOptionPane.showMessageDialog(null, "Aun no hay una partida en curso ...", "Error de SERVER",
                                JOptionPane.WARNING_MESSAGE);
                    }
                } catch (RemoteException e1) {

                    e1.printStackTrace();
                }

            }
        });

        iniciarPartidaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica para la opción de Iniciar Partida
                try {
                    if (server2.Validar_Partida()) {
                        limpiarJFrame();
                        SalaPartida_GUI();
                    } else {
                        JOptionPane.showMessageDialog(null, "Aun no hay una partida en curso ...", "Error de SERVER",
                                JOptionPane.WARNING_MESSAGE);
                    }
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }

            }
        });

        salirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica para la opción de Salir
                
                System.exit(0); // Cierra la aplicación
            }
        });

        // Crear un JPanel para los botones con GridLayout
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(Color.BLACK);
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        c.insets = new Insets(10, 100, 0, 100);
        buttonPanel.add(nuevaPartidaButton, c);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        c.weightx = 0.5;
        c.insets = new Insets(10, 100, 0, 100);
        buttonPanel.add(crearJugadorButton, c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        c.weightx = 0.5;
        c.insets = new Insets(10, 100, 0, 100);
        buttonPanel.add(iniciarPartidaButton, c);
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 3;
        c.weightx = 0.5;
        c.insets = new Insets(10, 100, 20, 100);
        buttonPanel.add(salirButton, c);

        // buttonPanel.setLayout(new BorderLayout(0,0));
        // Agregar el panel de botones debajo del texto
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }


    /**
     * Método principal que inicia y ejecuta el cliente de Bomberman.
     * 
     * @param args Los argumentos de la línea de comandos (se espera la dirección
     *             del servidor).
     */
    public static void main(String[] args) {
        // Obtener la dirección del servidor desde los argumentos de la línea de
        // comandos
        String host = (args.length < 1) ? null : args[0];
        try {
            // Obtener la referencia remota del servidor de Bomberman
            Registry registry = LocateRegistry.getRegistry(host, 1099);
            Bomberman server = (Bomberman) registry.lookup("BombermanServer");
            server2 = server;

            new BombermanCliente().Open_Window();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}