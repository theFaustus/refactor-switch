package inc.evil.refactorswitch.service.commission;

import inc.evil.refactorswitch.domain.CardType;
import inc.evil.refactorswitch.domain.Client;
import inc.evil.refactorswitch.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SecuredCommissionApplierStrategyImpl implements CommissionApplierStrategy {
    private static final double SECURED_COMMISSION = 0.0155;

    @Override
    public double applyCommission(Client client, Product product) {
        double price = product.getPrice();
        Long productId = product.getId();
        double updatedPrice = price + (price * SECURED_COMMISSION);
        log.info("Applied {} commission for client {} with card {} and price now = {} for product id {}",
                SECURED_COMMISSION, client.getId(), client.getCard().getType(), updatedPrice, productId);
        return updatedPrice;
    }

    @Override
    public CardType getCardType() {
        return CardType.SECURED;
    }
}
