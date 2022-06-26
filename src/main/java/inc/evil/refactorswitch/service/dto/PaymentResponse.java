package inc.evil.refactorswitch.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private String errorMessage = "";

    public boolean isSuccess() {
        return errorMessage.isEmpty();
    }
}
