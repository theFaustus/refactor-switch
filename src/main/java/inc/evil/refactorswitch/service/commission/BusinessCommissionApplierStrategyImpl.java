package inc.evil.refactorswitch.service.commission;

import inc.evil.refactorswitch.domain.CardType;
import inc.evil.refactorswitch.domain.Client;
import inc.evil.refactorswitch.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BusinessCommissionApplierStrategyImpl implements CommissionApplierStrategy {
    private static final double BUSINESS_COMMISSION = 0.017;
    private static final double BUSINESS_TAX = 0.003;

    @Override
    public double applyCommission(Client client, Product product) {
        double price = product.getPrice();
        Long productId = product.getId();
        double updatedPrice = price + (price * BUSINESS_COMMISSION) + (price * BUSINESS_TAX);
        log.info("Applied {} commission and tax {} for client {} with card {} and price now = {} for product id {}",
                BUSINESS_COMMISSION, BUSINESS_TAX, client.getId(), client.getCard().getType(), updatedPrice, productId);
        return updatedPrice;
    }

    @Override
    public CardType getCardType() {
        return CardType.BUSINESS;
    }
}
