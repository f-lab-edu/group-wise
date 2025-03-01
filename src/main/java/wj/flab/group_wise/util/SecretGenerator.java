package wj.flab.group_wise.util;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecretGenerator {
    public static void main(String[] args) {
        String secret = UUID.randomUUID().toString().replace("-", "");
        log.info("JWT Secret: {}", secret);
    }
}
