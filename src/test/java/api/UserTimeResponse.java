package api;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
@NoArgsConstructor
@Getter
public class UserTimeResponse extends UserTime{
    private String updatedAt;

    public UserTimeResponse(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserTimeResponse(String name, String job, String updatedAt) {
        super(name, job);
        this.updatedAt = updatedAt;
    }
}
