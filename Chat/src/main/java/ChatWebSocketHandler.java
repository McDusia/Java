import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import java.io.IOException;
import java.net.HttpCookie;


//SERVER SIDE
@WebSocket
public class ChatWebSocketHandler {
    private String sender, msg;
    private ChatSystem chat;

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {

        if(chat==null)
            chat = new ChatSystem();
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        //delete user from appropriate canal or from "UsersBeyondCanal" list
        String canalNumbers = chat.getCanalList().composeCanalsNumbers();
        Canal c = chat.getCanalList().findCanalForUser(user);
        if(c!=null ) {
            String s = String.valueOf(0);
            String sender = c.getuserUsernameMap().get(user);
            chat.getCanalList().leaveCanal(user);
            if(!chat.getCanalList().isEmpty())
            c.msgLeaveCanal(canalNumbers, sender, s+"User " + sender + " end session.");
        }
        chat.getCanalList().leaveCanal(user);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        switch (message.codePointAt(0)){
            case 1:
                //server receive name from new client
                message = message.substring(1, message.length());
                HttpCookie cookie = new HttpCookie("userName", message);
                user.getUpgradeRequest().getCookies().add(cookie);
                chat.getCanalList().addUserBeyondCanal(user, message);
                //sending canal list to new client
                chat.getCanalList().sendCanalNumbers();
                break;
            case 2:
                //user wants to create new canal
                chat.getCanalList().createNewCanal();
                chat.getCanalList().sendCanalNumbers();
                break;
            case 3:
                //user chose canal
                Integer nr = (Integer.valueOf (message.substring(1, message.length())));
                chat.getCanalList().addUserToCanal(user,nr);
                break;
            case 4:
                //user leave canal
                chat.getCanalList().leaveCanal(user);
                chat.getCanalList().sendCanalNumbers();
                break;
            case 5:
                //user send a message on canal
                String canalNumbers = chat.getCanalList().composeCanalsNumbers();
                Canal c = chat.getCanalList().findCanalForUser(user);
                if(c!=null)
                    c.broadcastMessage(canalNumbers,sender = c.getuserUsernameMap().get(user), msg = message);
                break;
            case 6:
                //user chose chatbot
                String canalNumbers2 = chat.getCanalList().composeCanalsNumbers();
                String question = message.substring(1, message.length());
                String response ="";
                Chatbot bot = new Chatbot();
                switch (question)
                {
                    case "time":
                        response = bot.getTime();
                        break;
                    case "weekday":
                        response = bot.getDay();
                        break;
                    case "weather":
                        response = bot.getWeatherInfo();
                        break;
                }
                Json j = new Json();
                String JsonToSend = j.newJsonString(canalNumbers2,question,response);

                try {
                    user.getRemote().sendString(JsonToSend);
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;
        }
    }
}
