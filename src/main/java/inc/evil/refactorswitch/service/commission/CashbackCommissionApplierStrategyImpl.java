package inc.evil.refactorswitch.service.commission;

import inc.evil.refactorswitch.domain.CardType;
import inc.evil.refactorswitch.domain.Client;
import inc.evil.refactorswitch.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CashbackCommissionApplierStrategyImpl implements CommissionApplierStrategy {
    private static final double CASHBACK_COMMISSION = 0.016;
    private static final double CASHBACK_DISCOUNT = 0.006;

    @Override
    public double applyCommission(Client client, Product product) {
        double price = product.getPrice();
        Long productId = product.getId();
        double updatedPrice = price + (price * CASHBACK_COMMISSION);
        log.info("Applied {} commission for client {} with card {} and price now = {} for product id {}",
                CASHBACK_COMMISSION, client.getId(), client.getCard().getType(), updatedPrice, productId);
        if (client.getCard().isCashbackEnabled()) {
            updatedPrice = price - (price * CASHBACK_DISCOUNT);
            log.info("Applied {} discount for client {} with card {} and price now = {} for product id {}",
                    CASHBACK_DISCOUNT, client.getId(), client.getCard().getType(), updatedPrice, productId);
        }
        return updatedPrice;
    }

    @Override
    public CardType getCardType() {
        return CardType.CASHBACK;
    }
}
