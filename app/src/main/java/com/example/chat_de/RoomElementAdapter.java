package com.example.chat_de;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.chat_de.databinding.ItemElementCenterSystemBinding;
import com.example.chat_de.databinding.ItemElementLeftImageBinding;
import com.example.chat_de.databinding.ItemElementLeftTextBinding;
import com.example.chat_de.databinding.ItemElementLoadingBinding;
import com.example.chat_de.databinding.ItemElementRightImageBinding;
import com.example.chat_de.databinding.ItemElementRightTextBinding;
import com.example.chat_de.datas.Chat;
import com.example.chat_de.datas.ChatRoomUser;
import com.example.chat_de.datas.IndexDeque;
import com.example.chat_de.datas.ViewType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class RoomElementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private IndexDeque<Chat> myDataList;
    private HashMap<String, ChatRoomUser> myUserList;
    private ChatRoomUser chatRoomUser;
    private int currentReadStates;
    private String passDate;
    private Chat passChat ;  //TODO : 수정해야함


    public RoomElementAdapter(IndexDeque<Chat> dataList,HashMap<String, ChatRoomUser> chatRoomUser){
        myUserList = chatRoomUser;
        myDataList = dataList;
    }
    //TODO : fix
    public void setUserList(IndexDeque<Chat> dataList,HashMap<String, ChatRoomUser> userList){
        myUserList = userList;
        myDataList = dataList;
        this.notifyDataSetChanged();
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch(viewType) {
            case ViewType.LOADING:
                view = inflater.inflate(R.layout.item_element_loading,parent,false);
                return new LoadingViewHolder(view);
            case ViewType.CENTER_CONTENT:
                view = inflater.inflate(R.layout.item_element_center_system,parent,false);
                return new CenterViewHolder(view);
            case ViewType.LEFT_CONTENT_TEXT:
                view = inflater.inflate(R.layout.item_element_left_text,parent,false);
                return new LeftTextViewHolder(view);
            case ViewType.LEFT_CONTENT_IMAGE:
                view = inflater.inflate(R.layout.item_element_left_image,parent,false);
                return new LeftImageViewHolder(view);
            case ViewType.RIGHT_CONTENT_TEXT:
                view = inflater.inflate(R.layout.item_element_right_text,parent,false);
                return new RightTextViewHolder(view);
            case ViewType.RIGHT_CONTENT_IMAGE:
                view = inflater.inflate(R.layout.item_element_right_image,parent,false);
                return new RightImageViewHolder(view);
            default:
                Log.e("VIEW_TYPE", "ViewType must be 1 or 2 or 3");
                view = inflater.inflate(R.layout.item_element_right_text,parent,false);
                return new RightTextViewHolder(view);
        }


    }
    // 실제 각 뷰 홀더에 데이터를 연결해주는 함수
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        //각 xml의 데이터 set
        if(viewHolder instanceof LoadingViewHolder){
            showLoadingView((LoadingViewHolder)viewHolder,position);
        }
        else {
            Chat item = myDataList.get(position);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("a hh:mm", Locale.KOREA);
            String str= simpleDateFormat.format(item.normalDate());

        //각 xml의 데이터 set
        if(viewHolder instanceof CenterViewHolder){
            ((CenterViewHolder)viewHolder).centerSystemBinding.textv.setText(item.getText());
        }else if(viewHolder instanceof LoadingViewHolder){
            showLoadingView((LoadingViewHolder)viewHolder,position);
        }
        else if(viewHolder instanceof LeftTextViewHolder){
            ((LeftTextViewHolder) viewHolder).leftTextBinding.textvMsg.setText(item.getText());
            ((LeftTextViewHolder) viewHolder).leftTextBinding.textvNicname.setText(chatRoomUser.getUserMeta().getName());
            ((LeftTextViewHolder) viewHolder).leftTextBinding.textvTime.setText(str);
            if(currentReadStates !=0)
                ((LeftTextViewHolder) viewHolder).leftTextBinding.readnum.setText(String.valueOf(currentReadStates));
            Glide.with(viewHolder.itemView.getContext()).load(chatRoomUser.getUserMeta().getPictureURL()).into(((LeftTextViewHolder)viewHolder).leftTextBinding.imgv);
        }else if(viewHolder instanceof LeftImageViewHolder){
                Glide.with(viewHolder.itemView.getContext()).load(item.getText()).override(200,200).into(((LeftImageViewHolder)viewHolder).leftImageBinding.imagevMsg);
                ((LeftImageViewHolder)viewHolder).leftImageBinding.textvNicname.setText(chatRoomUser.getUserMeta().getName());
                ((LeftImageViewHolder)viewHolder).leftImageBinding.textvTime.setText(str);
                if(currentReadStates !=0)
                    ((LeftImageViewHolder) viewHolder).leftImageBinding.readnum.setText(String.valueOf(currentReadStates));
                Glide.with(viewHolder.itemView.getContext()).load(chatRoomUser.getUserMeta().getPictureURL()).into(((LeftImageViewHolder)viewHolder).leftImageBinding.imgv);
            ((LeftImageViewHolder)viewHolder).leftImageBinding.imagevMsg.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    SimpleDateFormat passDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
                    passDate= passDateFormat.format(item.normalDate());  //TODO : 수정해야함

                    Intent intent = new Intent(viewHolder.itemView.getContext(),ImageFrameActivity.class);
                    intent.putExtra("fromName",chatRoomUser.getUserMeta().getName());
                    intent.putExtra("passDate",passDate);
                    intent.putExtra("imageView",item.getText());
                    view.getContext().startActivity(intent);
                }
            });
        }
        else if(viewHolder instanceof RightTextViewHolder){
            if(item.getType().equals(Chat.Type.TEXT)){
                ((RightTextViewHolder)viewHolder).rightTextBinding.textvMsg.setText(item.getText());
                ((RightTextViewHolder)viewHolder).rightTextBinding.textvNicname.setText(chatRoomUser.getUserMeta().getName());
                ((RightTextViewHolder)viewHolder).rightTextBinding.textvTime.setText(str);
                if(currentReadStates !=0)
                    ((RightTextViewHolder) viewHolder).rightTextBinding.readnum.setText(String.valueOf(currentReadStates));
            }
        }
        else if(viewHolder instanceof RightImageViewHolder){
            if(item.getType().equals(Chat.Type.IMAGE))
                Glide.with(viewHolder.itemView.getContext()).load(item.getText()).into(((RightImageViewHolder)viewHolder).rightImageBinding.imagevMsg);
            else
                Glide.with(viewHolder.itemView.getContext()).load("https://firebasestorage.googleapis.com/v0/b/ftest-2abe4.appspot.com/o/uploads%2F202201140341565500_user2.jpg?alt=media&token=acc6057a-796a-4f97-b809-1920dbcb988f").into(((RightImageViewHolder)viewHolder).rightImageBinding.imagevMsg);
            ((RightImageViewHolder)viewHolder).rightImageBinding.textvNicname.setText(chatRoomUser.getUserMeta().getName());
            ((RightImageViewHolder)viewHolder).rightImageBinding.textvTime.setText(str);
            if(currentReadStates !=0)
                ((RightImageViewHolder) viewHolder).rightImageBinding.readnum.setText(String.valueOf(currentReadStates));
            ((RightImageViewHolder)viewHolder).rightImageBinding.imagevMsg.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    SimpleDateFormat passDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
                    passDate= passDateFormat.format(item.normalDate());  //TODO : 수정해야함

                    if(item.getType().equals(Chat.Type.IMAGE)){
                    Intent intent = new Intent(viewHolder.itemView.getContext(),ImageFrameActivity.class);
                    intent.putExtra("fromName",chatRoomUser.getUserMeta().getName());
                    intent.putExtra("passDate",passDate);
                    intent.putExtra("imageView",item.getText());
                    view.getContext().startActivity(intent);}
                    else if(item.getType().equals(Chat.Type.VIDEO)){
                        Intent intent = new Intent(viewHolder.itemView.getContext(),VideoFrameActivity.class);
                        intent.putExtra("fromName",chatRoomUser.getUserMeta().getName());
                        intent.putExtra("passDate",passDate);
                        intent.putExtra("imageView",item.getText());
                        view.getContext().startActivity(intent);}
                }
            });

            }
        }
    }

    public void showLoadingView(LoadingViewHolder holder, int position) {

    }

    // 리사이클러뷰안에서 들어갈 뷰 홀더의 개수
    @Override
    public int getItemCount() {
        return myDataList.size();
    }

    // 위에 3개만 오버라이드가 기본 셋팅임,
    // 이 메소드는 ViewType때문에 오버라이딩 했음(구별할려고)
    @Override
    public int getItemViewType(int position) {

        Chat item = myDataList.get(position);


        currentReadStates = 0;    //하나의 메세지를 나타낼 때마다 초기화

        if (item == null)
            return ViewType.LOADING;

        //hashmap에서 데이터 뽑아내기 : 같은 userkey일 때 value값 userList에 저장
        for (String i : myUserList.keySet()) {
            //indexDifferent++;  //참가자 총 명수
            //Log.d("indexDifferent", String.valueOf(indexDifferent));

            if (item.getFrom().equals(myUserList.get(i).getUserMeta().getUserKey())) {
                chatRoomUser = myUserList.get(i);
            }
            if (item.getType().equals(Chat.Type.TEXT))
                if (item.getIndex() > myUserList.get(i).getLastReadIndex())
                    currentReadStates++;
        }
        //Log.d("indexDifferent_finish", String.valueOf(currentReadStates));




        //채팅 위치 타입 (왼, 중간, 오) 과 메세지 타입(이미지, 텍스트) 정하기
        if (item.getType().equals(Chat.Type.SYSTEM)) {
            return ViewType.CENTER_CONTENT;
        } else if ((item.getFrom().equals("user2"))) { //사용자 key로 대체
            if ((item.getType().equals(Chat.Type.TEXT)))
                return ViewType.RIGHT_CONTENT_TEXT;
            else if ((item.getType().equals(Chat.Type.IMAGE)))
                return ViewType.RIGHT_CONTENT_IMAGE;
        } else {
            if ((item.getType().equals(Chat.Type.TEXT)))
                return ViewType.LEFT_CONTENT_TEXT;
            else if ((item.getType().equals(Chat.Type.IMAGE)))
                return ViewType.LEFT_CONTENT_IMAGE;
        }
        return ViewType.RIGHT_CONTENT_TEXT;
    }

    // "리사이클러뷰에 들어갈 뷰 홀더", 그리고 "그 뷰 홀더에 들어갈 아이템들을 셋팅"
    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        ItemElementLoadingBinding itemElementLoadingBinding;
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            itemElementLoadingBinding = ItemElementLoadingBinding.bind(itemView);
        }
    }


    public class CenterViewHolder extends RecyclerView.ViewHolder{
        ItemElementCenterSystemBinding centerSystemBinding;
        public CenterViewHolder(@NonNull View itemView) {
            super(itemView);
            centerSystemBinding = ItemElementCenterSystemBinding.bind(itemView);
        }
    }

    public class LeftImageViewHolder extends RecyclerView.ViewHolder{
        ItemElementLeftImageBinding leftImageBinding;

        public LeftImageViewHolder(@NonNull View itemView) {
            super(itemView);
            leftImageBinding = ItemElementLeftImageBinding.bind(itemView);
        }
    }

    public class LeftTextViewHolder extends RecyclerView.ViewHolder{
        ItemElementLeftTextBinding leftTextBinding;
        public LeftTextViewHolder(@NonNull View itemView) {
            super(itemView);
            leftTextBinding = ItemElementLeftTextBinding.bind(itemView);
        }
    }
    public class RightImageViewHolder extends RecyclerView.ViewHolder{
        ItemElementRightImageBinding rightImageBinding;
        public RightImageViewHolder(@NonNull View itemView) {
            super(itemView);
            rightImageBinding = ItemElementRightImageBinding.bind(itemView);

        }
    }
    public class RightTextViewHolder extends RecyclerView.ViewHolder{
        ItemElementRightTextBinding rightTextBinding;
        public RightTextViewHolder(@NonNull View itemView) {
            super(itemView);
            rightTextBinding = ItemElementRightTextBinding.bind(itemView);
        }
    }

}
