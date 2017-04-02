import org.eclipse.jetty.websocket.api.Session;
import java.net.HttpCookie;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public class CanalList {

    private Map<Session, String> usersBeyondCanal = new ConcurrentHashMap<>();
    private Map<Integer, Canal> canalList = new ConcurrentHashMap<>();
    private int maxCanal;


    public CanalList()
    {
        maxCanal=0;
    }

    public boolean isEmpty()
    {
        for (Map.Entry<Integer, Canal> entry : canalList.entrySet()) {
            Canal c = entry.getValue();
            if(!c.isEmpty())
                return false;
        }
        return true;
    }

    public Canal findCanalForUser(Session s)
    {
        for (Map.Entry<Integer, Canal> entry : canalList.entrySet()) {
            Canal c = entry.getValue();
            if(c.isUserInCanal(s))
                return c;
        }
        return null;
    }

    public void leaveCanal(Session s)
    {
        Canal c = findCanalForUser(s);
        if(c==null)
            usersBeyondCanal.remove(s);
        else {
            c.removeUserFromCanal(s);
            usersBeyondCanal.put(s, getUsernameFromSession(s));
        }
    }

    public void addUserToCanal(Session s, int canalNr)
    {
        usersBeyondCanal.remove(s);
        Canal c = canalList.get(canalNr);
        String userName = getUsernameFromSession(s);
        c.addUserToCanal(s,userName);
    }

    private String getUsernameFromSession(Session s)
    {
        int maxi = s.getUpgradeRequest().getCookies().size();
        String userName="";
        boolean b = true;
        for(int i=0;i<maxi && b;i++) {
            HttpCookie username = s.getUpgradeRequest().getCookies().get(i);
            if(username.getName()=="userName")
            {userName = username.getValue(); b = false;}
        }
        return userName;
    }

    public String composeCanalsNumbers()
    {
        String result="";

        for (Map.Entry<Integer, Canal> entry : canalList.entrySet()) {
            String n = entry.getKey().toString();
            if (result.length() > 0)
                result += ",";
            result +=n;
        }
        return result;
    }

    private int getFirstFreeNumber ()
    {
        return ++maxCanal;
    }

    public int createNewCanal()
    {
        int canalNumber = getFirstFreeNumber();
        Canal C = new Canal(canalNumber);
        canalList.put(canalNumber,C);
        return canalNumber;
    }

    public void addUserBeyondCanal(Session s, String username)
    {
        usersBeyondCanal.put(s,username);
    }


    public void sendCanalNumbers() {
        try {
            String JsonToSend = new Json().newJsonString(composeCanalsNumbers(),"","");
            usersBeyondCanal.forEach((u,v) ->
                    {
                        try {
                            u.getRemote().sendString(JsonToSend);
                        } catch(Exception e){
                            e.printStackTrace();}
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
