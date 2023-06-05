import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class UserChat implements IUserChat
{    
    private RoomChat roomChat = null;
    private String usrName;
    private String msg;

    public UserChat(String usrName) {
        this.usrName = usrName;
    }

    public void deliverMsg(String senderName, String msg) {

    }

    public static void main (String args[]) throws Exception{
        String userName = JOptionPane.showInputDialog(null, "Digite seu nome de usuário");
        new UserChat(userName);
        //LocateRegistry registry//localhost/Servidor
        Registry registry = LocateRegistry.getRegistry("localhost", 1099);
        IServerChat server = (IServerChat) registry.lookup("Servidor");
        System.out.println(server.getRooms());
    }

    public String getUsrName() {
        return usrName;
    }
    public void setUsrName(String usrName) {
        this.usrName = usrName;
    }

}
