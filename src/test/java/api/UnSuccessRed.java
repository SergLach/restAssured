package api;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UnSuccessRed {
    private String error;

    public UnSuccessRed(String error) {
        this.error = error;
    }
}
