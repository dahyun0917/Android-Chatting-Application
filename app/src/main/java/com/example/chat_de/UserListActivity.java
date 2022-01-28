package com.example.chat_de;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_de.databinding.ActivityUserListBinding;
import com.example.chat_de.datas.AUser;
import com.example.chat_de.datas.ChatRoomMeta;
import com.example.chat_de.datas.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class UserListActivity extends AppCompatActivity implements TextWatcher {
    private final String[] items = {"전체","1-10기","11-20기","21-30기","31-40기","41-50기","51-60기","61기-70기","71기-"};
    private final int generationCountPerTen = items.length - 1;

    private ArrayList<UserListItem>[] userList;
    private ArrayList<UserListItem> selectedList;
    private UserListAdapter userListAdapter;
    private SelectedListAdapter selectedListAdapter;
    private HashMap<String, UserListItem> userDictionary;

    private final int NEW_CHAT = 1;
    private final int INVITE_CHAT = 2;
    private int mode=0;
    private AUser userMe;
    private String chatRoomKey = null;
    private ChatRoomMeta currentChatRoomMeta = null;
    private String newChatRoomName ="";
    private String newChatRoomPicture="";
    private HashSet<String> userKeySet;
    ActivityResultLauncher<Intent> getCreateRoomMeta;

    private ActivityUserListBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setUpUserListActivity();
        showUserList();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        userListAdapter.setListener(null);
    }

    public void setUpUserListActivity() {
    /*view, 변수 할당, click listener 등 한번만 설정되는 화면 구성 설정*/
        userList = new ArrayList[generationCountPerTen];
        for(int i = 0; i < generationCountPerTen; i++) {
            userList[i] = new ArrayList<>();
        }
        selectedList = new ArrayList<>();
        userDictionary = new HashMap<>();

        //새 채팅방 생성시 intent를 위한 getCreateRoomMeta 초기화
        getCreateRoomMeta = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if(result.getResultCode() == 9001) {
                Intent intent = result.getData();
                if(intent != null) {
                    newChatRoomName = intent.getStringExtra("chatRoomName");
                    newChatRoomPicture = intent.getStringExtra("chatRoomPicture");
                    if (newChatRoomName.trim().isEmpty()) {
                        Toast.makeText(UserListActivity.this,"채팅방 이름이 기본이름으로 설정됩니다.",Toast.LENGTH_SHORT).show();
                        newChatRoomName = changeToString(returnChoose(), false, true);
                    }
                    if (newChatRoomPicture.isEmpty()) {
                        newChatRoomPicture = "";
                    }
                    //파이어베이스에 채팅방 업로드
                    uploadChatRoom();
                }
            }
        });

        //유저리스트 리사이클러뷰 설정
        userListAdapter = new UserListAdapter(getApplicationContext(), userList);
        binding.recyclerUserList.setAdapter(userListAdapter);
        binding.recyclerUserList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false));
        //선택된 유저 뜨는 리사이클러뷰 설정
        selectedListAdapter = new SelectedListAdapter(UserListActivity.this,selectedList);
        binding.selectedList.setAdapter(selectedListAdapter);
        binding.selectedList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL,false));

        binding.selectedList.setVisibility(View.GONE);
        setActionBar();
        //검색 기능 추가
        binding.searchText.addTextChangedListener(this);

        //취소, 완료 설정
        binding.cancel.setOnClickListener(view -> finish());
        binding.complete.setOnClickListener(view -> {
            if(returnChoose().size()==0){
                Toast.makeText(UserListActivity.this,"초대할 사람을 선택해주세요.",Toast.LENGTH_SHORT).show();
            }
            else{
                if(mode == NEW_CHAT){
                    //채팅방 만들기
                    createChatRoom();
                }
                else if(mode == INVITE_CHAT){
                    //초대하기
                    inviteChatRoom();
                }
            }
        });

        //텍스트뷰 내용 지우기
        binding.searchButton.setOnClickListener(view -> binding.searchText.setText(null));

        //모두 선택 버튼 설정
        binding.checkedAll.setOnClickListener(view -> {
            for(UserListItem i: userListAdapter.getFilterUserList()){
                i.setChecked(true);
                if(!selectedList.contains(userDictionary.get(i.getUserKey())))
                    selectedList.add(userDictionary.get(i.getUserKey()));
            }
            if(selectedList.size() > 0) {
                binding.selectedList.setVisibility(View.VISIBLE);
            }
            selectedListAdapter.notifyDataSetChanged();
            userListAdapter.notifyDataSetChanged();
        });
    }
    public void setActionBar(){
        /*인텐트로 mode값 , 초대/생성하는 User 정보 받아오고, actionbar setting*/
        Intent intent = getIntent();
        mode = intent.getIntExtra("tag",0);
        userKeySet = (HashSet<String>)intent.getSerializableExtra("userList");
        setSupportActionBar(binding.toolbarUserList);
        getSupportActionBar().setTitle("");
        if(mode == NEW_CHAT){
            //채팅방 만들기
            binding.userListMode.setText("새 채팅방 만들기"); ;
        }
        else if(mode == INVITE_CHAT){
            //초대하기
            binding.userListMode.setText("초대하기");
            chatRoomKey = intent.getStringExtra("chatRoomKey");
            currentChatRoomMeta = (ChatRoomMeta)intent.getSerializableExtra("chatRoomMeta");
        }
        else{
            Log.e("ERROR MODE","Mode값은 1또는 2만 가능합니다.");
        }
    }
    private void createChatRoom() {
        /*채팅방 생성*/
        getCreateRoomMeta.launch(new Intent(UserListActivity.this, SetUpRoomMetaActivity.class));
    }

    private void classifyAdd(@NonNull UserListItem item){
        /*기수별로 나누어서 추가*/
        userList[(item.getGeneration()-1)/10].add(item);
        userDictionary.put(item.getUserKey(), item);
    }
    private void showUserList() {
        /*초기화 및 데이터 불러오기*/
        //로딩 시작
        MyProgressDialog loading = new MyProgressDialog(this);
        loading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loading.setCanceledOnTouchOutside(false);  //로딩 중 화면 눌렀을 때 로딩바 취소되지 않음
        //loading.setCancelable(false);  //로딩 중 뒤로가기 버튼 눌렀을 때 로딩방 취소되지 않음
        loading.show();

        ChatDB.getUsersCompleteListener(item -> { //모든 유저 목록 불러옴
            for(Map.Entry<String, User> i: item.entrySet()) {
                if(!userKeySet.contains(i.getKey())) {
                    classifyAdd(new UserListItem(i.getValue()));
                }
            }
            userMe = ChatDB.getCurrentUser();
            userListAdapter.setListener(new UserSelectListener() {
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
            selectedListAdapter.setListener(new UserSelectListener() {
                @Override
                public void onCheckedClick(String userID) {

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
                    userListAdapter.notifyDataSetChanged();
                }
            });
            //스피너 설정
            if(ChatMode.getChatMode()>0){
                binding.spinner.setVisibility(View.GONE);
                //TODO : 나중에 allUsers가 아니라 해당 기수만 뜨도록.(현재 chatMode에 기수 정보 담으면 용도에 맞게 실행됨)
                userListAdapter.setUserList(userList[(ChatMode.getChatMode()-1)/10]);
            }
            else {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                binding.spinner.setAdapter(adapter);
                binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    //아이템이 선택되면
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        // int i : item의 순서대로 0번부터 n-1번까지
                        // userList[0]: 1-10기 ...
                        if (i == 0) {
                            userListAdapter.allUsersList(userList);
                        } else {
                            userListAdapter.setUserList(userList[i - 1]);
                        }
                    }

                    //스피너에서 아무것도 선택되지 않은 상태일때
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        userListAdapter.allUsersList(userList);
                    }
                });
            }
            loading.dismiss();
        });
    }
    private ArrayList<AUser> returnChoose(){
        /*체크표시된 유저 리스트를 반환*/
        ArrayList<AUser> choose = new ArrayList<>(selectedList);
        return choose;
    }
    private void uploadChatRoom(){
        /*새 ChatRoom firebase에 업로드*/
        ArrayList<AUser> list = returnChoose();
        list.add(userMe);
        ChatDB.setChatRoomCompleteListener(newChatRoomName,newChatRoomPicture, list, userMe, generatedKey -> {
            Intent intent = new Intent(this, RoomActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("chatRoomKey", generatedKey);
            startActivity(intent);
            finish(); //액티비티 종료
        });
    }
    private void inviteChatRoom(){
        /*초대하기*/
        ArrayList<AUser> list = returnChoose();
        ChatDB.inviteUserListCompleteListener(chatRoomKey, currentChatRoomMeta, list, userMe, dummyKey -> finish());
    }
    private String changeToString(ArrayList<AUser> list, boolean formal, boolean self){
        /*유저리스트를 ~님, 형식으로 바꿔서 String으로 반환해줌.*/
        StringBuilder result = new StringBuilder();
        if(self)
            if(formal)
                result.append(ChatDB.getCurrentUser().getName()).append("님, ");
            else
                result.append(ChatDB.getCurrentUser().getName()).append(", ");

        if(formal){
            for(AUser i : list){
                result.append(i.getName()).append("님, ");
            }
            return result.substring(0, result.length() - 3);
        }
        else {
            for (AUser i : list) {
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
    public void afterTextChanged(Editable editable) { }
}
