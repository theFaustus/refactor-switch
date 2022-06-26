package inc.evil.refactorswitch.service;

import inc.evil.refactorswitch.domain.Category;
import inc.evil.refactorswitch.domain.Client;
import inc.evil.refactorswitch.domain.Product;
import inc.evil.refactorswitch.domain.ProductStatus;
import inc.evil.refactorswitch.exceptions.ProductNotFoundException;
import inc.evil.refactorswitch.exceptions.ProductPaymentException;
import inc.evil.refactorswitch.repo.ProductRepository;
import inc.evil.refactorswitch.service.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class PaymentServiceImpl implements PaymentService {
    private static final double REWARDS_COMMISSION = 0.016;
    private static final double REWARDS_DISCOUNT = 0.007;
    private static final double SECURED_COMMISSION = 0.0155;
    private static final double DEFAULT_COMMISSION = 0.015;
    private static final double LOW_INTEREST_COMMISSION = 0.0165;
    private static final double CASHBACK_COMMISSION = 0.016;
    private static final double CASHBACK_DISCOUNT = 0.006;
    private static final double STUDENT_SCHOOL_COMMISSION = 0.008;
    private static final double STUDENT_UNIVERSITY_COMMISSION = 0.012;
    private static final double TRAVEL_COMMISSION = 0.014;
    private static final double BUSINESS_COMMISSION = 0.017;
    private static final double BUSINESS_TAX = 0.003;
    private final ProductRepository productRepository;
    private final PaymentClient paymentClient;

    @Override
    public void payForProduct(Long productId, Client client) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(String.format("Product with id %s not found", productId)));

        double price = product.getPrice();
        double updatedPrice;
        switch (client.getCard().getType()) {
            case REWARDS -> {
                updatedPrice = price + (price * REWARDS_COMMISSION);
                log.info("Applied {} commission for client {} with card {} and price now = {} for product id {}",
                        REWARDS_COMMISSION, client.getId(), client.getCard().getType(), updatedPrice, productId);
                if (client.isRewardEligible()) {
                    updatedPrice = price - (price * REWARDS_DISCOUNT);
                    log.info("Applied {} discount for client {} with card {} and price now = {} for product id {}",
                            REWARDS_DISCOUNT, client.getId(), client.getCard().getType(), updatedPrice, productId);
                }
            }
            case SECURED -> {
                updatedPrice = price + (price * SECURED_COMMISSION);
                log.info("Applied {} commission for client {} with card {} and price now = {} for product id {}",
                        SECURED_COMMISSION, client.getId(), client.getCard().getType(), updatedPrice, productId);
            }
            case LOW_INTEREST -> {
                updatedPrice = price + (price * LOW_INTEREST_COMMISSION);
                log.info("Applied {} commission for client {} with card {} and price now = {} for product id {}",
                        LOW_INTEREST_COMMISSION, client.getId(), client.getCard().getType(), updatedPrice, productId);
            }
            case CASHBACK -> {
                updatedPrice = price + (price * CASHBACK_COMMISSION);
                log.info("Applied {} commission for client {} with card {} and price now = {} for product id {}",
                        CASHBACK_COMMISSION, client.getId(), client.getCard().getType(), updatedPrice, productId);
                if (client.getCard().isCashbackEnabled()) {
                    updatedPrice = price - (price * CASHBACK_DISCOUNT);
                    log.info("Applied {} discount for client {} with card {} and price now = {} for product id {}",
                            CASHBACK_DISCOUNT, client.getId(), client.getCard().getType(), updatedPrice, productId);
                }
            }
            case STUDENT -> {
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
            }
            case TRAVEL -> {
                if (product.getCategory() == Category.TRAVEL) {
                    updatedPrice = price + (price * TRAVEL_COMMISSION);
                    log.info("Applied {} commission for client {} with card {} and price now = {} for product id {}",
                            TRAVEL_COMMISSION, client.getId(), client.getCard().getType(), updatedPrice, productId);
                } else {
                    updatedPrice = price + (price * DEFAULT_COMMISSION);
                    log.info("Applied {} commission for client {} with card {} and price now = {} for product id {}",
                            DEFAULT_COMMISSION, client.getId(), client.getCard().getType(), updatedPrice, productId);
                }
            }
            case BUSINESS -> {
                updatedPrice = price + (price * BUSINESS_COMMISSION) + (price * BUSINESS_TAX);
                log.info("Applied {} commission and tax {} for client {} with card {} and price now = {} for product id {}",
                        BUSINESS_COMMISSION, BUSINESS_TAX, client.getId(), client.getCard().getType(), updatedPrice, productId);
            }
            default -> {
                updatedPrice = price + (price * DEFAULT_COMMISSION);
                log.info("Applied default commission {} for client {} with card {} and price now = {} for product id {}",
                        DEFAULT_COMMISSION, client.getId(), client.getCard().getType(), updatedPrice, productId);
            }
        }

        PaymentResponse paymentResponse = paymentClient.debitCard(client.getCard(), updatedPrice);
        if (paymentResponse.isSuccess()) {
            product.setStatus(ProductStatus.PAID);
            //process delivery, etc
            log.info("Product {} paid with success", productId);
        } else {
            //alert the user & other business logic
            log.warn("Product {} payment failed with [{}]", productId, paymentResponse.getErrorMessage());
            throw new ProductPaymentException(paymentResponse.getErrorMessage());
        }
    }

}
