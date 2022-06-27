package inc.evil.refactorswitch.service.commission;

import inc.evil.refactorswitch.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CashbackCommissionApplierStrategyImplTest {

    private CashbackCommissionApplierStrategyImpl cashbackCommissionApplierStrategy = new CashbackCommissionApplierStrategyImpl();
    private Product product;
    private Card card;
    private Client client;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "178-456-789", "PlayStation 5", 300.00, ProductStatus.IN_STOCK, Category.GAMING);
        card = new Card("Sponge Bob", "8945-7898-7895-7895", LocalDate.MAX, "123", CardType.CASHBACK, true);
        client = new Client("978456789", "Sponge", "bob", card, true, 19);
    }

    @Test
    void applyCommission_whenCardTypeCASHBACKAndCashbackEnabledTrue_appliesCASHBACK_COMISSIONAndDISCOUNT() {
        double updatedPrice = cashbackCommissionApplierStrategy.applyCommission(client, product);

        assertThat(updatedPrice).isEqualTo(298.2);
    }

    @Test
    void applyCommission_whenCardTypeCASHBACKAndCashbackEnabledFalse_appliesCASHBACK_COMISSION() {
        card.setCashbackEnabled(false);

        double updatedPrice = cashbackCommissionApplierStrategy.applyCommission(client, product);

        assertThat(updatedPrice).isEqualTo(304.8);
    }


    @Test
    void getCardType_returnsCASHBACK() {
        assertThat(cashbackCommissionApplierStrategy.getCardType()).isEqualTo(CardType.CASHBACK);
    }
}