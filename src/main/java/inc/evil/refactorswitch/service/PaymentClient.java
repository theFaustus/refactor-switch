package inc.evil.refactorswitch.service;

import inc.evil.refactorswitch.domain.Card;
import inc.evil.refactorswitch.service.dto.PaymentResponse;

public interface PaymentClient {
    PaymentResponse debitCard(Card card, double price);
}
