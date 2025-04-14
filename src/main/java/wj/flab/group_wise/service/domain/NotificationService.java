package wj.flab.group_wise.service.domain;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wj.flab.group_wise.domain.Member;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MemberService memberService;
//    private final EmailService emailService;
//    private final SMSService smsService;

    public void notifyMembers(List<Long> memberIds, String title, String message) {
        memberIds.forEach(memberId -> notifyMember(memberId, title, message));
    }

    public void notifyMember(Long memberId, String title, String message) {
        Member member = memberService.findMember(memberId);

//        // 이메일 알림
//        emailService.sendEmail(member.getEmail(), title, message);
//
//        // SMS 알림 (전화번호가 있는 경우)
//        if (member.getPhoneNumber() != null) {
//            smsService.sendSMS(member.getPhoneNumber(), message);
//        }

        // 웹 알림 저장 (사용자가 웹사이트에 접속했을 때 보여줄 알림)
//        saveWebNotification(member, title, message, groupPurchase.getId());

    }


//    private void saveWebNotification(Member member, String title, String message, Long groupPurchaseId) {
//        // 데이터베이스에 알림 정보 저장
//        Notification notification = new Notification();
//        notification.setMember(member);
//        notification.setTitle(title);
//        notification.setMessage(message);
//        notification.setGroupPurchaseId(groupPurchaseId);
//        notification.setCreatedAt(LocalDateTime.now());
//        notification.setRead(false);
//
//        notificationRepository.save(notification);
//    }
}
