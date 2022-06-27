package inc.evil.refactorswitch.service.commission;

import inc.evil.refactorswitch.domain.CardType;
import inc.evil.refactorswitch.domain.Client;
import inc.evil.refactorswitch.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static inc.evil.refactorswitch.service.commission.DefaultCommissionApplierStrategyImpl.DEFAULT_COMMISSION;

@Slf4j
@Service
public class StudentCommissionApplierStrategyImpl implements CommissionApplierStrategy {
    private static final double STUDENT_SCHOOL_COMMISSION = 0.008;
    private static final double STUDENT_UNIVERSITY_COMMISSION = 0.012;

    @Override
    public double applyCommission(Client client, Product product) {
        double price = product.getPrice();
        Long productId = product.getId();
        double updatedPrice;
        if (client.getAge() >= 18 && client.getAge() <= 21) {
            updatedPrice = price + (price * STUDENT_SCHOOL_COMMISSION);
            log.info("Applied {} commission for client {} with card {} and price now = {} for product id {}",
                    STUDENT_SCHOOL_COMMISSION, client.getId(), client.getCard().getType(), updatedPrice, productId);
        } else if (client.getAge() > 21 && client.getAge() <= 23) {
            updatedPrice = price + (price * STUDENT_UNIVERSITY_COMMISSION);
            log.info("Applied {} commission for client {} with card {} and price now = {} for product id {}",
                    STUDENT_UNIVERSITY_COMMISSION, client.getId(), client.getCard().getType(), updatedPrice, productId);
        } else {
            updatedPrice = price + (price * DEFAULT_COMMISSION);
            log.info("Applied {} commission for client {} with card {} and price now = {} for product id {}",
                    DEFAULT_COMMISSION, client.getId(), client.getCard().getType(), updatedPrice, productId);
        }
        return updatedPrice;
    }

    @Override
    public CardType getCardType() {
        return CardType.STUDENT;
    }
}
