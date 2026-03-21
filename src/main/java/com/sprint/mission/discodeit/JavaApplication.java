package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    public static UserService us = new JCFUserService();
    public static ChannelService cs = new JCFChannelService();
    public static MessageService ms = new JCFMessageService(us,cs);

    public static void main(String[] args) {
        System.out.println("------------ 사용자 테스트 시작---------------\n");
        System.out.println("------------ 사용자 생성 시작---------------\n");
        User user = new User("김태오", "teoh1234@1234.com","teoh1234");
        User user1 = new User("주찬빈", "chan1234@1234.com","chan1234");
        User user2 = new User("음정윤", "eum1234@1234.com","eum1234");
        User user3 = new User("김인제", "inje@1234.com","inje1234");
        System.out.println(user);
        System.out.println(user1);
        System.out.println(user2);
        System.out.println(user3);
        System.out.println("");
        System.out.println("------------ 사용자 생성 끝---------------\n");
        System.out.println("------------ 사용자 등록 및 전체 및 단건 조회 시작---------------\n");
        us.save(user);
        us.save(user1);
        us.save(user2);
        us.save(user3);
        System.out.println(us.findAll());
        System.out.println(us.findById(user.getUserId()));
        System.out.println("");
        System.out.println("------------ 사용자 등록 및 전체 및 단건 조회 끝---------------\n");
        System.out.println("------------ 사용자 정보 수정 후 조회 시작---------------\n");
        System.out.println(us.updateUsername(user.getUserId(), "김태오1"));
        System.out.println(us.updateEmail(user1.getUserId(),"joo1234@1234.com"));
        System.out.println(us.updatePassword(user2.getUserId(), "eum12345"));
        System.out.println("");
        System.out.println("------------ 사용자 정보 수정 후 조회 끝---------------\n");
        System.out.println("------------ 사용자 삭제 후 사용자 리스트 조회 시작---------------\n");
        us.deleteById(user3.getUserId());
        System.out.println(us.findAll());
        System.out.println("");
        System.out.println("------------ 사용자 삭제 후 사용자 리스트 조회 끝---------------\n");

        System.out.println("----------------------채널 테스트 시작--------------------------\n");
        System.out.println("------------ 채널 생성 시작---------------\n");
        Channel channel = new Channel("김태오의 채널");
        Channel channel1 = new Channel("주찬빈의 채널");
        Channel channel2 = new Channel("음정윤의 채널");
        System.out.println(channel);
        System.out.println(channel1);
        System.out.println(channel2);
        System.out.println("");
        System.out.println("------------ 채널 생성 끝---------------\n");
        System.out.println("------------ 채널 등록 및 전체 및 단건 조회 시작---------------\n");
        cs.save(channel);
        cs.save(channel1);
        cs.save(channel2);
        System.out.println(cs.findAll());
        System.out.println(cs.findById(channel.getChannelId()));
        System.out.println("");
        System.out.println("------------ 채널 등록 및 전체 및 단건 조회 끝---------------\n");
        System.out.println("------------ 채널 정보 수정 후 조회 시작---------------\n");
        System.out.println(cs.updateChannelName(channel.getChannelId(), "김태오의 채널1"));
        System.out.println("");
        System.out.println("------------ 채널 정보 수정 후 조회 끝---------------\n");
        System.out.println("------------ 채널 삭제 후 채널 리스트 조회 시작---------------\n");
        cs.deleteById(channel2.getChannelId());
        System.out.println(cs.findAll());
        System.out.println("");
        System.out.println("------------ 채널 삭제 후 채널 리스트 조회 끝---------------\n");
        System.out.println("---------------------- 메세지 테스트 시작--------------------------\n");
        System.out.println("------------ 메세지 생성 시작---------------\n");
        Message message = new Message("안녕하세요 태오입니다" , user, channel);
        Message message1 = new Message("안녕하세요 찬빈입니다" , user1, channel);
        Message message2 = new Message("안녕하세요 정윤입니다" , user2, channel1);
        Message message3 = new Message("안녕하세요 음정윤입니다", user2, channel2); //삭제된 채널의 메세지 생성 확인
        Message message4 = new Message("안녕하세요 인제입니다", user3, channel1); //삭제된 회원의 메세지 생성 확인
        System.out.println(message);
        System.out.println(message1);
        System.out.println(message2);
        System.out.println("");
        System.out.println("------------ 메세지 생성 끝---------------\n");
        System.out.println("------------ 메세지 등록 및 전체 및 단건 조회 시작---------------\n");
        ms.save(message);
        ms.save(message1);
        ms.save(message2);
        try {
        ms.save(message3); // 삭제된 채널의 메세지 저장
        }catch (IllegalArgumentException e){
            System.out.println("메세지 저장 실패 : " + e.getMessage());
        }
        try {
        ms.save(message4); // 삭제된 회원의 메세지 저장
        }catch (IllegalArgumentException e){
            System.out.println("메세지 저장 실패 : " + e.getMessage());
        }
        System.out.println(ms.findAll());
        System.out.println(ms.findById(message2.getMessageId()));
        System.out.println("");
        System.out.println("------------ 메세지 등록 및 전체 및 단건 조회 끝---------------\n");
        System.out.println("------------ 메세지 정보 수정 후 조회 시작---------------\n");
        System.out.println(ms.updateContent(message1.getMessageId(), "안녕하세요 주찬빈입니다"));
        System.out.println("");
        System.out.println("------------ 메세지 정보 수정 후 조회 끝---------------\n");
        System.out.println("------------ 메세지 삭제 후 메세지 리스트 조회 시작---------------\n");
        ms.deleteById(message1.getMessageId());
        System.out.println(ms.findAll());
        System.out.println("");
        System.out.println("------------ 메세지 삭제 후 메세지 리스트 조회 끝---------------\n");

    }
    }
