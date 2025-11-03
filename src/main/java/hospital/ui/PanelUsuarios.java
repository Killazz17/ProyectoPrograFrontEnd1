package hospital.ui;

import com.google.gson.JsonObject;
import hospital.Cliente.WebSocketClienteMensajeria;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class PanelUsuarios extends JPanel {

    private final DefaultListModel<String> modeloLista;
    private final JList<String> listaUsuarios;
    private final WebSocketClienteMensajeria clienteWS;
    private final String usuarioActual;
    private final JLabel lblEstado;

    public PanelUsuarios(WebSocketClienteMensajeria clienteWS, String usuarioActual) {
        this.clienteWS = clienteWS;
        this.usuarioActual = usuarioActual;

        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Usuarios Activos",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)
        ));
        setPreferredSize(new Dimension(200, 0));

        // Label de estado
        lblEstado = new JLabel("Conectando...");
        lblEstado.setFont(new Font("Arial", Font.ITALIC, 10));
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblEstado, BorderLayout.NORTH);

        // Lista de usuarios
        modeloLista = new DefaultListModel<>();
        listaUsuarios = new JList<>(modeloLista);
        listaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaUsuarios.setFont(new Font("Arial", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(listaUsuarios);
        add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new GridLayout(2, 1, 5, 5));

        JButton btnEnviar = new JButton("Enviar Mensaje");
        JButton btnRecibir = new JButton("Ver Mensajes");

        btnEnviar.setFont(new Font("Arial", Font.BOLD, 11));
        btnRecibir.setFont(new Font("Arial", Font.BOLD, 11));

        btnEnviar.addActionListener(e -> enviarMensaje());
        btnRecibir.addActionListener(e -> verMensajesPendientes());

        panelBotones.add(btnEnviar);
        panelBotones.add(btnRecibir);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(panelBotones, BorderLayout.SOUTH);
    }

    public void actualizarUsuarios(List<String> usuarios) {
        modeloLista.clear();
        usuarios.forEach(modeloLista::addElement);
        lblEstado.setText("Conectado - " + usuarios.size() + " usuario(s)");
    }

    private void enviarMensaje() {
        String destinatario = listaUsuarios.getSelectedValue();

        if (destinatario == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor seleccione un usuario de la lista",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String mensaje = JOptionPane.showInputDialog(
                this,
                "Ingrese su mensaje para " + destinatario + ":",
                "Enviar Mensaje",
                JOptionPane.PLAIN_MESSAGE
        );

        if (mensaje != null && !mensaje.trim().isEmpty()) {
            clienteWS.enviarMensaje(destinatario, mensaje);
            JOptionPane.showMessageDialog(
                    this,
                    "Mensaje enviado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void verMensajesPendientes() {
        JOptionPane.showMessageDialog(
                this,
                "No hay mensajes pendientes",
                "Mensajes",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void mostrarMensajeRecibido(JsonObject mensaje) {
        String remitente = mensaje.get("remitenteNombre").getAsString();
        String contenido = mensaje.get("contenido").getAsString();

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "Mensaje de: " + remitente + "\n\n" + contenido + "\n\n¿Desea responder?",
                "Mensaje Recibido",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );

        if (opcion == JOptionPane.YES_OPTION) {
            String respuesta = JOptionPane.showInputDialog(
                    this,
                    "Escriba su respuesta:",
                    "Responder a " + remitente,
                    JOptionPane.PLAIN_MESSAGE
            );

            if (respuesta != null && !respuesta.trim().isEmpty()) {
                clienteWS.enviarMensaje(
                        mensaje.get("remitenteId").getAsString(),
                        respuesta
                );
                JOptionPane.showMessageDialog(
                        this,
                        "Respuesta enviada",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    }
}