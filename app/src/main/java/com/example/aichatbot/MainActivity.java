package com.example.aichatbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText type;
    ImageButton send;
    List<Message> messages;
    MessageAdapter messageAdapter;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messages = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        type = findViewById(R.id.tb_type);
        send = findViewById(R.id.btn_send);
        messageAdapter = new MessageAdapter(messages);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = type.getText().toString().trim();
                AddToChat(question, Message.sent_by_user);
                type.setText("");
                CallAPI(question);
            }
        });
    }

    void AddToChat(String message, String send_by) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messages.add(new Message(message, send_by));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    void AddRespond(String respond) {
        messages.remove(messages.size() - 1);
        AddToChat(respond, Message.sent_by_ai);
    }

    void CallAPI(String question) {
        messages.add(new Message("Typing...", Message.sent_by_ai));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", "gpt-3.5-turbo");
            JSONArray messagesArray = new JSONArray();
            JSONObject messageObject = new JSONObject();
            messageObject.put("role", "user");
            messageObject.put("content", question);
            messagesArray.put(messageObject);
            jsonObject.put("messages", messagesArray);
            jsonObject.put("temperature", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer YOUR_OPENAI_API_KEY")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                AddRespond("Failed to respond due to " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        AddRespond("API request failed with code: " + response.code());
                        return;
                    }
                    try (ResponseBody responseBody = response.body()) {
                        if (responseBody != null) {
                            String responseString = responseBody.string();
                            JSONObject jsonResponse = new JSONObject(responseString);
                            JSONArray choicesArray = jsonResponse.getJSONArray("choices");
                            String result = choicesArray.getJSONObject(0).getJSONObject("message").getString("content");
                            AddRespond(result.trim());
                        } else {
                            AddRespond("Empty response body from API");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } finally {
                    response.close();
                }
            }
        });
    }
}