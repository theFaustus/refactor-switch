package inc.evil.refactorswitch.service.commission;

import inc.evil.refactorswitch.domain.CardType;
import inc.evil.refactorswitch.domain.Category;
import inc.evil.refactorswitch.domain.Client;
import inc.evil.refactorswitch.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static inc.evil.refactorswitch.service.commission.DefaultCommissionApplierStrategyImpl.DEFAULT_COMMISSION;

@Slf4j
@Service
public class TravelCommissionApplierStrategyImpl implements CommissionApplierStrategy {
    private static final double TRAVEL_COMMISSION = 0.014;

    @Override
    public double applyCommission(Client client, Product product) {
        double price = product.getPrice();
        Long productId = product.getId();
        double updatedPrice;
        if (product.getCategory() == Category.TRAVEL) {
            updatedPrice = price + (price * TRAVEL_COMMISSION);
            log.info("Applied {} commission for client {} with card {} and price now = {} for product id {}",
                    TRAVEL_COMMISSION, client.getId(), client.getCard().getType(), updatedPrice, productId);
        } else {
            updatedPrice = price + (price * DEFAULT_COMMISSION);
            log.info("Applied {} commission for client {} with card {} and price now = {} for product id {}",
                    DEFAULT_COMMISSION, client.getId(), client.getCard().getType(), updatedPrice, productId);
        }
        return updatedPrice;
    }

    @Override
    public CardType getCardType() {
        return CardType.TRAVEL;
    }
}
