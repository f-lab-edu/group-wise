package wj.flab.group_wise.dto;

public record JwtResponse (String token) {
    private static String type = "Bearer";
}
