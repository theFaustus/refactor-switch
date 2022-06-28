package inc.evil.refactorswitch.service;

import inc.evil.refactorswitch.domain.*;
import inc.evil.refactorswitch.exceptions.ProductNotFoundException;
import inc.evil.refactorswitch.exceptions.ProductPaymentException;
import inc.evil.refactorswitch.repo.ProductRepository;
import inc.evil.refactorswitch.service.dto.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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

    private PaymentService paymentService;
    private Product product;
    private Card card;
    private Client client;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(productRepository, paymentClient);
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
    void payForProduct_whenProductFoundAndCardTypeREWARDSAndRewardEligibleTrue_appliesREWARDS_COMISSIONAndDISCOUNT() {
        card.setType(CardType.REWARDS);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(paymentClient.debitCard(eq(card), any(Double.class))).thenReturn(new PaymentResponse(""));

        paymentService.payForProduct(PRODUCT_ID, client);

        ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        verify(paymentClient).debitCard(eq(card), priceCaptor.capture());
        assertThat(priceCaptor.getValue()).isEqualTo(297.9);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.PAID);
    }

    @Test
    void payForProduct_whenProductFoundAndCardTypeREWARDSAndRewardEligibleFalse_appliesREWARDS_COMISSION() {
        card.setType(CardType.REWARDS);
        client.setRewardEligible(false);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(paymentClient.debitCard(eq(card), any(Double.class))).thenReturn(new PaymentResponse(""));

        paymentService.payForProduct(PRODUCT_ID, client);

        ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        verify(paymentClient).debitCard(eq(card), priceCaptor.capture());
        assertThat(priceCaptor.getValue()).isEqualTo(304.8);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.PAID);
    }

    @Test
    void payForProduct_whenProductFoundAndCardTypeSECURED_appliesSECURED_COMISSION() {
        card.setType(CardType.SECURED);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(paymentClient.debitCard(eq(card), any(Double.class))).thenReturn(new PaymentResponse(""));

        paymentService.payForProduct(PRODUCT_ID, client);

        ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        verify(paymentClient).debitCard(eq(card), priceCaptor.capture());
        assertThat(priceCaptor.getValue()).isEqualTo(304.65);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.PAID);
    }

    @Test
    void payForProduct_whenProductFoundAndCardTypeLOW_INTEREST_appliesLOW_INTEREST_COMISSION() {
        card.setType(CardType.LOW_INTEREST);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(paymentClient.debitCard(eq(card), any(Double.class))).thenReturn(new PaymentResponse(""));

        paymentService.payForProduct(PRODUCT_ID, client);

        ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        verify(paymentClient).debitCard(eq(card), priceCaptor.capture());
        assertThat(priceCaptor.getValue()).isEqualTo(304.95);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.PAID);
    }

    @Test
    void payForProduct_whenProductFoundAndCardTypeCASHBACKAndCashbackEnabledTrue_appliesCASHBACK_COMISSIONAndDISCOUNT() {
        card.setType(CardType.CASHBACK);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(paymentClient.debitCard(eq(card), any(Double.class))).thenReturn(new PaymentResponse(""));

        paymentService.payForProduct(PRODUCT_ID, client);

        ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        verify(paymentClient).debitCard(eq(card), priceCaptor.capture());
        assertThat(priceCaptor.getValue()).isEqualTo(298.2);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.PAID);
    }

    @Test
    void payForProduct_whenProductFoundAndCardTypeCASHBACKAndCashbackEnabledFalse_appliesCASHBACK_COMISSION() {
        card.setType(CardType.CASHBACK);
        card.setCashbackEnabled(false);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(paymentClient.debitCard(eq(card), any(Double.class))).thenReturn(new PaymentResponse(""));

        paymentService.payForProduct(PRODUCT_ID, client);

        ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        verify(paymentClient).debitCard(eq(card), priceCaptor.capture());
        assertThat(priceCaptor.getValue()).isEqualTo(304.8);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.PAID);
    }

    @Test
    void payForProduct_whenProductFoundAndCardTypeSTUDENTAndAge19_appliesSTUDENT_SCHOOL_COMISSION() {
        card.setType(CardType.STUDENT);
        client.setAge(19);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(paymentClient.debitCard(eq(card), any(Double.class))).thenReturn(new PaymentResponse(""));

        paymentService.payForProduct(PRODUCT_ID, client);

        ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        verify(paymentClient).debitCard(eq(card), priceCaptor.capture());
        assertThat(priceCaptor.getValue()).isEqualTo(302.4);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.PAID);
    }

    @Test
    void payForProduct_whenProductFoundAndCardTypeSTUDENTAndAge22_appliesSTUDENT_UNIVERSITY_COMISSION() {
        card.setType(CardType.STUDENT);
        client.setAge(22);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(paymentClient.debitCard(eq(card), any(Double.class))).thenReturn(new PaymentResponse(""));

        paymentService.payForProduct(PRODUCT_ID, client);

        ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        verify(paymentClient).debitCard(eq(card), priceCaptor.capture());
        assertThat(priceCaptor.getValue()).isEqualTo(303.6);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.PAID);
    }

    @Test
    void payForProduct_whenProductFoundAndCardTypeSTUDENTAndAge27_appliesDEFAULT_COMISSION() {
        card.setType(CardType.STUDENT);
        client.setAge(27);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(paymentClient.debitCard(eq(card), any(Double.class))).thenReturn(new PaymentResponse(""));

        paymentService.payForProduct(PRODUCT_ID, client);

        ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        verify(paymentClient).debitCard(eq(card), priceCaptor.capture());
        assertThat(priceCaptor.getValue()).isEqualTo(304.5);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.PAID);
    }

    @Test
    void payForProduct_whenProductFoundAndCardTypeTRAVELAndProductCategoryTRAVEL_appliesTRAVEL_COMISSION() {
        card.setType(CardType.TRAVEL);
        product.setCategory(Category.TRAVEL);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(paymentClient.debitCard(eq(card), any(Double.class))).thenReturn(new PaymentResponse(""));

        paymentService.payForProduct(PRODUCT_ID, client);

        ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        verify(paymentClient).debitCard(eq(card), priceCaptor.capture());
        assertThat(priceCaptor.getValue()).isEqualTo(304.2);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.PAID);
    }

    @Test
    void payForProduct_whenProductFoundAndCardTypeTRAVELAndProductCategoryNotTRAVEL_appliesDEFAULT_COMISSION() {
        card.setType(CardType.TRAVEL);
        product.setCategory(Category.GAMING);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(paymentClient.debitCard(eq(card), any(Double.class))).thenReturn(new PaymentResponse(""));

        paymentService.payForProduct(PRODUCT_ID, client);

        ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        verify(paymentClient).debitCard(eq(card), priceCaptor.capture());
        assertThat(priceCaptor.getValue()).isEqualTo(304.5);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.PAID);
    }

    @Test
    void payForProduct_whenProductFoundAndCardTypeBUSINESS_appliesBUSINESS_COMISSIONAndTAX() {
        card.setType(CardType.BUSINESS);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(paymentClient.debitCard(eq(card), any(Double.class))).thenReturn(new PaymentResponse(""));

        paymentService.payForProduct(PRODUCT_ID, client);

        ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        verify(paymentClient).debitCard(eq(card), priceCaptor.capture());
        assertThat(priceCaptor.getValue()).isEqualTo(306.0);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.PAID);
    }

    @Test
    void payForProduct_whenProductFoundAndCardTypeSUBPRIME_appliesDEFAULT_COMISSION() {
        card.setType(CardType.SUBPRIME);
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(paymentClient.debitCard(eq(card), any(Double.class))).thenReturn(new PaymentResponse(""));

        paymentService.payForProduct(PRODUCT_ID, client);

        ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        verify(paymentClient).debitCard(eq(card), priceCaptor.capture());
        assertThat(priceCaptor.getValue()).isEqualTo(304.5);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.PAID);
    }
}