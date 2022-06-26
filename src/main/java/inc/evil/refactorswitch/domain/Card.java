package inc.evil.refactorswitch.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Card {
    private String cardHolder;
    private String cardNumber;
    private LocalDate expireDate;
    private String cvv;
    private CardType type;
    private boolean isCashbackEnabled;
}
