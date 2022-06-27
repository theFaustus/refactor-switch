package inc.evil.refactorswitch.service;

import inc.evil.refactorswitch.domain.*;
import inc.evil.refactorswitch.exceptions.ProductNotFoundException;
import inc.evil.refactorswitch.exceptions.ProductPaymentException;
import inc.evil.refactorswitch.repo.ProductRepository;
import inc.evil.refactorswitch.service.commission.CommissionApplierStrategy;
import inc.evil.refactorswitch.service.commission.DefaultCommissionApplierStrategyImpl;
import inc.evil.refactorswitch.service.dto.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    public static final long PRODUCT_ID = 1L;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PaymentClient paymentClient;
    @Mock
    private CommissionApplierStrategy commissionApplierStrategy;
    @Mock
    private DefaultCommissionApplierStrategyImpl defaultCommissionApplierStrategy;

    private PaymentService paymentService;
    private Product product;
    private Card card;
    private Client client;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(productRepository, paymentClient, List.of(commissionApplierStrategy), defaultCommissionApplierStrategy);
        product = new Product(1L, "178-456-789", "PlayStation 5", 300.00, ProductStatus.IN_STOCK, Category.GAMING);
        card = new Card("Sponge Bob", "8945-7898-7895-7895", LocalDate.MAX, "123", CardType.TRAVEL, true);
        client = new Client("978456789", "Sponge", "bob", card, true, 19);
    }

    @Test
    void payForProduct_whenProductNotFound_throwsProductNotFoundException() {
        assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> paymentService.payForProduct(PRODUCT_ID, client))
                .withMessage("Product with id 1 not found");
    }

    @Test
    void payForProduct_whenPaymentResponseErrorMessageNotEmpty_throwProductPaymentException() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(paymentClient.debitCard(eq(card), any(Double.class))).thenReturn(new PaymentResponse("Error 3015: Insufficient funds."));

        assertThatExceptionOfType(ProductPaymentException.class)
                .isThrownBy(() -> paymentService.payForProduct(PRODUCT_ID, client))
                .withMessage("Error 3015: Insufficient funds.");
    }

    @Test
    void payForProduct_whenProductFoundAndCardTypeLowInterest_usesLOW_INTEREST_COMMISSION() {
        card.setType(CardType.LOW_INTEREST);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(paymentClient.debitCard(eq(card), any(Double.class))).thenReturn(new PaymentResponse(""));
        when(commissionApplierStrategy.applyCommission(client, product)).thenReturn(300.00);
        when(commissionApplierStrategy.getCardType()).thenReturn(CardType.LOW_INTEREST);

        paymentService.payForProduct(PRODUCT_ID, client);

        ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        verify(paymentClient).debitCard(eq(card), priceCaptor.capture());
        assertThat(priceCaptor.getValue()).isEqualTo(300.0);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.PAID);
    }

    @Test
    void payForProduct_whenProductFoundAndCardTypeSubprime_usesDEFAULT_COMMISSION() {
        card.setType(CardType.SUBPRIME);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(paymentClient.debitCard(eq(card), any(Double.class))).thenReturn(new PaymentResponse(""));
        when(defaultCommissionApplierStrategy.applyCommission(client, product)).thenReturn(200.00);

        paymentService.payForProduct(PRODUCT_ID, client);

        ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        verify(paymentClient).debitCard(eq(card), priceCaptor.capture());
        assertThat(priceCaptor.getValue()).isEqualTo(200.0);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.PAID);
    }

}