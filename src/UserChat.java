import javax.swing.*;
import java.awt.*;

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

    public static void main (String args[]) {
        String userName = JOptionPane.showInputDialog(null, "Digite seu nome de usuário");
        new UserChat(userName);
    }

    public String getUsrName() {
        return usrName;
    }
    public void setUsrName(String usrName) {
        this.usrName = usrName;
    }

}
