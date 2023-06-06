import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

public class ServerChat extends UnicastRemoteObject implements IServerChat {

    // JAVA SWING
    private JFrame frame;
    private JButton closeRoomButton = new JButton("Fechar sala");
    private JButton createRoomButton = new JButton("Criar sala");
    private JList<String> roomJList;

    public ServerChat() throws RemoteException {
        createButtons();
        UpdateRooms();
    }
    private ArrayList<String> roomList =  new ArrayList<>();

    public ArrayList<String> getRooms() {
        return getRoomList();
    }

    @Override
    public void createRoom(String roomName) {
        try {
            if (this.roomList.contains(roomName)) {
                System.out.println("Esta sala já existe, não foi possível completar a ação.");
            } else {

                try {
                    // Criar instância da implementação da interface remota
                    RoomChat newRoomChat = new RoomChat(roomName);

                    // Vincular o objeto remoto a um nome no Registro RMI
                    Naming.rebind("//localhost/" + roomName, newRoomChat);

                    this.roomList.add(roomName);
                    System.out.println("A sala " + roomName + " foi criada com sucesso.");
                    this.UpdateRooms();

                } catch (MalformedURLException | RemoteException var3) {
                    var3.printStackTrace();
                }

            }
        }
        catch (Exception e) {
            System.out.println("Erro na criação da sala :" + roomName + ". :C");
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            // Inicia o registro RMI na porta desejada
            LocateRegistry.createRegistry(1099);

            // Criar instância da implementação da interface remota
            ServerChat server = new ServerChat();

            // Vincular o objeto remoto a um nome no Registro RMI
            Naming.rebind("//localhost/Servidor", server);

            System.out.println("Servidor RMI pronto para receber chamadas...");
            server.createRoom("Sala_Inicial_Padrão");
            server.UpdateRooms();

        } catch (Exception e) {
            System.out.println("Erro na criação do servidor. :C");
            System.out.println(e.getMessage());
        }
    }

    public void UpdateRooms() {
        if (this.frame != null) {
            this.frame.setVisible(false);
        }
        this.frame = null;
        createFrame();
        createPanelRooms();
        this.frame.setVisible(true);
    }

    public void createButtons(){
        this.closeRoomButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent var1) {
                try {
                    if (ServerChat.this.roomJList.getSelectedValue() == null) {
                        return;
                    }

                    Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                    IRoomChat sala = (IRoomChat) registry.lookup(ServerChat.this.roomJList.getSelectedValue()); // Conecta na sala
                    sala.closeRoom();
                    roomList.remove(ServerChat.this.roomJList.getSelectedValue());

                    UpdateRooms();
                } catch (Exception var3) {
                    var3.printStackTrace();
                }

            }
        });
        this.createRoomButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent var1) {
                try {
                    String roomName = JOptionPane.showInputDialog(null, "Digite o nome da sala:");
                    if (roomName == null || roomName.equals("")) {
                        return;
                    }
                    createRoom(roomName);
                    UpdateRooms();
                } catch (Exception var3) {
                    var3.printStackTrace();
                }
            }
        });
    }
    public void createPanelRooms () {
        JPanel panel = new JPanel(new BorderLayout());
        this.roomJList = new JList(roomList.toArray());
        this.roomJList.setVisibleRowCount(1);
        this.roomJList.setSelectionMode(0);
        this.roomJList.setLayoutOrientation(1);
        JScrollPane panelList = new JScrollPane(this.roomJList, 21, 30);
        panel.add(roomJList);
        panel.add(panelList, "North");
        this.frame.setContentPane(panel);

        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout(FlowLayout.CENTER));
        this.closeRoomButton.setBackground(Color.RED);
        this.createRoomButton.setBackground(Color.GREEN);
        painelBotoes.add(closeRoomButton);
        painelBotoes.add(createRoomButton);

        // Adicionando o painel ao JFrame
        this.frame.getContentPane().add(painelBotoes, BorderLayout.SOUTH);
    }

    public void createFrame(){
        this.frame = new JFrame("Gerenciamento de Salas");
        this.frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        this.frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                int res = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja encerrar o programa?",  "Fechar", JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION) {
                    try {
                        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                        for (String room: roomList) {
                            IRoomChat sala = (IRoomChat) registry.lookup(room);
                            sala.closeRoom();
                        }
                    } catch (Exception var3) {
                        var3.printStackTrace();
                    }
                    ServerChat.this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    System.exit(0);
                } else {
                    ServerChat.this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }

    public ArrayList<String> getRoomList() {
        return roomList;
    }
}