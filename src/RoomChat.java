import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class RoomChat extends java.rmi.server.UnicastRemoteObject implements IRoomChat {

    private ArrayList<String> Users;
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
    }
    public void closeRoom() {
        return;
    }
    public String getRoomName() {
        return roomName;
    }
}
