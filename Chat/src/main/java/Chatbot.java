import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import static java.lang.StrictMath.round;

public class Chatbot {


    public String getTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String time=timeFormat.format(calendar.getTime());
        return time;
    }

    public String getDay(){
        Calendar calendar = Calendar.getInstance();
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
        String day="Today is ";
        switch(day_of_week){
            case 1:
                day+="Sunday.";
                break;
            case 2:
                day+="Monday.";
                break;
            case 3:
                day+="Tuesday.";
                break;
            case 4:
                day+="Wednesday.";
                break;
            case 5:
                day+="Thursday.";
                break;
            case 6:
                day+="Friday.";
                break;
            case 7:
                day+="Saturday.";
                break;
            default:
                break;
        }
        return day;
    }

    public String getWeatherInfo() {

        String info1,info2,info3;
        try {

            String url = "http://api.openweathermap.org/data/2.5/weather?q=Cracow&APPID=fe0ab36b6bc2629a60f4addd5988bed7";
            String readedJson = read(url);
            JSONObject j = new JSONObject(readedJson);
            String weather =j.getJSONArray("weather").getJSONObject(0).getString("description");
            info1=" Cracow: "+weather+", ";
            double temp =j.getJSONObject("main").getDouble("temp");
            temp=temp-273;
            temp *=100;
            temp = round(temp);
            temp/=100;
            weather=String.valueOf(temp);
            info2="Temp. "+weather+" st. C, ";
            weather= String.valueOf(j.getJSONObject("main").getDouble("pressure"))+"";
            info3="Pressure: "+weather+" hPa";
            String res = info1 + info2 +info3;
            return res;

        } catch(JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String read(String urlString) throws Exception {
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        String inputLine;
        StringBuilder builder = new StringBuilder();
        while ((inputLine = in.readLine()) != null){
            builder.append(inputLine);
        }
        in.close();
        return String.valueOf(builder);
    }
}
