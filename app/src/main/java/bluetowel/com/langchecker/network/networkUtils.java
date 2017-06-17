package bluetowel.com.langchecker.network;

import android.content.SharedPreferences;
import android.provider.Settings;

import java.io.IOException;
import java.net.URL;

import bluetowel.com.langchecker.MainActivity;
import bluetowel.com.langchecker.utils.BasicCallback;
import bluetowel.com.langchecker.utils.UniversalVariables;
import bluetowel.com.langchecker.utils.Utilities;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Pawan on 5/14/2017.
 */

public class networkUtils {


    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");
    private static final String HTTP_HEADER_JSON = "application/json";
//    private static final String baseURL = "https://languagetool.org/api/v2/check";
//private static final String LanguagesURL = "https://languagetool.org/api/v2/languages";

//    private static final String baseLocalURL = "http://103.216.93.193:8081/v2/check";
    private static String textCheckURL= "/v2/check";
    private static String defaultPort = "8081";
    private static String HTTP = "http://";
    private static  String baseLocalURL = "http://192.168.0.150:8081/v2/check";
    private static final String baseLocalLanguagesURL = "http://192.168.0.150:8081/v2/languages";
    private static final String HTTP_HEADER_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final OkHttpClient client = new OkHttpClient();
    private static String enabledTAG = "&enabledOnly=false";



    private static String reformatText(String text) {
        return text.replace(" ", "%20");
    }

    public static void GETSupportedLanguages() {
        Request request1 = new Request.Builder()
                .url(baseLocalLanguagesURL)
                .get()
                .build();

    }

    public static void POSTcall(String text, String langCode, final BasicCallback callback) {
        try {
            text = reformatText(text);
            langCode = "en-US";
            String postBody = "text=" + text + "&language=" + langCode + enabledTAG;

            RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, postBody);


            String url;
            SharedPreferences settings = MainActivity.context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
            String server = settings.getString(UniversalVariables.server, UniversalVariables.notSet);

            if (!server.equalsIgnoreCase(UniversalVariables.notSet)) {
                String port = settings.getString(UniversalVariables.portNumber, UniversalVariables.notSet);
                if (port.equalsIgnoreCase(UniversalVariables.notSet)) {
                    url =HTTP+ server + ":" + defaultPort + textCheckURL;
                } else {
                    url = HTTP+ server + ":" + port + textCheckURL;
                }
            } else {
                url = baseLocalURL;
            }


            Request request = new Request.Builder()
                    .url(url)
                    .header("Host", "localhost:8081")
                    .addHeader("Content-Type", HTTP_HEADER_FORM_URLENCODED)
                    .addHeader("Referer", "Android-Device")
                    .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .addHeader("Accept-Language", "en-US,en;q=0.5")
                    .addHeader("Accept-Encoding", "gzip, deflate, br")
                    .addHeader("Connection", "keep-alive")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    callback.callBack(Utilities.CallbackResultCode.FAIL, null);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    try {
                        if (response.isSuccessful()) {
                            callback.callBack(Utilities.CallbackResultCode.SUCCESS, response.body().string());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    //TODO correct this ping setup
    public static boolean checkNetworkConnectivity(){

        final boolean flag=true;
        Request request = new Request.Builder()
                .url(baseLocalLanguagesURL)
                .build();


        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
//                flag = false;

            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {

//                flag = true;

            }
        });
        return flag;
    }
}
