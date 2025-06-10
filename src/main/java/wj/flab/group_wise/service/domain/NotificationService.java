package wj.flab.group_wise.service.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wj.flab.group_wise.domain.Member;
import wj.flab.group_wise.domain.Notification;
import wj.flab.group_wise.repository.NotificationRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final MemberService memberService;
    private final NotificationRepository notificationRepository;
//    private final EmailService emailService;

    public boolean notify(Notification notification) {
        try {
            String title = notification.getTitle();
            String message = notification.getMessage();
            Member member = memberService.findMember(notification.getMemberId());

//            emailService.sendEmail(member.getEmail(), title, message);
            notificationRepository.save(notification);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
