import java.rmi.RemoteException;

public class RoomChat extends java.rmi.server.UnicastRemoteObject implements IRoomChat {

    private String roomName;

    public RoomChat(String roomName) throws RemoteException {
        this.roomName = roomName;
    }

    public void sendMsg(String usrName, String msg) {
        return;
    }
    public void joinRoom(String usrName, IUserChat user) {
        return;
    }
    public void leaveRoom(String usrName) {
        return;
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
