package inc.evil.refactorswitch.service.commission;

import inc.evil.refactorswitch.domain.CardType;
import inc.evil.refactorswitch.domain.Client;
import inc.evil.refactorswitch.domain.Product;

public interface CommissionApplierStrategy {
    /**
     * Method that applies commission based on client data and returns updated price
     *
     * @param client  client making the payment
     * @param product actual product
     * @return updated price
     */
    double applyCommission(Client client, Product product);

    CardType getCardType();
}
