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
import com.example.chat_de.datas.Chat;
import com.example.chat_de.datas.ViewType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class RoomElementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Chat> myDataList;
    private enum MessageType { TEXT, IMAGE };
    private MessageType messageType;

    public RoomElementAdapter(ArrayList<Chat> dataList){
        myDataList = dataList;
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
            ((CenterViewHolder)viewHolder).textv.setText(myDataList.get(position).getText());
        }else if(viewHolder instanceof LeftViewHolder){
            if(messageType.equals(MessageType.TEXT)){
                ((LeftViewHolder)viewHolder).textvNicname.setText(myDataList.get(position).getFrom());
                ((LeftViewHolder)viewHolder).textvMsg.setText(myDataList.get(position).getText());
                ((LeftViewHolder)viewHolder).textvTime.setText(str);
            }
            else{
                ((LeftViewHolder)viewHolder).textvNicname.setText(myDataList.get(position).getFrom());
                ((LeftViewHolder)viewHolder).textvTime.setText(str);
                Glide.with(viewHolder.itemView.getContext()).load(myDataList.get(position).getText()).into(((LeftViewHolder)viewHolder).imagevMsg);
            }
        }else{
            if(messageType.equals(MessageType.TEXT)){
                ((RightViewHolder)viewHolder).textvNicname.setText(myDataList.get(position).getFrom());
                ((RightViewHolder)viewHolder).textvMsg.setText(myDataList.get(position).getText());
                ((RightViewHolder)viewHolder).textvTime.setText(str);}
            else{
                ((RightViewHolder)viewHolder).textvNicname.setText(myDataList.get(position).getFrom());
                ((RightViewHolder)viewHolder).textvTime.setText(str);
                ImageView imagevMsg= viewHolder.itemView.findViewById(R.id.imagev_msg);
                Glide.with(viewHolder.itemView.getContext()).load(myDataList.get(position).getText()).into(imagevMsg);
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
        TextView textv;

        public CenterViewHolder(@NonNull View itemView) {
            super(itemView);
            textv = itemView.findViewById(R.id.textv);
        }
    }

    public class LeftViewHolder extends RecyclerView.ViewHolder{
        //CircleImageView imgv;
        TextView textvMsg;
        TextView textvTime;
        TextView textvNicname;
        ImageView imagevMsg;
        public LeftViewHolder(@NonNull View itemView) {
            super(itemView);
            //imgv = (CircleImageView)itemView.findViewById(R.id.imgv);
            if(messageType == MessageType.TEXT) {
                textvNicname = itemView.findViewById(R.id.textv_nicname);
                textvMsg = itemView.findViewById(R.id.textv_msg);
                textvTime = itemView.findViewById(R.id.textv_time);
            }
            else if(messageType == MessageType.IMAGE){
                textvNicname = itemView.findViewById(R.id.textv_nicname);
                imagevMsg = itemView.findViewById(R.id.imagev_msg);
                textvTime = itemView.findViewById(R.id.textv_time);
            }
        }
    }

    public class RightViewHolder extends RecyclerView.ViewHolder{
        TextView textvMsg;
        TextView textvTime;
        TextView textvNicname;
        ImageView imagevMsg;

        public RightViewHolder(@NonNull View itemView) {
            super(itemView);
            if(messageType.equals(MessageType.TEXT)) {
                textvNicname = itemView.findViewById(R.id.textv_nicname);
                textvMsg = itemView.findViewById(R.id.textv_msg);
                textvTime = itemView.findViewById(R.id.textv_time);
            }
            else if(messageType.equals(MessageType.IMAGE)){
                textvNicname = itemView.findViewById(R.id.textv_nicname);
                imagevMsg = itemView.findViewById(R.id.imagev_msg);
                textvTime = itemView.findViewById(R.id.textv_time);
            }
        }
    }

}


