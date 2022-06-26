package inc.evil.refactorswitch.service;

import inc.evil.refactorswitch.domain.Card;
import inc.evil.refactorswitch.service.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class PayBuddyPaymentClientImpl implements PaymentClient {

    @Override
    public PaymentResponse debitCard(Card card, double price) {
        //call the 3rd party app to process transaction
        return new PaymentResponse("");
    }
}
