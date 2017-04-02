
import static spark.Spark.init;
import static spark.Spark.staticFileLocation;
import static spark.Spark.webSocket;


public class ChatSystem {

    private CanalList list;

    public ChatSystem()
    {
        list = new CanalList();
    }

    public CanalList getCanalList()
    {
        return list;
    }

    public static void main(String[] args) {

        webSocket("/public", ChatWebSocketHandler.class);
        staticFileLocation("/public/"); //index.html is served at localhost:4567 (default port)
        init();

    }
}
