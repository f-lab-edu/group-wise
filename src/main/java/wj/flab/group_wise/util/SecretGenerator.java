package wj.flab.group_wise.util;

import java.util.UUID;

public class SecretGenerator {
    public static void main(String[] args) {
        String secret = UUID.randomUUID().toString().replace("-", "");
        System.out.println("JWT Secret: " + secret);
    }
}
