import com.google.gson.Gson;

public class ApiHelper {
    public static String toJson(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }
}
