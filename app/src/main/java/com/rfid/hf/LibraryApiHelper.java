package com.rfid.hf;

import android.util.Log;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;

public class LibraryApiHelper {

    // Interface custom callback
    public interface BookCallback {
        void onSuccess(String title, boolean rentable);
        void onFailure(String message);
    }

    // Fungsi utama untuk ambil info buku dari UID
    public static void getBookByUID(String privateKey, String uid, BookCallback callback) {
        String url = "https://openlibrary.telkomuniversity.ac.id/olafa/index.php/rfidscanner2/book";

        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("private_key", privateKey)
                .add("barcode", uid)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("Server error");
                    return;
                }

                String body = response.body().string();
                Log.d("API_RESPONSE", body);
                try {
                    JSONObject json = new JSONObject(body);
                    if (json.optString("status").equals("success")) {
                        JSONObject data = json.optJSONObject("data");
                        String title = data.optString("title");
                        boolean rentable = data.optString("rentable").equals("true");
                        callback.onSuccess(title, rentable);
                    } else {
                        callback.onFailure("Data not found");
                    }
                } catch (JSONException e) {
                    callback.onFailure("Invalid response");
                }
            }
        });
    }
}
