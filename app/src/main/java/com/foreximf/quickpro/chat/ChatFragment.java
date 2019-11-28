package com.foreximf.quickpro.chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.foreximf.quickpro.R;
import com.foreximf.quickpro.chat.model.ChatDepartment;
import com.foreximf.quickpro.chat.model.ChatThread;
import com.foreximf.quickpro.chat.model.ChatThreadComplete;
import com.foreximf.quickpro.database.ForexImfAppDatabase;
import com.foreximf.quickpro.services.WebSocketChat;
import com.foreximf.quickpro.util.F;
import com.foreximf.quickpro.util.ImageUtils;
import com.skydoves.powermenu.CustomPowerMenu;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatFragment extends Fragment {

    DialogsList dialogsList;
    DialogsListAdapter dialogsListAdapter;
    FloatingActionButton floatingActionButton;
    ImageLoader imageLoader;
    private ChatDialogViewModel chatDialogViewModel;
    private Observer<List<ChatThreadComplete>> chatThreadObserver;

    public ChatFragment() {

    }

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        chatDialogViewModel.refresh();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().startService(new Intent(getActivity(), WebSocketChat.class));
//        Intent chatRoomIntent = new Intent(getActivity(), ChatRoomActivity.class);
//        startActivity(chatRoomIntent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        dialogsList = view.findViewById(R.id.dialogs_list);
        imageLoader = (imageView, url, payload) -> ImageUtils.loadImagePicasso(getActivity(), url, imageView);
        dialogsListAdapter = new DialogsListAdapter(imageLoader);
        dialogsList.setAdapter(dialogsListAdapter);

        dialogsListAdapter.setOnDialogClickListener(dialog -> {
            Intent chatRoomIntent = new Intent(getActivity(), ChatRoomActivity.class);
            chatRoomIntent.putExtra("ChatThread", dialog.getId());
            startActivity(chatRoomIntent);
        });

        floatingActionButton = view.findViewById(R.id.new_chat);
        floatingActionButton.setOnClickListener(view1 -> {
            CustomPowerMenu customPowerMenu = new CustomPowerMenu.Builder(getActivity(), new ChatDepartmentAdapter())
                    .setAnimation(MenuAnimation.SHOW_UP_CENTER)
                    .setMenuRadius(10f)
                    .setMenuShadow(10f)
                    .setWidth(800)
                    .setDivider(new ColorDrawable(getResources().getColor(R.color.colorLightGrey)))
                    .setDividerHeight(1)
                    .setLifecycleOwner(ChatFragment.this)
                    .build();
            customPowerMenu.setOnMenuItemClickListener((OnMenuItemClickListener<ChatDepartment>) (position, item) -> {
                customPowerMenu.dismiss();
                new ChatThreadAsyncTask(getActivity()).execute(item.getId());
            });
            new PowerMenuAsyncTask(customPowerMenu, getActivity()).execute();
        });
        chatDialogViewModel = ViewModelProviders.of(this).get(ChatDialogViewModel.class);
        chatThreadObserver = chatThreads -> dialogsListAdapter.setItems(chatThreads);
        chatDialogViewModel.getAll().observe(this, chatThreadObserver);
        return view;
    }

    private class PowerMenuAsyncTask extends AsyncTask<Void, Void, Void> {

        private CustomPowerMenu customPowerMenu;
        private Context context;

        public PowerMenuAsyncTask(CustomPowerMenu customPowerMenu, Context context) {
            this.customPowerMenu = customPowerMenu;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... v) {
            List<ChatDepartment> departmentList = ForexImfAppDatabase.getDatabase(context).chatDao().getDepartments();
            for (int i = 0; i < departmentList.size(); i++) {
                customPowerMenu.addItem(departmentList.get(i));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            customPowerMenu.showAtCenter(dialogsList);
        }
    }

    private class ChatThreadAsyncTask extends AsyncTask<String, Void, ChatThread> {
        private ProgressDialog dialog;
        private ChatDao chatDao;
        private String department;

        public ChatThreadAsyncTask(Activity activity) {
            dialog = new ProgressDialog(activity);
            chatDao = ForexImfAppDatabase.getDatabase(activity).chatDao();
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected ChatThread doInBackground(String... args) {
            department = args[0];
            ChatThread chatThread = chatDao.findActiveThreadByDepartment(department);
            if (chatThread != null) {
                return chatThread;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ChatThread result) {
            if (result != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Intent chatRoomIntent = new Intent(getActivity(), ChatRoomActivity.class);
                chatRoomIntent.putExtra("ChatThread", result.getId());
                startActivity(chatRoomIntent);
            } else {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String url = "https://client.foreximf.com/api-chat";
                Map<String, String> params = new HashMap<>();
                params.put("token", preferences.getString("login-token", ""));
                params.put("mode", "department");
                params.put("department", department);
                F.JSONRequest(getActivity(), url, params, new F.JSONRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            boolean error = response.getBoolean("error");
                            if (error) {
                                Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_LONG).show();
                            } else {
                                JSONObject chat_thread = response.getJSONObject("chat_thread");
                                String id = chat_thread.getString("id");
                                ChatRepository chatRepository = ChatRepository.getInstance(getActivity().getApplicationContext());
                                chatRepository.add(new ChatThread(chat_thread), () -> {
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                    Intent chatRoomIntent = new Intent(getActivity(), ChatRoomActivity.class);
                                    chatRoomIntent.putExtra("ChatThread", id);
                                    startActivity(chatRoomIntent);
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(getActivity(), "Muncul error saat mencoba sign in, silakan coba beberapa saat lagi", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
}