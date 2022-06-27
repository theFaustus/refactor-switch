package inc.evil.refactorswitch.service.commission;

import inc.evil.refactorswitch.domain.CardType;
import inc.evil.refactorswitch.domain.Client;
import inc.evil.refactorswitch.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DefaultCommissionApplierStrategyImpl implements CommissionApplierStrategy {
    public static final double DEFAULT_COMMISSION = 0.015;

    @Override
    public double applyCommission(Client client, Product product) {
        double price = product.getPrice();
        Long productId = product.getId();
        double updatedPrice = price + (price * DEFAULT_COMMISSION);
        log.info("Applied default commission {} for client {} with card {} and price now = {} for product id {}",
                DEFAULT_COMMISSION, client.getId(), client.getCard().getType(), updatedPrice, productId);
        return updatedPrice;
    }

    @Override
    public CardType getCardType() {
        return CardType.SIMPLE;
    }
}
