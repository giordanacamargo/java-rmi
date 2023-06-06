import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class RoomChat extends java.rmi.server.UnicastRemoteObject implements IRoomChat {

    private Map<String, IUserChat> userList;

    private String roomName;
    private boolean setToClose = false;

    public RoomChat(String roomName) throws RemoteException {
        this.roomName = roomName;
        this.userList = new HashMap<String, IUserChat>();
    }

    public void sendMsg(String usrName, String msg) throws Exception {
        ArrayList<IUserChat> users = new ArrayList<>();
        for (Map.Entry<String, IUserChat> pair : userList.entrySet()) {
            users.add(pair.getValue());
        }
        for(IUserChat u: users)
        {
            u.deliverMsg(usrName, msg);
        }
    }
    public void joinRoom(String usrName, IUserChat user) {
        userList.put(usrName, user);
        System.out.println("Usuário adicionado: " + usrName);
        System.out.println("Usuários: " + userList);
    }
    public void leaveRoom(String usrName) {
        userList.remove(usrName);
        if(setToClose)
        {
            if(userList.isEmpty())
            {
                try{
                    Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                    registry.unbind(this.roomName);
                }catch(Exception e){
                    System.out.println("Erro durante o fechamento da sala.");
                }
            }
        }

    }
    public void closeRoom() {
        try{
            this.sendMsg("Servidor", "Sala fechada pelo servidor.");
            this.setToClose = true;
        }catch(Exception e){
            System.out.println("Erro durante o fechamento da sala.");
        }
    }
    public String getRoomName() {
        return roomName;
    }
}