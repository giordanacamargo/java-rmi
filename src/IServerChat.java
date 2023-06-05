import java.util.ArrayList;
import java.rmi.RemoteException;

public interface IServerChat extends java.rmi.Remote 
{
    public ArrayList<String> getRooms() throws RemoteException;
    public void createRoom(String roomName) throws RemoteException;
}
