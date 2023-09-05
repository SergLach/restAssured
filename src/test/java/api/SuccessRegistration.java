package api;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SuccessRegistration {

    private Integer id;
    private String token;

    public SuccessRegistration(int id, String token) {
        this.id = id;
        this.token = token;
    }
}
