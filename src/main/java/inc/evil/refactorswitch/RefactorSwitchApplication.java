package inc.evil.refactorswitch;

import inc.evil.refactorswitch.domain.Card;
import inc.evil.refactorswitch.domain.CardType;
import inc.evil.refactorswitch.domain.Client;
import inc.evil.refactorswitch.service.PaymentService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class RefactorSwitchApplication {

	public static void main(String[] args) {
		SpringApplication.run(RefactorSwitchApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(PaymentService paymentService) {
		return args -> {
			Card card = new Card("Sponge Bob", "8945-7898-7895-7895", LocalDate.MAX, "123", CardType.TRAVEL, true);
			Client client = new Client("978456789", "Sponge", "bob", card, true, 19);
			paymentService.payForProduct(1L, client);
			card.setType(CardType.BUSINESS);
			paymentService.payForProduct(2L, client);
			card.setType(CardType.CASHBACK);
			paymentService.payForProduct(3L, client);
			card.setType(CardType.REWARDS);
			paymentService.payForProduct(4L, client);
		};
	}

}
