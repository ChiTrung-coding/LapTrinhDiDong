package com.example.vietschool.chatAI;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import com.example.vietschool.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainChat extends AppCompatActivity {
    RecyclerView recyc_view;
    TextView wellcome_taget;
    EditText message_edit_text;
    ImageButton send_buton;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    public static final MediaType JSON = MediaType.get("application/json");
    OkHttpClient client = new OkHttpClient();
    private static final long REQUEST_INTERVAL_MS = 2000; // 2 seconds
    private static final int MAX_RETRY_ATTEMPTS = 3;

    private long lastRequestTime = 0;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_chat2);
        recyc_view = findViewById(R.id.recyc_view);
        wellcome_taget = findViewById(R.id.wellcome_taget);
        message_edit_text = findViewById(R.id.message_edit_text);
        send_buton = findViewById(R.id.send_buton);
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyc_view.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyc_view.setLayoutManager(llm);
        send_buton.setOnClickListener((v) -> {
            String question = message_edit_text.getText().toString().trim();
            addToChat(question, Message.SENT_BY_ME);
            message_edit_text.setText("");
            if (System.currentTimeMillis() - lastRequestTime >= REQUEST_INTERVAL_MS) {
                APIRequest requet = new APIRequest();
                requet.callAPI(question,0);
                lastRequestTime = System.currentTimeMillis();
            } else {
                Toast.makeText(MainChat.this, "Please wait before sending another message.", Toast.LENGTH_SHORT).show();
            }
            wellcome_taget.setVisibility(View.GONE);
        });
    }
    //gemini = AIzaSyAOpdvXzeD9ogvDnpUrFpI-rl8zbaUiusI
    void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyc_view.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }
    public class APIRequest {
        private static final int MAX_RETRY_ATTEMPTS = 3;
        private final GenerativeModelFutures model;
        private final Executor executor = MoreExecutors.directExecutor();
        private static final String API_KEY = "AIzaSyAOpdvXzeD9ogvDnpUrFpI-rl8zbaUiusI";
        public APIRequest() {
            GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", API_KEY);
            model = GenerativeModelFutures.from(gm);
        }
        void addResponse(String response) {
            addToChat(response, Message.SENT_BT_BOT);
        }
        void callAPI(String question, int retryAttempt) {
            Content.Builder userContentBuilder = new Content.Builder();
            userContentBuilder.setRole("user");
            userContentBuilder.addText("Hello, Our group is group 9.");
            Content userContent = userContentBuilder.build();
            Content.Builder modelContentBuilder = new Content.Builder();
            modelContentBuilder.setRole("model");
            modelContentBuilder.addText("Great to meet you. What would you like to know?");
            Content modelContent = modelContentBuilder.build();
            List<Content> history = Arrays.asList(userContent, modelContent);
            ChatFutures chat = model.startChat(history);
            Content.Builder userMessageBuilder = new Content.Builder();
            userMessageBuilder.setRole("user");
            userMessageBuilder.addText(question);
            Content userMessage = userMessageBuilder.build();
            ListenableFuture<GenerateContentResponse> response = chat.sendMessage(userMessage);
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String resultText = result.getText();
                    addResponse(resultText.trim());
                }
                @Override
                public void onFailure(@Nonnull Throwable t) {
                    if (retryAttempt < MAX_RETRY_ATTEMPTS) {
                        handler.postDelayed(() -> callAPI(question, retryAttempt + 1), (retryAttempt + 1) * 2000);
                    } else {
                        addResponse("Lỗi dữ liệu: " + t.getMessage());
                    }
                }
            }, executor);
        }
    }

}
