package api;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserTime {
       private String name;
       private String job;

    public UserTime(String name, String job) {
        this.name = name;
        this.job = job;
    }
}
