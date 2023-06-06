import javax.swing.*;
import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;


public class UserChat extends java.rmi.server.UnicastRemoteObject implements IUserChat {
    private String usrName;
    private ArrayList<String> roomList =  new ArrayList<>(); //Todas as salas do servidor
    private IRoomChat currentRoom = null; // Sala do usuário
    private IServerChat server = null;

    // JAVA SWING
    private JFrame frameRooms;
    private JFrame frameChatRoom;
    private JPanel panelRooms;
    private JTextPane messageArea = new JTextPane();
    JTextField textField = new JTextField(50);
    private JButton joinRoomButton = new JButton("Entrar na sala");
    private JButton createRoomButton = new JButton("Criar sala");
    private JList<String> roomJList;

    public UserChat(String usrName, IServerChat server) throws Exception{
        this.usrName = usrName;
        this.server = server;
        createButtons();
        updateAndShowPanelRooms();
    }

    public void createButtons() {
        this.joinRoomButton.addActionListener(ev -> {
            try {
                if (UserChat.this.currentRoom != null) {
                    UserChat.this.currentRoom.leaveRoom(UserChat.this.usrName);
                    UserChat.this.currentRoom = null;
                }

                if (UserChat.this.roomJList.getSelectedValue() == null) {
                    return;
                }

                try {
                    Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                    IRoomChat sala = (IRoomChat) registry.lookup(UserChat.this.roomJList.getSelectedValue()); // Conecta na sala
                    UserChat.this.currentRoom = sala;
                    sala.joinRoom(UserChat.this.usrName, UserChat.this);
                    showPanelChatRoom();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Sala foi fechada pelo Servidor.");
                    updateAndShowPanelRooms();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        this.createRoomButton.addActionListener(ev ->{
            try {
                String roomName = JOptionPane.showInputDialog(null, "Digite o nome da sala:");
                if (roomName == null || roomName.equals("")) {
                    return;
                }
                this.server.createRoom(roomName);
                this.roomList = this.server.getRooms();
                updateAndShowPanelRooms();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void createFrameRooms(){
        this.frameRooms = new JFrame("Olá, " + this.usrName);
        this.frameRooms.setSize(600, 400);
        frameRooms.setLocationRelativeTo(null);
        this.frameRooms.addWindowListener(new WindowAdapter() {
            public void windowClosing (WindowEvent ev) {
                int res = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja encerrar o programa?",  "Fechar", JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION) {
                    try {
                        UserChat.this.currentRoom.leaveRoom(UserChat.this.usrName);
                        UserChat.this.currentRoom = null;
                        System.exit(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    UserChat.this.frameRooms.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                } else {
                    UserChat.this.frameRooms.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }

    public void createFrameChatRooms () throws Exception {
        this.frameChatRoom = new JFrame("Olá, " + this.usrName + "! Você está na sala " + this.currentRoom.getRoomName());
        this.frameChatRoom.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.frameChatRoom.setSize(800, 800);
        frameChatRoom.setLocationRelativeTo(null);
        this.frameChatRoom.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                if (JOptionPane.showConfirmDialog(UserChat.this.frameChatRoom, "Tem certeza que deseja sair da sala?",
                        "Fechar a janela?", 0, 3) == 0 && UserChat.this.frameChatRoom != null) {

                    try {
                        UserChat.this.currentRoom.leaveRoom(UserChat.this.usrName);
                        UserChat.this.currentRoom = null;
                        UserChat.this.frameChatRoom.setVisible(false);
                        UserChat.this.frameRooms.setVisible(true);
                        UserChat.this.messageArea.getStyledDocument().remove(0, UserChat.this.messageArea.getStyledDocument().getLength());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    public void showPanelChatRoom () throws Exception {
        this.frameRooms.setVisible(false);
        this.frameChatRoom = null;
        createFrameChatRooms();
        messageArea.setPreferredSize(new Dimension(700, 700) );
        textField.setEditable(true);
        messageArea.setEditable(false);
        this.frameChatRoom.getContentPane().add(textField, BorderLayout.SOUTH);
        this.frameChatRoom.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        this.frameChatRoom.pack();

        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    UserChat.this.currentRoom.sendMsg(UserChat.this.usrName, UserChat.this.textField.getText());
                    textField.setText("");
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        this.frameChatRoom.setVisible(true);
    }

    public void updateAndShowPanelRooms (){
        try {
            UpdateRoomList();
            if (this.frameRooms != null) {
                this.frameRooms.setVisible(false);
            }
            this.frameRooms = null;
            createFrameRooms();
            createPanelRooms();
            this.frameRooms.setVisible(true);
        } catch (Exception e) {
            System.out.println("Problema na hora de atualizar o painel de salas.");
        }
    }

    public void createPanelRooms () {
        this.panelRooms = new JPanel(new BorderLayout());
        this.roomJList = new JList(roomList.toArray());
        this.roomJList.setVisibleRowCount(1);
        this.roomJList.setSelectionMode(0);
        this.roomJList.setLayoutOrientation(1);
        JScrollPane panelList = new JScrollPane(this.roomJList, 21, 30);
        this.panelRooms.add(roomJList);
        this.panelRooms.add(panelList, "North");
        this.frameRooms.setContentPane(this.panelRooms);

        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout(FlowLayout.CENTER));
        this.joinRoomButton.setBackground(Color.GREEN);
        this.createRoomButton.setBackground(Color.BLUE);
        this.createRoomButton.setForeground(Color.white);
        painelBotoes.add(createRoomButton);
        painelBotoes.add(joinRoomButton);

        // Adicionando o painel ao JFrame
        this.frameRooms.getContentPane().add(painelBotoes, BorderLayout.SOUTH);
    }

    public void deliverMsg(String senderName, String msg) throws BadLocationException {
        appendToPane(senderName, msg, Color.magenta);
        if (senderName == "Servidor" && msg == "Sala fechada pelo servidor.") {
            try {
                //ajustar
                UserChat.this.currentRoom.leaveRoom(UserChat.this.usrName);
                UserChat.this.currentRoom = null;
                UserChat.this.frameChatRoom.setVisible(false);
                UserChat.this.frameRooms.setVisible(true);
                UserChat.this.messageArea.getStyledDocument().remove(0, UserChat.this.messageArea.getStyledDocument().getLength());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void ConnectServer() throws Exception {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        this.server = (IServerChat) registry.lookup("Servidor");
    }

    public static void main (String args[]) throws Exception{
        // Criar instância da implementação da interface remota
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        IServerChat server = (IServerChat) registry.lookup("Servidor");
        String userName = null;

        while (userName == null || userName.equals("")){
            userName = JOptionPane.showInputDialog(null, "Digite seu nome de usuário");
        }


        UserChat client = new UserChat(userName, server);
        // Vincular o objeto remoto a um nome no Registro RMI
        Naming.rebind(userName, client);

        //Faz os trâmites de se conectar ao servidor e a sala
        client.UpdateRoomList();
        client.ConnectServer();
    }


    public void appendToPane(String name, String msg, Color c) throws BadLocationException {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c); //cor do nome
        aset = sc.addAttribute( aset, StyleConstants.FontFamily, "Lucida Console" );
        aset = sc.addAttribute( aset, StyleConstants.Bold, true);
        messageArea.getStyledDocument().insertString(messageArea.getDocument().getLength(), name+ ": ", aset);

        StyleContext sc2 = StyleContext.getDefaultStyleContext();
        AttributeSet aset2 = sc2.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
        aset2 = sc2.addAttribute( aset2, StyleConstants.FontFamily, "Lucida Console" );
        aset2 = sc2.addAttribute( aset2, StyleConstants.Italic, true);
        messageArea.getStyledDocument().insertString(messageArea.getDocument().getLength(), msg + "\n", aset2);
    }

    public void UpdateRoomList() {
        try {
            this.roomList = server.getRooms();
        } catch (Exception e) {
            System.out.println("Erro na atualização das salas.");
        }
    }
}
