package inc.evil.refactorswitch.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Client {
    private String id;
    private String firstName;
    private String lastName;
    private Card card;
    private boolean isRewardEligible;
    private int age;

}
