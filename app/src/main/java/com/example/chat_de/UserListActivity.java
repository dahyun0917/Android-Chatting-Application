package com.example.chat_de;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chat_de.databinding.ActivityUserListBinding;
import com.example.chat_de.datas.ChatRoomMeta;
import com.example.chat_de.datas.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class UserListActivity extends AppCompatActivity implements TextWatcher {
    String[] items = {"전체","1-10기","11-20기","21-30기","31-40기","41-50기","51-60기","61기-70기","71기-"};

    private ArrayList<UserListItem>[] userList = new ArrayList[9];
    private ArrayList<UserListItem> selectedList = new ArrayList<>();
    private UserListAdapter userListAdapter;
    private SelectedListAdapter selectedListAdapter;
    private HashMap<String, UserListItem> userDictionary = new HashMap<>();

    private final int NEW_CHAT = 1;
    private final int INVITE_CHAT = 2;
    private int mode=0;
    private User userMe;
    private String chatRoomKey = null;
    private ChatRoomMeta chatRoomMeta = null;
    private String myUserKey;
    private String chatRoomName="";
    private HashSet<String> userKeySet;

    private ActivityUserListBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        for(int i = 0; i < userList.length; ++i) {
            userList[i] = new ArrayList<>();
        }
        myUserKey = ChatDB.getCurrentUserKey();
        setView();
    }
    @Override
    protected void onStart() {
        super.onStart();
        showUserList();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        userListAdapter.setOnCheckBoxClickListener(null);
    }

    public void setView() {
        binding.selectedList.setVisibility(View.GONE);
        setActionBar();
        /*검색 기능 추가*/
        binding.searchText.addTextChangedListener(this);

        /*취소, 완료 설정*/
        binding.cancel.setOnClickListener(view -> finish());
        binding.complete.setOnClickListener(view -> {
            if(returnChoose().size()==0){
                Toast.makeText(UserListActivity.this,"초대할 사람을 선택해주세요.",Toast.LENGTH_SHORT).show();
            }
            else{
                if(mode== NEW_CHAT){
                    //채팅방 만들기
                    showNewChatDialog();
                }
                else if(mode==INVITE_CHAT){
                    //초대하기
                    inviteChatRoom();
                }
            }
        });

        /*텍스트뷰 내용 지우기*/
        binding.searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                binding.searchText.setText(null);
            }
        });

        /*모두 선택 버튼 설정*/
        binding.checkedAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.selectedList.setVisibility(View.VISIBLE);
                for(UserListItem i: userListAdapter.getFilterUserList()){
                    i.setChecked(true);
                    if(!selectedList.contains(userDictionary.get(i.getUserKey())))
                        selectedList.add(userDictionary.get(i.getUserKey()));
                }
                selectedListAdapter.notifyDataSetChanged();
                userListAdapter.notifyDataSetChanged();
            }
        });

        /*유저리스트 리사이클러뷰 설정*/
        userListAdapter = new UserListAdapter(getApplicationContext(), userList);
        binding.recyclerUserList.setAdapter(userListAdapter);
        binding.recyclerUserList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false));
        /*선택된 유저 뜨는 리사이클러뷰 설정*/
        selectedListAdapter = new SelectedListAdapter(UserListActivity.this,selectedList);
        binding.selectedList.setAdapter(selectedListAdapter);
        binding.selectedList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL,false));
    }
    public void setActionBar(){
        //인텐트로 mode값 , 초대/생성하는 User 정보 받아오기기
        Intent intent = getIntent();
        mode = intent.getIntExtra("tag",0);
        userKeySet = (HashSet<String>)intent.getSerializableExtra("userList");
        setSupportActionBar(binding.toolbarUserList);
        getSupportActionBar().setTitle("");
        if(mode== NEW_CHAT){
            //채팅방 만들기
            binding.userListMode.setText("새 채팅방 만들기"); ;
        }
        else if(mode==INVITE_CHAT){
            //초대하기
            binding.userListMode.setText("초대하기");
            chatRoomKey = intent.getStringExtra("chatRoomKey");
            chatRoomMeta = (ChatRoomMeta)intent.getSerializableExtra("chatRoomMeta");
        }
        else{
            Log.e("ERROR MODE","Mode값은 1또는 2만 가능합니다.");
        }
    }
    private void showNewChatDialog(){
        //다이얼로그(대화상자) 띄우기
        final EditText editText = new EditText(UserListActivity.this);
        AlertDialog.Builder dlg = new AlertDialog.Builder(UserListActivity.this);
        dlg.setTitle("채팅방 이름 입력"); //제목
        dlg.setMessage("새로 생성할 채팅방 이름을 입력해주세요.");
        dlg.setView(editText);
        dlg.setPositiveButton("입력", (dialogInterface, i) -> {
            chatRoomName = "";
            chatRoomName = editText.getText().toString();
            if(chatRoomName.isEmpty()){
                chatRoomName = changeToString(returnChoose(),false);
            }
            createChatRoom();
        });
        dlg.setNegativeButton("취소",null);
        dlg.show();
    }

    private void classifyAdd(@NonNull UserListItem item){
        userList[(item.getGeneration()-1)/10].add(item);
        userDictionary.put(item.getUserKey(), item);
    }
    private void showUserList() {
        //초기화 및 데이터 불러오기
//        getAllUserList();
        ChatDB.getUsersCompleteEventListener(item -> {
            for(Map.Entry<String, User> i: item.entrySet()) {
                if(!userKeySet.contains(i.getKey())) {
                    classifyAdd(new UserListItem(i.getValue()));
                }
            }
            userMe = item.get(myUserKey);
            userListAdapter.setOnCheckBoxClickListener(new CheckBoxClickListener() {
                @Override
                public void onCheckedClick(String userID) {
                    selectedList.add(userDictionary.get(userID));
                    selectedListAdapter.notifyDataSetChanged();
                    if(selectedList.size()==1){
                        Animation animation = new AlphaAnimation(0, 1);
                        animation.setDuration(200);
                        binding.selectedList.setVisibility(View.VISIBLE);
                        binding.selectedList.setAnimation(animation);
                    }
                }

                @Override
                public void onUnCheckedClick(String userID) {
                    selectedList.remove(userDictionary.get(userID));
                    selectedListAdapter.notifyDataSetChanged();
                    if(selectedList.size() == 0){
                        Animation animation = new AlphaAnimation(1, 0);
                        animation.setDuration(80);
                        binding.selectedList.setVisibility(View.GONE);
                        binding.selectedList.setAnimation(animation);
                    }
                }
            });
            /*스피너 설정*/
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            binding.spinner.setAdapter(adapter);
            binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                //아이템이 선택되면
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    // int i : item의 순서대로 0번부터 n-1번까지
                    // userList[0]: 1-10기 ...
                    if(i == 0) {
                        userListAdapter.allUsersList(userList);
                    } else {
                        userListAdapter.setUserList(userList[i-1]);
                    }
                }
                //스피너에서 아무것도 선택되지 않은 상태일때
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    userListAdapter.allUsersList(userList);
                }
            });
        });
    }
    private ArrayList<User> returnChoose(){
        ArrayList<User> choose = new ArrayList<>();
        for(ArrayList<UserListItem> list : userList) {
            for (UserListItem i : list) {
                if (i.getChecked()) {
                    choose.add(i);
                }
            }
        }
        return choose;
    }
    private void createChatRoom(){
        //체크박스로 표시된 유저 정보를 받아옴.
        ArrayList<User> list = returnChoose();
        list.add(userMe);
        //채팅방 만들기 누른 유저 정보 : callUserName
        //새 ChatRoom 생성
        //chatRoomJoined에 list의 유저 추가-> list.get(i).getUserKey() 사용
        //list의 user의 userJoined에 생성된 채팅방 정보 추가
        //생성메세지(message) 현재 채팅방에 시스템 메세지로 추가
        ChatDB.setChatRoomCompleteListener(chatRoomName, list, userMe, generatedKey -> {
            Intent intent = new Intent(this, RoomActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("chatRoomKey", generatedKey);
            startActivity(intent);
            finish(); //액티비티 종료
        });
    }
    private void inviteChatRoom(){
        //체크박스로 표시된 유저 정보를 받아옴
        ArrayList<User> list = returnChoose();
        //초대하기 누른 유저 정보 : callUserName

        ChatDB.inviteUserListCompleteListener(chatRoomKey, chatRoomMeta, list, userMe, dummyKey -> finish());
    }
    private String changeToString(ArrayList<User> list, boolean formal){
        //유저리스트를 ~님, 형식으로 바꿔서 String으로 반환해줌.
        StringBuilder result = new StringBuilder();
        if(formal){
            for(User i : list){
                result.append(i.getName()).append("님, ");
            }
            return result.substring(0, result.length() - 3);
        }
        else {
            for (User i : list) {
                result.append(i.getName()).append(", ");
            }
            return result.substring(0, result.length() - 2);
        }
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        /*검색 설정*/
        userListAdapter.getFilter().filter(charSequence);
    }
    @Override
    public void afterTextChanged(Editable editable) {  }
}
