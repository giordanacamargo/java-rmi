import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import java.rmi.RemoteException;

public class ServerChat extends UnicastRemoteObject implements IServerChat {
    public ServerChat() throws RemoteException {

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
            server.createRoom("Sala_Inicial_1");
            server.createRoom("Sala_Inicial_2");
            server.createRoom("Sala_Inicial_3");


        } catch (Exception e) {
            System.out.println("Erro na criação do servidor. :C");
            System.out.println(e.getMessage());
        }       
    }

    public ArrayList<String> getRoomList() {
        return roomList;
    }

    public void setRoomList(ArrayList<String> roomList) {
        this.roomList = roomList;
    }
}