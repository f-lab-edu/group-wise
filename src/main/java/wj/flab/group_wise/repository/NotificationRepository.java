package wj.flab.group_wise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wj.flab.group_wise.domain.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
