package hospital.ui;

import com.google.gson.JsonObject;
import hospital.Cliente.WebSocketClienteMensajeria;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

public class VentanaConMensajeria extends JFrame {

    private WebSocketClienteMensajeria clienteWS;
    private PanelUsuarios panelUsuarios;
    private String userId;
    private String userName;

    public VentanaConMensajeria(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;

        setTitle("Sistema de Gestión - Usuario: " + userName);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(1000, 700);

        inicializarWebSocket();
        crearInterfaz();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void inicializarWebSocket() {
        try {
            URI serverUri = new URI("ws://localhost:8887");
            clienteWS = new WebSocketClienteMensajeria(serverUri);

            clienteWS.configurarCallbacks(
                    usuarios -> panelUsuarios.actualizarUsuarios(usuarios),
                    mensaje -> panelUsuarios.mostrarMensajeRecibido(mensaje)
            );

            clienteWS.connect();

            // Esperar conexión y registrar usuario
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    clienteWS.registrarUsuario(userId, userName);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al conectar con el servidor de mensajería:\n" + e.getMessage(),
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void crearInterfaz() {
        // Panel central (tu contenido actual)
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblBienvenida = new JLabel(
                "Bienvenido " + userName + " - Sistema de Gestión Hospitalaria",
                SwingConstants.CENTER
        );
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 18));
        panelCentral.add(lblBienvenida, BorderLayout.NORTH);

        // Aquí va tu contenido principal del sistema
        JTextArea areaContenido = new JTextArea("Área principal del sistema...");
        areaContenido.setFont(new Font("Monospaced", Font.PLAIN, 12));
        panelCentral.add(new JScrollPane(areaContenido), BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);

        // Panel de usuarios a la derecha
        panelUsuarios = new PanelUsuarios(clienteWS, userId);
        add(panelUsuarios, BorderLayout.EAST);

        // Panel inferior con información
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblInfo = new JLabel("Sistema de mensajería en tiempo real activo");
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 10));
        panelInferior.add(lblInfo);
        add(panelInferior, BorderLayout.SOUTH);
    }

    @Override
    public void dispose() {
        if (clienteWS != null && clienteWS.isOpen()) {
            clienteWS.close();
        }
        super.dispose();
    }

    // Método para probar la aplicación
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String userId = JOptionPane.showInputDialog("Ingrese su ID de usuario:");
            String userName = JOptionPane.showInputDialog("Ingrese su nombre:");

            if (userId != null && userName != null) {
                new VentanaConMensajeria(userId, userName);
            }
        });
    }
}