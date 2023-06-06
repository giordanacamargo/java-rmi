import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.util.Map;
import java.util.HashMap;

public class RoomChat extends java.rmi.server.UnicastRemoteObject implements IRoomChat {

    private Map<String, IUserChat> userList;

    private String roomName;

    public RoomChat(String roomName) throws RemoteException {
        this.roomName = roomName;
        this.userList = new HashMap<String, IUserChat>();
    }

    public void sendMsg(String usrName, String msg) throws Exception {
        for (Map.Entry<String, IUserChat> pair : userList.entrySet()) {
            pair.getValue().deliverMsg(usrName, msg);
        }
    }
    public void joinRoom(String usrName, IUserChat user) {
        userList.put(usrName, user);
    }
    public void leaveRoom(String usrName) {
        userList.remove(usrName);
        System.out.println("\nUsuários: ");
        System.out.println(userList);
    }
    public void closeRoom() {
        try{
            this.sendMsg("Sala fechada pelo servidor.", "Servidor");
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            registry.unbind(this.roomName);
        }catch(Exception e){
            System.out.println("Erro durante o fechamento da sala.");
        }
    }
    public String getRoomName() {
        return roomName;
    }
}