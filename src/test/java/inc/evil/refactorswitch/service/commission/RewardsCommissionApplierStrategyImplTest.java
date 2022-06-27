package inc.evil.refactorswitch.service.commission;

import inc.evil.refactorswitch.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RewardsCommissionApplierStrategyImplTest {

    private RewardsCommissionApplierStrategyImpl rewardsCommissionApplierStrategy = new RewardsCommissionApplierStrategyImpl();
    private Product product;
    private Card card;
    private Client client;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "178-456-789", "PlayStation 5", 300.00, ProductStatus.IN_STOCK, Category.GAMING);
        card = new Card("Sponge Bob", "8945-7898-7895-7895", LocalDate.MAX, "123", CardType.REWARDS, true);
        client = new Client("978456789", "Sponge", "bob", card, true, 19);
    }

    @Test
    void applyCommission_whenCardTypeREWARDSAndRewardEligibleTrue_appliesREWARDS_COMISSIONAndDISCOUNT() {
        double updatePrice = rewardsCommissionApplierStrategy.applyCommission(client, product);

        assertThat(updatePrice).isEqualTo(297.9);
    }

    @Test
    void applyCommission_whenCardTypeREWARDSAndRewardEligibleFalse_appliesREWARDS_COMISSION() {
        client.setRewardEligible(false);

        double updatePrice = rewardsCommissionApplierStrategy.applyCommission(client, product);

        assertThat(updatePrice).isEqualTo(304.8);
    }

    @Test
    void getCardType_returnsREWARDS() {
        assertThat(rewardsCommissionApplierStrategy.getCardType()).isEqualTo(CardType.REWARDS);
    }
}