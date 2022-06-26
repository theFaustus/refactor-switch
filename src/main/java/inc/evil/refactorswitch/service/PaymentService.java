package inc.evil.refactorswitch.service;

import inc.evil.refactorswitch.domain.Client;

public interface PaymentService {
    void payForProduct(Long productId, Client client);
}
