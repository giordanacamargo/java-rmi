import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class RoomChat extends java.rmi.server.UnicastRemoteObject implements IRoomChat {

    private ArrayList<String> Users;
    private String roomName;

    public RoomChat(String roomName) throws RemoteException {
        this.roomName = roomName;
        this.Users = new ArrayList<>();
    }

    public void sendMsg (String usrName, String msg) throws Exception {
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        for (String userName: Users) {
            IUserChat user = (IUserChat) registry.lookup(userName);
            user.deliverMsg(usrName, msg);
        }
    }
    public void joinRoom(String usrName, IUserChat user) {
        this.Users.add(usrName);
    }
    public void leaveRoom(String usrName) {
        this.Users.remove(usrName);
    }
    public void closeRoom() {
        return;
    }
    public String getRoomName() {
        return roomName;
    }
    public void setRoomName (String roomName) {
        this.roomName = roomName;
    }
}
