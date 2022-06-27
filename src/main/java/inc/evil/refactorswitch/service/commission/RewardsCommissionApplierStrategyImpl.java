package inc.evil.refactorswitch.service.commission;

import inc.evil.refactorswitch.domain.CardType;
import inc.evil.refactorswitch.domain.Client;
import inc.evil.refactorswitch.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RewardsCommissionApplierStrategyImpl implements CommissionApplierStrategy {
    private static final double REWARDS_COMMISSION = 0.016;
    private static final double REWARDS_DISCOUNT = 0.007;

    @Override
    public double applyCommission(Client client, Product product) {
        double price = product.getPrice();
        Long productId = product.getId();
        double updatedPrice = price + (price * REWARDS_COMMISSION);
        log.info("Applied {} commission for client {} with card {} and price now = {} for product id {}",
                REWARDS_COMMISSION, client.getId(), client.getCard().getType(), updatedPrice, productId);
        if (client.isRewardEligible()) {
            updatedPrice = price - (price * REWARDS_DISCOUNT);
            log.info("Applied {} discount for client {} with card {} and price now = {} for product id {}",
                    REWARDS_DISCOUNT, client.getId(), client.getCard().getType(), updatedPrice, productId);
        }
        return updatedPrice;
    }

    @Override
    public CardType getCardType() {
        return CardType.REWARDS;
    }
}
