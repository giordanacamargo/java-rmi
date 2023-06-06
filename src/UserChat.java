import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JEditorPane;

//Adicionar o Highlighter
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;


public class UserChat extends java.rmi.server.UnicastRemoteObject implements IUserChat {
    private String usrName;
    private String msg;
    private ArrayList<String> roomList =  new ArrayList<>(); //Todas as salas do servidor
    private IRoomChat currentRoom = null; // Sala do usuário
    private IServerChat server = null;

    // JAVA SWING
    private JFrame frame;
    private JTextArea messageArea;
    private JTextField messageField;
    private JButton sendMessageButton = new JButton("Enviar mensagem");;
    private JButton joinRoomButton = new JButton("Entrar na sala");
    private JButton createRoomButton = new JButton("Criar sala");
    private JButton leaveRoomButton = new JButton("Sair da sala");;
    private JList<String> roomJList;



    public UserChat(String usrName, ArrayList<String> roomList) throws Exception{
        this.usrName = usrName;
        this.roomList = roomList;
        createInterface();
    }

    public void createInterface() {
        createFrame();
        createPanelJoinRoom();
        this.frame.setVisible(true);
    }

    public void createPanelJoinRoom() {
        JPanel panel = new JPanel(new BorderLayout());
        this.roomJList = new JList(roomList.toArray());
        this.roomJList.setVisibleRowCount(1);
        this.roomJList.setSelectionMode(0);
        this.roomJList.setLayoutOrientation(1);
        JScrollPane panelList = new JScrollPane(this.roomJList, 21, 30);
        panel.add(roomJList);
        panel.add(panelList, "North");
        this.joinRoomButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent var1) {
                try {
                    if (UserChat.this.currentRoom != null) {
                        UserChat.this.currentRoom.leaveRoom(UserChat.this.usrName);
                        UserChat.this.currentRoom = null;
                    }

                    if (UserChat.this.roomJList.getSelectedValue() == null) {
                        return;
                    }

                    Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                    IRoomChat sala = (IRoomChat) registry.lookup(UserChat.this.roomJList.getSelectedValue()); // Conecta na sala
                    UserChat.this.currentRoom = sala;
                    sala.joinRoom(UserChat.this.usrName, UserChat.this);
                    UserChat.this.SendMessage("Olá à Todos!!");
                    UserChat.this.frame.setTitle("Olá, " + UserChat.this.usrName + "! Você está na sala: " + UserChat.this.currentRoom.getRoomName());
                } catch (Exception var3) {
                    var3.printStackTrace();
                }

            }
        });

        panel.add(joinRoomButton, "South");
        panel.add(createRoomButton, "West");

        this.frame.setContentPane(panel);
    }

    public void createFrame(){
        this.frame = new JFrame("Olá, " + this.usrName);
        this.frame.setDefaultCloseOperation(3);
        this.frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        this.frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent var1) {
                if (JOptionPane.showConfirmDialog(UserChat.this.frame, "Tem certeza que deseja sair?", "Fechar a janela?", 0, 3) == 0 && UserChat.this.currentRoom != null) {
                    try {
                        UserChat.this.currentRoom.leaveRoom(UserChat.this.usrName);
                    } catch (Exception var3) {
                        var3.printStackTrace();
                    }

                    UserChat.this.currentRoom = null;
                    UserChat.this.frame.setTitle("Usuário: " + UserChat.this.usrName);
                    System.exit(0);
                }

            }
        });
    }

    public void deliverMsg(String senderName, String msg) 
    {
        //var line = in.nextLine();
        //int sizeChat = document.getLength();
        //document.insertString(sizeChat, line.substring(12) + "\n", null);
        System.out.println("Mensagem: " + msg + ". Recebida de: " + senderName);
    }
    private void ConnectServer() throws Exception{        
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        this.server = (IServerChat) registry.lookup("Servidor");
    }
    private void ShowRooms() throws Exception
    {
        System.out.println(this.server.getRooms());
    }
    private void SelectRoom(String roomName) throws Exception
    {        
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        this.currentRoom = (IRoomChat) registry.lookup(roomName);
        currentRoom.joinRoom(this.usrName, this);
    }
    private void SendMessage(String message) throws Exception
    {
        if(currentRoom != null)
            currentRoom.sendMsg(this.usrName, message);
    }

    public static void main (String args[]) throws Exception{
        // Criar instância da implementação da interface remota
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        IServerChat server = (IServerChat) registry.lookup("Servidor");


        String userName = JOptionPane.showInputDialog(null, "Digite seu nome de usuário");
        
        UserChat client = new UserChat(userName, server.getRooms());   
        // Vincular o objeto remoto a um nome no Registro RMI
        Naming.rebind(userName, client);
        System.out.println("O usuário " + userName + " foi registrado no Registry com sucesso.");
        
        //Faz os trâmites de se conectar ao servidor e a sala
        client.ConnectServer();
        //client.ShowRooms();
        //client.server.createRoom("Sala_Criada_4");
        //client.ShowRooms();
        //client.SelectRoom("Sala_Inicial_1");
    }

    public String getUsrName() 
    {
        return usrName;
    }
    public void setUsrName(String usrName) 
    {
        this.usrName = usrName;
    }

}
