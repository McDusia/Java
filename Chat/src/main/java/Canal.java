import org.eclipse.jetty.websocket.api.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Canal {

    private int number;
    private Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();

    public Canal(int n)
    {
        number = n;
    }

    public boolean isUserInCanal(Session s)
    {
      return userUsernameMap.containsKey(s);
    }
    public boolean isEmpty(){
        return userUsernameMap.isEmpty();
    }

    public void removeUserFromCanal(Session s)
    {
        userUsernameMap.remove(s);
    }
    public Map<Session,String> getuserUsernameMap()
    {
        return userUsernameMap;
    }
    public void addUserToCanal(Session s,String username)
    {
        userUsernameMap.put(s,username);
    }

    public void broadcastMessage(String canalNumbers, String sender, String message) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                Json j = new Json();
                session.getRemote().sendString(j.newJsonString(canalNumbers,sender,message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    public void msgLeaveCanal(String canalNumbers, String sender,String message) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                Json j = new Json();
                session.getRemote().sendString(j.newJsonString(canalNumbers,sender,message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
