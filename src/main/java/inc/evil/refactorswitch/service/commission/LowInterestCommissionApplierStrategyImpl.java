package inc.evil.refactorswitch.service.commission;

import inc.evil.refactorswitch.domain.CardType;
import inc.evil.refactorswitch.domain.Client;
import inc.evil.refactorswitch.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LowInterestCommissionApplierStrategyImpl implements CommissionApplierStrategy {
    private static final double LOW_INTEREST_COMMISSION = 0.0165;

    @Override
    public double applyCommission(Client client, Product product) {
        double price = product.getPrice();
        Long productId = product.getId();
        double updatedPrice = price + (price * LOW_INTEREST_COMMISSION);
        log.info("Applied {} commission for client {} with card {} and price now = {} for product id {}",
                LOW_INTEREST_COMMISSION, client.getId(), client.getCard().getType(), updatedPrice, productId);
        return updatedPrice;
    }

    @Override
    public CardType getCardType() {
        return CardType.LOW_INTEREST;
    }
}
