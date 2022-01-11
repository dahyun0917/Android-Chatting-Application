package com.example.chat_de;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.chat_de.databinding.ItemElementCenterSystemBinding;
import com.example.chat_de.databinding.ItemElementLeftImageBinding;
import com.example.chat_de.databinding.ItemElementLeftTextBinding;
import com.example.chat_de.databinding.ItemElementRightImageBinding;
import com.example.chat_de.databinding.ItemElementRightTextBinding;
import com.example.chat_de.databinding.ItemRecyclerUserListBinding;
import com.example.chat_de.datas.Chat;
import com.example.chat_de.datas.ChatRoomUser;
import com.example.chat_de.datas.ViewType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class RoomElementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Chat> myDataList;
    private enum MessageType { TEXT, IMAGE };
    private MessageType messageType;
    private HashMap<String, ChatRoomUser> myUserList;
    private ChatRoomUser UserList;

    public RoomElementAdapter(ArrayList<Chat> dataList,HashMap<String, ChatRoomUser> userList){
        myDataList = dataList;
        myUserList = userList;
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        switch(viewType) {
            case ViewType.CENTER_CONTENT:
                view = inflater.inflate(R.layout.item_element_center_system,parent,false);
                return new CenterViewHolder(view);
            case ViewType.LEFT_CONTENT:
                if(messageType == MessageType.TEXT)
                    view = inflater.inflate(R.layout.item_element_left_text,parent,false);
                else
                    view = inflater.inflate(R.layout.item_element_left_image,parent,false);
                return new LeftViewHolder(view);
            case ViewType.RIGHT_CONTENT:
                if(messageType == MessageType.TEXT)
                    view = inflater.inflate(R.layout.item_element_right_text,parent,false);
                else
                    view = inflater.inflate(R.layout.item_element_right_image,parent,false);
                return new RightViewHolder(view);
            default:
                Log.e("VIEW_TYPE", "ViewType must be 1 or 2 or 3");
                view = inflater.inflate(R.layout.item_element_right_text,parent,false);
                return new RightViewHolder(view);
        }
    }
    // 실제 각 뷰 홀더에 데이터를 연결해주는 함수
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        //DateFormat df = new SimpleDateFormat("a HH:mm");
        //String str=df.format(myDataList.get(position).normalDate());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("a hh:mm", Locale.KOREA);
        String str= simpleDateFormat.format(myDataList.get(position).normalDate());


        if(viewHolder instanceof CenterViewHolder){
            ((CenterViewHolder)viewHolder).centerSystemBinding.textv.setText(myDataList.get(position).getText());
        }else if(viewHolder instanceof LeftViewHolder){
            if(messageType.equals(MessageType.TEXT)) {
                ((LeftViewHolder) viewHolder).leftTextBinding.textvMsg.setText(myDataList.get(position).getText());
                ((LeftViewHolder) viewHolder).leftTextBinding.textvNicname.setText(UserList.getUserMeta().getName());
                ((LeftViewHolder) viewHolder).leftTextBinding.textvTime.setText(str);
                Glide.with(viewHolder.itemView.getContext()).load(UserList.getUserMeta().getPictureURL()).into(((LeftViewHolder)viewHolder).leftTextBinding.imgv);
            }
            else{
                Glide.with(viewHolder.itemView.getContext()).load(myDataList.get(position).getText()).into(((LeftViewHolder)viewHolder).leftImageBinding.imagevMsg);
                ((LeftViewHolder)viewHolder).leftImageBinding.textvNicname.setText(UserList.getUserMeta().getName());
                ((LeftViewHolder)viewHolder).leftImageBinding.textvTime.setText(str);
                Glide.with(viewHolder.itemView.getContext()).load(UserList.getUserMeta().getPictureURL()).into(((LeftViewHolder)viewHolder).leftImageBinding.imgv);
            }
        }else{
            if(messageType.equals(MessageType.TEXT)){
                ((RightViewHolder)viewHolder).rightTextBinding.textvMsg.setText(myDataList.get(position).getText());
                ((RightViewHolder)viewHolder).rightTextBinding.textvNicname.setText(UserList.getUserMeta().getName());
                ((RightViewHolder)viewHolder).rightTextBinding.textvTime.setText(str);
            }
            else{
                Glide.with(viewHolder.itemView.getContext()).load(myDataList.get(position).getText()).into(((RightViewHolder)viewHolder).rightImageBinding.imagevMsg);
                ((RightViewHolder)viewHolder).rightImageBinding.textvNicname.setText(UserList.getUserMeta().getName());
                ((RightViewHolder)viewHolder).rightImageBinding.textvTime.setText(str);
            }
        }
    }
    // 리사이클러뷰안에서 들어갈 뷰 홀더의 개수
    @Override
    public int getItemCount() {
        return myDataList.size();
    }
    // ★★★
    // 위에 3개만 오버라이드가 기본 셋팅임,
    // 이 메소드는 ViewType때문에 오버라이딩 했음(구별할려고)
    @Override
    public int getItemViewType(int position) {
        UserList = new ChatRoomUser();
        for(String i : myUserList.keySet()){
            if(myDataList.get(position).getFrom().equals(myUserList.get(i).getUserMeta().getUserKey())){
                UserList=myUserList.get(i);
            }
        }
        if(myDataList.get(position).getType().equals(Chat.Type.TEXT))
            messageType = MessageType.TEXT;
        else if(myDataList.get(position).getType().equals(Chat.Type.IMAGE))
            messageType = MessageType.IMAGE;

        if(myDataList.get(position).getType().equals(Chat.Type.SYSTEM))
            return ViewType.CENTER_CONTENT;
        else if((myDataList.get(position).getFrom().equals("user2")))
            return ViewType.RIGHT_CONTENT;
        else
            return ViewType.LEFT_CONTENT;

    }

    // "리사이클러뷰에 들어갈 뷰 홀더", 그리고 "그 뷰 홀더에 들어갈 아이템들을 셋팅"
    public class CenterViewHolder extends RecyclerView.ViewHolder{
        ItemElementCenterSystemBinding centerSystemBinding;

        public CenterViewHolder(@NonNull View itemView) {
            super(itemView);
            centerSystemBinding = ItemElementCenterSystemBinding.bind(itemView);
        }
    }

    public class LeftViewHolder extends RecyclerView.ViewHolder{
        //CircleImageView imgv;
        ItemElementLeftImageBinding leftImageBinding;
        ItemElementLeftTextBinding leftTextBinding;

        public LeftViewHolder(@NonNull View itemView) {
            super(itemView);
            if(messageType == MessageType.TEXT) {
                leftTextBinding = ItemElementLeftTextBinding.bind(itemView);
            }
            else if(messageType == MessageType.IMAGE){
                leftImageBinding = ItemElementLeftImageBinding.bind(itemView);
            }
        }
    }

    public class RightViewHolder extends RecyclerView.ViewHolder{
        ItemElementRightImageBinding rightImageBinding;
        ItemElementRightTextBinding rightTextBinding;
        public RightViewHolder(@NonNull View itemView) {
            super(itemView);
            if(messageType.equals(MessageType.TEXT)) {
                rightTextBinding = ItemElementRightTextBinding.bind(itemView);
            }
            else if(messageType.equals(MessageType.IMAGE)){
                rightImageBinding = ItemElementRightImageBinding.bind(itemView);
            }
        }
    }

}