package wj.flab.group_wise.config;

import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    /*
    기본 설정(기본 TaskExecutor) 으로 실행되는 내용:
    - 스레드 풀 크기: 무제한 (매번 새 스레드 생성)
    - 큐 크기: 무제한
    - 스레드 이름: task-1, task-2, task-3...
     */

    /*
    # 아래 동작방식
    1. 요청 1~5개: 즉시 처리 (기본 스레드 사용)
    2. 요청 6~15개: 새 스레드 생성해서 처리
    3. 요청 15~115: 큐에서 대기
    4. 요청 116개 이상: RejectedExecutionException 발생 (시스템 보호)
     */

    @Bean("notificationTaskExecutor")
    public TaskExecutor notificationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 기본 스레드 수 (항상 유지)
        executor.setCorePoolSize(5);

        // 최대 스레드 수 (피크 시간 대비)
        executor.setMaxPoolSize(15);

        // 대기열 크기 (급작스런 트래픽 대비)
        // 추천: 예상 동시 요청 수 * 2
        executor.setQueueCapacity(100);

        // 스레드 이름 (디버깅용)
        executor.setThreadNamePrefix("notification-");

        // 스레드 유지 시간 (idle 스레드 정리)
        // CorePoolSize를 초과해서 생성된 스레드들이 idle 상태일 때 얼마나 살아있을지 결정
        executor.setKeepAliveSeconds(60);

        // 애플리케이션 종료 시 처리
        // 1. 새로운 작업 수락 중단
        // 2. 현재 실행 중인 작업들은 완료될 때까지 대기
        // 3. 큐에 대기 중인 작업들도 처리 완료 후 종료
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // 30초 지나도 작업이 남아있으면: 강제 종료
        executor.setAwaitTerminationSeconds(30);

        // 큐가 가득 찬 경우 정책
        // 4가지 정책이 있음. 현재 정책은 스레드 풀이 가득 차면, Controller 스레드가 직접 알림 발송 처리
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }
}
