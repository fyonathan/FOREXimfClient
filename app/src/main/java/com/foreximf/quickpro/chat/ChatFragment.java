package com.foreximf.quickpro.chat;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.foreximf.quickpro.R;
import com.foreximf.quickpro.chat.model.ChatDepartment;
import com.foreximf.quickpro.chat.model.ChatDepartmentDao;
import com.foreximf.quickpro.chat.model.ChatDialog;
import com.foreximf.quickpro.database.ForexImfAppDatabase;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    DialogsList dialogsList;
    DialogsListAdapter dialogsListAdapter;
    ImageLoader imageLoader;
    private ChatDialogViewModel chatDialogViewModel;
    private Observer<List<ChatDepartment>> chatDepartmentObserver;

    public ChatFragment() {

    }

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Intent chatRoomIntent = new Intent(getActivity(), ChatRoomActivity.class);
//        startActivity(chatRoomIntent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        dialogsList = view.findViewById(R.id.dialogs_list);
        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                Picasso.with(getActivity()).load(url).into(imageView);
            }
        };
        dialogsListAdapter = new DialogsListAdapter(imageLoader);
        dialogsList.setAdapter(dialogsListAdapter);

        chatDialogViewModel = ViewModelProviders.of(this).get(ChatDialogViewModel.class);

        chatDepartmentObserver = new Observer<List<ChatDepartment>>() {
            @Override
            public void onChanged(@Nullable List<ChatDepartment> chatDepartments) {
                dialogsListAdapter.setItems(chatDepartments);
            }
        };

        chatDialogViewModel.getAll().observe(this, chatDepartmentObserver);

//        ChatDepartmentDao chatDepartmentDao = ForexImfAppDatabase.getDatabase(getActivity()).chatDepartmentDao();
//        dialogsListAdapter.setItems(chatDepartmentDao.getAll().getValue());
        return view;
    }
}
